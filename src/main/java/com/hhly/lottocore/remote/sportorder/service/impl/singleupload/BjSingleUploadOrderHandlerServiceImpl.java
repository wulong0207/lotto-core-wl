package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.impl.singleupload.AbstractSingleUploadOrderHandler;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.common.SingleUploadEnum.EncodingType;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.CalculatorUtil;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.base.util.sportsutil.SportsZsUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.SingleUploadJCBetDetailVO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBetDetail;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author lgs on
 * @version 1.0
 * @desc 北单单式上传 。校验+组装标准下单格式
 * @date 2017/8/4.
 * @company 益彩网络科技有限公司
 */
@Service("bjSingleUploadOrderHandlerService")
public class BjSingleUploadOrderHandlerServiceImpl extends AbstractSingleUploadOrderHandler implements SingleUploadOrderHandlerService {


    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private IJcDataService jcDataService;
    
    /**
     * 单式上传基本校验
     *
     * @param orderInfoSingleUploadVO
     * @return
     */
    @Override
    public ResultBO<?> baseValidate(OrderInfoSingleUploadVO orderInfoSingleUploadVO) throws ResultJsonException {
        if (ObjectUtil.isBlank(orderInfoSingleUploadVO.getIsChooseMatch())) {
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        if (Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()) {//页面选择场次时，选择场次信息和系统编号串必传
            if (ObjectUtil.isBlank(orderInfoSingleUploadVO.getSelectedMatchs()) || ObjectUtil.isBlank(orderInfoSingleUploadVO.getBuyScreen())) {
                return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
            }
        }

        ResultBO<?> resultBO = userInfoCacheService.checkToken(orderInfoSingleUploadVO.getToken());
        if (resultBO.isError()) {
            return resultBO;
        }
        UserInfoBO userInfo = (UserInfoBO) resultBO.getData();
        orderInfoSingleUploadVO.setUserId(userInfo.getId());
        return super.baseValidate(orderInfoSingleUploadVO);
    }

    /**
     * 北京单场组装标准的下单格式
     *
     * @param orderInfoSingleUploadVO
     * @return
     * @throws ResultJsonException
     */
    @Override
    public ResultBO<?> transferOrderInfoVO(OrderInfoSingleUploadVO orderInfoSingleUploadVO, QiniuUploadVO qiniuUploadVO) throws ResultJsonException {
        orderInfoSingleUploadVO.setPlayCode(BJDCConstants.getShortName(orderInfoSingleUploadVO.getLotteryChildCode()));
        Map<String, BjDaoBO> systemToMatch = new HashMap<>();
        //1.设置订单明细信息
        if (Constants.NUM_1 == orderInfoSingleUploadVO.getIsPageModify()) {//入参方式组装
            ResultBO<?> resultBO = setStandardBetContent(orderInfoSingleUploadVO, systemToMatch);
            if (resultBO.isError()) {
                return resultBO;
            }
            if (ObjectUtil.isBlank(orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList())) {
                return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
            }
        } else if (Constants.NUM_2 == orderInfoSingleUploadVO.getIsPageModify()) {//解析文件组装内容
            ResultBO<?> resultBO = parseFileGetOrderDetails(orderInfoSingleUploadVO, systemToMatch, qiniuUploadVO);
            if (resultBO.isError()) {
                return resultBO;
            }
        }
        //2.文件包含不选择的场次，前端传的buyScreen就不行了。所以都需要重新取值
        //if(Constants.NUM_2 == orderInfoSingleUploadVO.getIsChooseMatch()){
        setBuyScreen(orderInfoSingleUploadVO, systemToMatch);
        //}
        //3.组装标准格式
        return super.transferOrderInfoVO(orderInfoSingleUploadVO);
    }


    /**
     * 文件包含场次系统编号需要自己设置
     *
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     */
    private void setBuyScreen(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, BjDaoBO> systemToMatch) {
        Set<String> jczqDaoBOSet = systemToMatch.keySet();
        if (!ObjectUtil.isBlank(jczqDaoBOSet)) {
            StringBuffer systemCodes = new StringBuffer();
            for (String key : jczqDaoBOSet) {
                systemCodes.append(systemToMatch.get(key).getSystemCode()).append(SymbolConstants.COMMA);
            }
            String sysytems = StringUtil.interceptEndSymbol(systemCodes.toString(), SymbolConstants.COMMA);
            orderInfoSingleUploadVO.setBuyScreen(sysytems);
        }
    }

    /**
     * 解析文件获取订单明细
     *
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     * @return
     */
    private ResultBO<?> parseFileGetOrderDetails(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, BjDaoBO> systemToMatch,QiniuUploadVO qiniuUploadVO) {
        ResultBO<?> resultBO = null;
        //1.读取文件
//        String[] uploadDatas = FileUtil.readFileLine(orderInfoSingleUploadVO.getFileURL(), SingleUploadEnum.EncodingType.UFT8.getShortName());
        
        /** 7牛云 */
		SingleUploadJCVO singleUploadJCVO = new SingleUploadJCVO();
		singleUploadJCVO.setFilePath(orderInfoSingleUploadVO.getFileURL());

        resultBO = FileUtil.readFileFromQiniu(singleUploadJCVO, qiniuUploadVO,  EncodingType.GBK.getShortName());
        if(resultBO.isError()){
            return resultBO;
        }
        List<String> ret = (List<String>)resultBO.getData();
        String[] uploadDatas = ret.toArray(new String[ret.size()]);
        /** 7牛云 */
        
        if (ObjectUtil.isBlank(uploadDatas)) {
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        //2.1选择场次，获取成功的行数
        if (Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()) {
            //获取校验成功的内容
            resultBO = getChooseSingleUploadSucList(orderInfoSingleUploadVO, uploadDatas);
            if (resultBO.isError()) {
                return resultBO;
            }
            //2.2文件包含场次，获取成功的行数
        } else if (Constants.NUM_2 == orderInfoSingleUploadVO.getIsChooseMatch()) {
            //获取校验成功的内容
            resultBO = getUnChooseSingleUploadSucList(orderInfoSingleUploadVO, uploadDatas);
            if (resultBO.isError()) {
                return resultBO;
            }
        }
        List<SingleUploadSuccessResultBO> successList = (List<SingleUploadSuccessResultBO>) resultBO.getData();
        //3.组装标准的投注明细，包括betcontent投注内容，并以此计算注数,金额
        if (!ObjectUtil.isBlank(successList)) {
            resultBO = setOrderDetailSingleUploadList(orderInfoSingleUploadVO, systemToMatch, successList);
            if (resultBO.isError()) {
                return resultBO;
            }
        } else {
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        return ResultBO.ok();
    }

    /**
     * 文件包含场次获取成功上传的信息
     *
     * @param orderInfoSingleUploadVO
     * @param uploadDatas
     * @return
     */
    private ResultBO<?> getUnChooseSingleUploadSucList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, String[] uploadDatas) {
        Map<String, String> shiftMap = null;
        List<SingleUploadSuccessResultBO> successList = new ArrayList<>();
        if (!ObjectUtil.isBlank(orderInfoSingleUploadVO.getShiftContent())) {
            ResultBO<?> shiftResult = null;//super.getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(), orderInfoSingleUploadVO.getPlayCode());
            if(orderInfoSingleUploadVO.getPlayCode().equals("Q")){
            	shiftResult = getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(),"BQ");
    		}else{
    			shiftResult = getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(),orderInfoSingleUploadVO.getPlayCode());
    		}
            if (shiftResult.isOK()) {
                shiftMap = (Map<String, String>) shiftResult.getData();
            } else {
                return shiftResult;
            }
        }
        int length = getLength(orderInfoSingleUploadVO.getLotteryCode(), orderInfoSingleUploadVO.getLotteryChildCode());
        // 内容验证正确List
        for (int i = 0; i < uploadDatas.length; i++) {
            ResultBO<?> verifyResult = super.verifySingleUploadContent(uploadDatas[i], orderInfoSingleUploadVO.getPlayCode(), length, shiftMap, orderInfoSingleUploadVO.getLotteryCode());
            if (verifyResult.isError()) {
                continue;
            } else {
                successList.add((SingleUploadSuccessResultBO) verifyResult.getData());
            }
        }
        return ResultBO.ok(successList);
    }

    /**
     * 解析文件 选择场次 获取成功的记录
     *
     * @param orderInfoSingleUploadVO
     * @param uploadDatas
     * @return
     */
    private ResultBO<?> getChooseSingleUploadSucList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, String[] uploadDatas) {
        List<SingleUploadSuccessResultBO> successList = new ArrayList<>();
        ResultBO<?> resultBO = getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(), orderInfoSingleUploadVO.getPlayCode());
        if (resultBO.isError()) {
            return resultBO;
        }
        SingleUploadJCVO jczqSingleUploadVO = new SingleUploadJCVO();
        jczqSingleUploadVO.setLotteryCode(orderInfoSingleUploadVO.getLotteryCode());
        jczqSingleUploadVO.setPlayCode(orderInfoSingleUploadVO.getPlayCode());
        jczqSingleUploadVO.setShiftContent(orderInfoSingleUploadVO.getShiftContent());
        jczqSingleUploadVO.setSelectedMatchs(orderInfoSingleUploadVO.getSelectedMatchs());
        jczqSingleUploadVO.setLotteryChildCode(orderInfoSingleUploadVO.getLotteryChildCode());
        //jczqSingleUploadVO.setPlayCode(orderInfoSingleUploadVO.getPlayCode());
//        resultBO = getStandarTransfer(jczqSingleUploadVO.getShiftContent(), BJDCConstants.getShortName(jczqSingleUploadVO.getLotteryChildCode()));
		if(orderInfoSingleUploadVO.getPlayCode().equals("Q")){
			resultBO = getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(),"BQ");
		}else{
			resultBO = getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(),orderInfoSingleUploadVO.getPlayCode());
		}
        if (resultBO.isError()) {
            return resultBO;
        }
        Map<String, String> mapTransfer = (Map<String, String>) resultBO.getData();
        jczqSingleUploadVO.setTransfer(mapTransfer);

        if (!ObjectUtil.isBlank(uploadDatas)) {
            for (int i = 0; i < uploadDatas.length; i++) {
                resultBO = super.verify(jczqSingleUploadVO, uploadDatas[i], i + 1);
                if (resultBO.isError()) {
                    continue;
                }
                SingleUploadSuccessResultBO successResultBO = (SingleUploadSuccessResultBO) resultBO.getData();
                successList.add(successResultBO);
            }
        }

        return ResultBO.ok(successList);
    }

    /**
     * 设置订单明细信息
     *
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     * @param successList
     * @return
     */
    private ResultBO<?> setOrderDetailSingleUploadList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, BjDaoBO> systemToMatch, List<SingleUploadSuccessResultBO> successList) {
        List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOList = new ArrayList<>();
        Double totalAmount = 0d;
        List<SingleUploadSuccessResultBO> unUseResult = new ArrayList<>();
        for (SingleUploadSuccessResultBO singleUploadSuccessResultBO : successList) {
            OrderDetailSingleUploadVO orderDetailSingleUploadVO = new OrderDetailSingleUploadVO();
            orderDetailSingleUploadVO.setB_s(singleUploadSuccessResultBO.getParlay());
            orderDetailSingleUploadVO.setM(singleUploadSuccessResultBO.getMultiple());
            List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList = new ArrayList<>();
            for (SingleUploadJCBetDetail singleUploadJCBetDetail : singleUploadSuccessResultBO.getBetDetails()) {
                if (ObjectUtil.isBlank(singleUploadJCBetDetail.getSessionNumber()) || singleUploadJCBetDetail.getLotteryResult().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetail.getLotteryResult().equals(SymbolConstants.STAR)) {
                    continue;
                }
                SingleUploadJCBetDetailVO singleUploadJCBetDetailVO = new SingleUploadJCBetDetailVO();
                singleUploadJCBetDetailVO.setLr(singleUploadJCBetDetail.getLotteryResult());
                singleUploadJCBetDetailVO.setPlay(singleUploadJCBetDetail.getPlay());
                singleUploadJCBetDetailVO.setSn(singleUploadJCBetDetail.getSessionNumber());
                singleUploadJCBetDetailVOList.add(singleUploadJCBetDetailVO);
            }
            orderDetailSingleUploadVO.setSupjcdl(singleUploadJCBetDetailVOList);
            orderDetailSingleUploadVO.setL_c_c(orderInfoSingleUploadVO.getLotteryChildCode());
            //获取标准的投注内容格式
            ResultBO<?> resultBO = getStandardBetContent(orderDetailSingleUploadVO.getSupjcdl(),
                    orderDetailSingleUploadVO.getB_s(), orderDetailSingleUploadVO.getL_c_c(), orderDetailSingleUploadVO.getM(), systemToMatch, orderInfoSingleUploadVO.getLotteryCode().toString());
            if (resultBO.isError()) {
                if (Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()) {//赛事信息有误，选择场次的，直接返回，文件包含场次的继续下一个
                    return resultBO;
                } else {
                    unUseResult.add(singleUploadSuccessResultBO);
                    continue;
                }
            }
            String betContent = (String) resultBO.getData();
            orderDetailSingleUploadVO.setS_p_c(betContent);
            orderDetailSingleUploadVO.setP_c(singleUploadSuccessResultBO.getBettingContent());
            int betNum = SportsZsUtil.getSportsManyNote(betContent, orderInfoSingleUploadVO.getLotteryCode());
            orderDetailSingleUploadVO.setB_n(betNum);
            Double amount = CalculatorUtil.calculateAmount(betNum, singleUploadSuccessResultBO.getMultiple(), Constants.NUM_2);
            totalAmount = MathUtil.add(amount, totalAmount);
            orderDetailSingleUploadVO.setA(amount);
            //orderDetailSingleUploadVO.setSupjcdl(singleUploadJCBetDetailVOList);
            if (betNum == 1) {//单式
                orderDetailSingleUploadVO.setC_t(OrderEnum.BetContentType.SINGLE.getValue());
            } else {
                orderDetailSingleUploadVO.setC_t(OrderEnum.BetContentType.MULTIPLE.getValue());
            }
            orderDetailSingleUploadVOList.add(orderDetailSingleUploadVO);
        }

        if (!ObjectUtil.isBlank(unUseResult)) {
            successList.removeAll(unUseResult);
        }
        if (ObjectUtil.isBlank(successList)) {
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        orderInfoSingleUploadVO.setOrderDetailSingleUploadVOList(orderDetailSingleUploadVOList);
        orderInfoSingleUploadVO.setOrderAmount(MathUtil.mul(totalAmount, orderInfoSingleUploadVO.getMultipleNum()));
        return ResultBO.ok();
    }

    /**
     * 设置标准的投注内容
     *
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     * @return
     */
    private ResultBO<?> setStandardBetContent(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, BjDaoBO> systemToMatch) {
        List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOS = orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList();
        List<OrderDetailSingleUploadVO> unUseOrderDetails = new ArrayList<>();
        for (OrderDetailSingleUploadVO orderDetailSingleUploadVO : orderDetailSingleUploadVOS) {
            List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOS = orderDetailSingleUploadVO.getSupjcdl();
            if (ObjectUtil.isBlank(singleUploadJCBetDetailVOS)) {
                return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
            }
            ResultBO<?> resultBO = getStandardBetContent(orderDetailSingleUploadVO.getSupjcdl(),
                    orderDetailSingleUploadVO.getB_s(), orderDetailSingleUploadVO.getL_c_c(), orderDetailSingleUploadVO.getM(), systemToMatch, orderInfoSingleUploadVO.getLotteryCode().toString());
            if (resultBO.isError()) {
                if (Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()) {//赛事信息有误，选择场次的，直接返回，文件包含场次的继续下一个
                    return resultBO;
                } else {
                    unUseOrderDetails.add(orderDetailSingleUploadVO);
                    continue;
                }
            }
            orderDetailSingleUploadVO.setS_p_c((String) resultBO.getData());
        }
        if (!ObjectUtil.isBlank(unUseOrderDetails)) {
            orderDetailSingleUploadVOS.removeAll(unUseOrderDetails);
        }
        orderInfoSingleUploadVO.setOrderDetailSingleUploadVOList(orderDetailSingleUploadVOS);
        return ResultBO.ok();
    }

    /**
     * @param textList
     * @param fileUrl
     * @return
     * @throws ResultJsonException
     */
    @Override
    public ResultBO<?> saveFile(List<String> textList, String fileUrl) throws ResultJsonException {
        return super.saveFile(textList, fileUrl);
    }


    /**
     * 组装入库标准的投注内容
     *
     * @return
     */
    private ResultBO<?> getStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList, String bunchStr,
                                              Integer lotteryChildCode, Integer multiple, Map<String, BjDaoBO> systemToMatch, String lotteryCode) {
        ResultBO<?> resultBO = null;
        List<SingleUploadJCBetDetailVO> list = super.resetSingleUploadJCBetDetailVOList(singleUploadJCBetDetailVOList);
        switch (lotteryChildCode) {
            case BJDCConstants.ID_SFC://胜负过关
                resultBO = getSFStandardBetContent(list, bunchStr, multiple, systemToMatch, lotteryCode, lotteryChildCode);
                if (resultBO.isError()) {
                    return resultBO;
                }
                resultBO = ResultBO.ok(resultBO.getData());
                break;
            case BJDCConstants.ID_RQS://让球胜平负
                resultBO = getRQStandardBetContent(list, bunchStr, multiple, systemToMatch, lotteryCode, lotteryChildCode);
                if (resultBO.isError()) {
                    return resultBO;
                }
                resultBO = ResultBO.ok(resultBO.getData());
                break;
            case BJDCConstants.ID_FBF://比分
            case BJDCConstants.ID_FZJQ://总进球
            case BJDCConstants.ID_FBCQ://半全场
            case BJDCConstants.ID_SXDX://上下单双
                resultBO = getZQStandardBetContent(list, bunchStr, multiple, systemToMatch, lotteryCode);
                if (resultBO.isError()) {
                    return resultBO;
                }
                resultBO = ResultBO.ok(resultBO.getData());
                break;
            default:
                resultBO = ResultBO.ok("");
                break;
        }
        return resultBO;
    }


    /**
     * 北京单场 胜负过关
     * 组装入库时的标准投注内容。SP值为1
     *
     * @return
     */
    private ResultBO<?> getSFStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList, String bunchStr, Integer multiple, Map<String, BjDaoBO> systemToMatch, String lotteryCode, Integer lotteryChildCode) {
        StringBuffer buffer = new StringBuffer();
        //161128001[+1.5](3@1)|161128002[+1.5](0@1)|161128003[+1.5](0@1)^3_1
        //1.对阵+投注内容
        for (int i = 1; i <= singleUploadJCBetDetailVOList.size(); i++) {
            String bjNum = singleUploadJCBetDetailVOList.get(i - 1).getSn();
            if (ObjectUtil.isBlank(bjNum) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN + SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.STAR)) {//赛事编号没有选择，说明这场赛事是没有选的
                continue;
            }
            String systemCode = "";
            if (systemToMatch.containsKey(bjNum)) {
                systemCode = systemToMatch.get(bjNum).getSystemCode();
            } else {
                BjDaoBO bjDaoBO = jcDataService.findBjSingleDataByBjNum(bjNum, lotteryCode);
                if (!ObjectUtil.isBlank(bjDaoBO)) {
                    systemCode = bjDaoBO.getSystemCode();
                    systemToMatch.put(bjNum, bjDaoBO);
                } else {
                    return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
                }
            }

            BjDaoBO betBjDaoBO = systemToMatch.get(bjNum);

            double letNum = betBjDaoBO.getLetScore();

            String letNumBetContent = "";
            if (ObjectUtil.isBlank(letNum) || letNum > 0) {
                letNumBetContent = SymbolConstants.ADD + letNum;
            } else if (letNum == 0) {
                letNumBetContent = SymbolConstants.ADD + Constants.NUM_0;
            } else {
                letNumBetContent = String.valueOf(letNum);
            }

            if (i < singleUploadJCBetDetailVOList.size()) {
                buffer.append(systemCode).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNumBetContent).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i - 1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT).append(SymbolConstants.VERTICAL_BAR);
            } else {
                buffer.append(systemCode).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNumBetContent).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i - 1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT);
            }
        }
        //buyScreen设值systemCode，文件包含场次时这个字段要设值。
        //2.串关，倍数
        buffer.append(SymbolConstants.UP_CAP).append(bunchStr).append(SymbolConstants.UP_CAP).append(multiple);
        return ResultBO.ok(buffer.toString());
    }

    /**
     * 北京单场 让球胜平负
     * 组装入库时的标准投注内容。SP值为1
     *
     * @return
     */
    private ResultBO<?> getRQStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList, String bunchStr, Integer multiple, Map<String, BjDaoBO> systemToMatch, String lotteryCode, Integer lotteryChildCode) {
        StringBuffer buffer = new StringBuffer();
        //161128001[+0](3@1)|161128002[+0](0@1)|161128003[+0](0@1)^3_1
        //1.对阵+投注内容
        for (int i = 1; i <= singleUploadJCBetDetailVOList.size(); i++) {
            String bjNum = singleUploadJCBetDetailVOList.get(i - 1).getSn();
            if (ObjectUtil.isBlank(bjNum) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN + SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.STAR)) {//赛事编号没有选择，说明这场赛事是没有选的
                continue;
            }
            String systemCode = "";
            if (systemToMatch.containsKey(bjNum)) {
                systemCode = systemToMatch.get(bjNum).getSystemCode();
            } else {
                BjDaoBO bjDaoBO = jcDataService.findBjSingleDataByBjNum(bjNum, lotteryCode);
                if (!ObjectUtil.isBlank(bjDaoBO)) {
                    systemCode = bjDaoBO.getSystemCode();
                    systemToMatch.put(bjNum, bjDaoBO);
                } else {
                    return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
                }
            }

            BjDaoBO betBjDaoBO = systemToMatch.get(bjNum);

			int letNum = betBjDaoBO.getLetNum() == null ? 0 : betBjDaoBO.getLetNum().intValue();

            String letNumBetContent = "";
            if (ObjectUtil.isBlank(letNum) || letNum > 0) {
                letNumBetContent = SymbolConstants.ADD + letNum;
            } else if (letNum == 0) {
                letNumBetContent = SymbolConstants.ADD + Constants.NUM_0;
            } else {
                letNumBetContent = String.valueOf(letNum);
            }

            if (i < singleUploadJCBetDetailVOList.size()) {
                buffer.append(systemCode).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNumBetContent).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i - 1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT).append(SymbolConstants.VERTICAL_BAR);
            } else {
                buffer.append(systemCode).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNumBetContent).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i - 1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT);
            }
        }
        //buyScreen设值systemCode，文件包含场次时这个字段要设值。
        //2.串关，倍数
        buffer.append(SymbolConstants.UP_CAP).append(bunchStr).append(SymbolConstants.UP_CAP).append(multiple);
        return ResultBO.ok(buffer.toString());
    }

    /**
     * 北京单场
     * 组装入库时的标准投注内容。SP值为1
     *
     * @return
     */
    private ResultBO<?> getZQStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList, String bunchStr, Integer multiple, Map<String, BjDaoBO> systemToMatch, String lotteryCode) {
        StringBuffer buffer = new StringBuffer();
        //161128001(3@1)|161128002(0@1)|161128003(0@1)^3_1
        //1.对阵+投注内容
        for (int i = 1; i <= singleUploadJCBetDetailVOList.size(); i++) {
            String bjNum = singleUploadJCBetDetailVOList.get(i - 1).getSn();
            if (ObjectUtil.isBlank(bjNum) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN + SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.STAR)) {//赛事编号没有选择，说明这场赛事是没有选的
                continue;
            }
            String systemCode = "";
            if (systemToMatch.containsKey(bjNum)) {
                systemCode = systemToMatch.get(bjNum).getSystemCode();
            } else {
                BjDaoBO bjDaoBO = jcDataService.findBjSingleDataByBjNum(bjNum, lotteryCode);
                if (!ObjectUtil.isBlank(bjDaoBO)) {
                    systemCode = bjDaoBO.getSystemCode();
                    systemToMatch.put(bjNum, bjDaoBO);
                } else {
                    return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
                }
            }
            if (i < singleUploadJCBetDetailVOList.size()) {
                buffer.append(systemCode).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i - 1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT).append(SymbolConstants.VERTICAL_BAR);
            } else {
                buffer.append(systemCode).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i - 1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT);
            }
        }
        //buyScreen设值systemCode，文件包含场次时这个字段要设值。
        //2.串关，倍数
        buffer.append(SymbolConstants.UP_CAP).append(bunchStr).append(SymbolConstants.UP_CAP).append(multiple);
        return ResultBO.ok(buffer.toString());
    }

}
