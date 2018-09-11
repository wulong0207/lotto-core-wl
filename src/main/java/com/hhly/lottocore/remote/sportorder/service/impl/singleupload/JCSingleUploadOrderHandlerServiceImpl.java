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
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCConstants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.CalculatorUtil;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.base.util.sportsutil.SportsZsUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.SingleUploadJCBetDetailVO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBetDetail;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDaoBO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 竞彩足球单式上传。校验+组装标准下单格式
 * @date 2017/6/14 12:13
 * @company 益彩网络科技公司
 */
@Service("jcSingleUploadOrderHandlerServiceImpl")
public class JCSingleUploadOrderHandlerServiceImpl extends  AbstractSingleUploadOrderHandler implements SingleUploadOrderHandlerService {

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private IJcDataService jcDataService;

    /**
     * 竞彩足球单式上传基本校验
     * @return
     */
    public ResultBO<?> baseValidate(OrderInfoSingleUploadVO orderInfoSingleUploadVO)throws ResultJsonException {
        if (ObjectUtil.isBlank(orderInfoSingleUploadVO.getIsChooseMatch())) {
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        if (Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()) {//页面选择场次时，选择场次信息和系统编号串必传
            if (ObjectUtil.isBlank(orderInfoSingleUploadVO.getSelectedMatchs()) || ObjectUtil.isBlank(orderInfoSingleUploadVO.getBuyScreen())) {
                return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
            }
        }

    	ResultBO<?> resultBO = userInfoCacheService.checkToken(orderInfoSingleUploadVO.getToken());
        if (resultBO.isError())
            return resultBO;
        
        UserInfoBO userInfo = (UserInfoBO) resultBO.getData();
        orderInfoSingleUploadVO.setUserId(userInfo.getId());
        return super.baseValidate(orderInfoSingleUploadVO);
    }

    /**
     * 竞彩足球组装标准的下单格式
     * @param orderInfoSingleUploadVO
     * @return
     * @throws ResultJsonException
     */
    public ResultBO<?> transferOrderInfoVO(OrderInfoSingleUploadVO orderInfoSingleUploadVO, QiniuUploadVO qiniuUploadVO)throws ResultJsonException {
        orderInfoSingleUploadVO.setPlayCode(JCZQConstants.getShortName(orderInfoSingleUploadVO.getLotteryChildCode()));
        Map<String,JczqDaoBO> systemToMatch = new HashMap<String,JczqDaoBO>();
        //1.设置订单明细信息
        if(Constants.NUM_1 == orderInfoSingleUploadVO.getIsPageModify()){//入参方式组装
            ResultBO<?> resultBO = setStandardBetContent(orderInfoSingleUploadVO, systemToMatch);
            if (resultBO.isError()){
                return resultBO;
            }
            if(ObjectUtil.isBlank(orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList())){
                return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
            }
        }else if(Constants.NUM_2 == orderInfoSingleUploadVO.getIsPageModify()){//解析文件组装内容
            ResultBO<?> resultBO = parseFileGetOrderDetails(orderInfoSingleUploadVO, systemToMatch, qiniuUploadVO);
            if (resultBO.isError()){
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
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     */
    private void setBuyScreen(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, JczqDaoBO> systemToMatch) {
        Set<String> jczqDaoBOSet = systemToMatch.keySet();
        if(!ObjectUtil.isBlank(jczqDaoBOSet)){
            StringBuffer systemCodes = new StringBuffer();
            for(String key : jczqDaoBOSet){
                systemCodes.append(systemToMatch.get(key).getSystemCode()).append(SymbolConstants.COMMA);
            }
            String sysytems = StringUtil.interceptEndSymbol(systemCodes.toString(),SymbolConstants.COMMA);
            orderInfoSingleUploadVO.setBuyScreen(sysytems);
        }
    }

    /**
     * 解析文件获取订单明细
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     * @return
     */
    private ResultBO<?> parseFileGetOrderDetails(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, JczqDaoBO> systemToMatch, QiniuUploadVO qiniuUploadVO) {
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
        
        if(ObjectUtil.isBlank(uploadDatas)){
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        //2.1选择场次，获取成功的行数
        if(Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()){
            //获取校验成功的内容
            resultBO = getChooseSingleUploadSucList(orderInfoSingleUploadVO, uploadDatas);
            if (resultBO.isError()){
                return resultBO;
            }
        //2.2文件包含场次，获取成功的行数
        }else if(Constants.NUM_2 == orderInfoSingleUploadVO.getIsChooseMatch()){
            //获取校验成功的内容
            resultBO = getUnChooseSingleUploadSucList(orderInfoSingleUploadVO, uploadDatas);
            if(resultBO.isError()){
                return resultBO;
            }
        }
        List<SingleUploadSuccessResultBO> successList = (List<SingleUploadSuccessResultBO>)resultBO.getData();
        //3.组装标准的投注明细，包括betcontent投注内容，并以此计算注数,金额
        if(!ObjectUtil.isBlank(successList)){
            resultBO = setOrderDetailSingleUploadList(orderInfoSingleUploadVO, systemToMatch, successList);
            if (resultBO.isError()){
                return resultBO;
            }
        }else {
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        return ResultBO.ok();
    }

    /**
     * 文件包含场次获取成功上传的信息
     * @param orderInfoSingleUploadVO
     * @param uploadDatas
     * @return
     */
    private ResultBO<?> getUnChooseSingleUploadSucList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, String[] uploadDatas) {
        Map<String,String> shiftMap = null;
        List<SingleUploadSuccessResultBO> successList = new ArrayList<SingleUploadSuccessResultBO>();
        if(!ObjectUtil.isBlank(orderInfoSingleUploadVO.getShiftContent())){
            ResultBO<?> shiftResult  =  super.getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(),orderInfoSingleUploadVO.getPlayCode());
            if(shiftResult.isOK()){
                shiftMap = (Map<String, String>)shiftResult.getData();
            }else{
                return shiftResult;
            }
        }
        int length = getLength(orderInfoSingleUploadVO.getLotteryCode(),orderInfoSingleUploadVO.getLotteryChildCode());
        // 内容验证正确List
        for (int i=0;i<uploadDatas.length;i++) {
            ResultBO<?> verifyResult = super.verifySingleUploadContent(uploadDatas[i], orderInfoSingleUploadVO.getPlayCode(),length,shiftMap, orderInfoSingleUploadVO.getLotteryCode());
            if (verifyResult.isError()) {
                continue;
            } else {
                successList.add((SingleUploadSuccessResultBO)verifyResult.getData());
            }
        }
        return ResultBO.ok(successList);
    }

    /**
     * 解析文件 选择场次 获取成功的记录
     * @param orderInfoSingleUploadVO
     * @param uploadDatas
     * @return
     */
    private ResultBO<?> getChooseSingleUploadSucList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, String[] uploadDatas) {
        List<SingleUploadSuccessResultBO> successList = new ArrayList<SingleUploadSuccessResultBO>();
        ResultBO<?> resultBO = getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(), orderInfoSingleUploadVO.getPlayCode());
        if(resultBO.isError()){
            return resultBO;
        }
        SingleUploadJCVO jczqSingleUploadVO = new SingleUploadJCVO();
        jczqSingleUploadVO.setLotteryCode(orderInfoSingleUploadVO.getLotteryCode());
        jczqSingleUploadVO.setPlayCode(orderInfoSingleUploadVO.getPlayCode());
        jczqSingleUploadVO.setShiftContent(orderInfoSingleUploadVO.getShiftContent());
        jczqSingleUploadVO.setSelectedMatchs(orderInfoSingleUploadVO.getSelectedMatchs());
        jczqSingleUploadVO.setLotteryChildCode(orderInfoSingleUploadVO.getLotteryChildCode());
        //jczqSingleUploadVO.setPlayCode(orderInfoSingleUploadVO.getPlayCode());
        resultBO = getStandarTransfer(jczqSingleUploadVO.getShiftContent(), JCZQConstants.getShortName(jczqSingleUploadVO.getLotteryChildCode()));
        if(resultBO.isError()){
            return resultBO;
        }
        Map<String, String> mapTransfer = (Map<String,String>)resultBO.getData();
        jczqSingleUploadVO.setTransfer(mapTransfer);

        if(!ObjectUtil.isBlank(uploadDatas)){
            for(int i = 0; i < uploadDatas.length; i++){
                resultBO = super.verify(jczqSingleUploadVO, uploadDatas[i], i+1);
                if(resultBO.isError()){
                    continue;
                }
                SingleUploadSuccessResultBO successResultBO = (SingleUploadSuccessResultBO)resultBO.getData();
                successList.add(successResultBO);
            }
        }
        return ResultBO.ok(successList);
    }

    /**
     * 设置订单明细信息
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     * @param successList
     * @return
     */
    private ResultBO<?> setOrderDetailSingleUploadList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, JczqDaoBO> systemToMatch, List<SingleUploadSuccessResultBO> successList) {
        List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOList = new ArrayList<OrderDetailSingleUploadVO>();
        Double totalAmount=0d;
        List<SingleUploadSuccessResultBO> unUseResult = new ArrayList<SingleUploadSuccessResultBO>();
        for(SingleUploadSuccessResultBO singleUploadSuccessResultBO : successList){
            OrderDetailSingleUploadVO orderDetailSingleUploadVO = new OrderDetailSingleUploadVO();
            orderDetailSingleUploadVO.setB_s(singleUploadSuccessResultBO.getParlay());
            orderDetailSingleUploadVO.setM(singleUploadSuccessResultBO.getMultiple());
            List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList = new ArrayList<SingleUploadJCBetDetailVO>();
            for(SingleUploadJCBetDetail singleUploadJCBetDetail: singleUploadSuccessResultBO.getBetDetails()){
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
                    orderDetailSingleUploadVO.getB_s(),orderDetailSingleUploadVO.getL_c_c(),orderDetailSingleUploadVO.getM(),systemToMatch);
            if(resultBO.isError()){
                if(Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()){//赛事信息有误，选择场次的，直接返回，文件包含场次的继续下一个
                    return resultBO;
                }else{
                    unUseResult.add(singleUploadSuccessResultBO);
                    continue;
                }
            }
            String betContent = (String)resultBO.getData();
            orderDetailSingleUploadVO.setS_p_c(betContent);
            orderDetailSingleUploadVO.setP_c(singleUploadSuccessResultBO.getBettingContent());
            int betNum = SportsZsUtil.getSportsManyNote(betContent, orderDetailSingleUploadVO.getL_c_c());
            orderDetailSingleUploadVO.setB_n(betNum);
            Double amount = CalculatorUtil.calculateAmount(betNum,singleUploadSuccessResultBO.getMultiple(),Constants.NUM_2);
            totalAmount = MathUtil.add(amount,totalAmount);
            orderDetailSingleUploadVO.setA(amount);
            //orderDetailSingleUploadVO.setSupjcdl(singleUploadJCBetDetailVOList);
            if(betNum==1){//单式
                orderDetailSingleUploadVO.setC_t(OrderEnum.BetContentType.SINGLE.getValue());
            }else{
                orderDetailSingleUploadVO.setC_t(OrderEnum.BetContentType.MULTIPLE.getValue());
            }
            orderDetailSingleUploadVOList.add(orderDetailSingleUploadVO);
        }

        if(!ObjectUtil.isBlank(unUseResult)){
            successList.removeAll(unUseResult);
        }
        if(ObjectUtil.isBlank(successList)){
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        orderInfoSingleUploadVO.setOrderDetailSingleUploadVOList(orderDetailSingleUploadVOList);
        orderInfoSingleUploadVO.setOrderAmount(MathUtil.mul(totalAmount,orderInfoSingleUploadVO.getMultipleNum()));
        return ResultBO.ok();
    }

    /**
     * 设置标准的投注内容
     * @param orderInfoSingleUploadVO
     * @param systemToMatch
     * @return
     */
    private ResultBO<?> setStandardBetContent(OrderInfoSingleUploadVO orderInfoSingleUploadVO, Map<String, JczqDaoBO> systemToMatch) {
        List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOS = orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList();
        List<OrderDetailSingleUploadVO> unUseOrderDetails = new ArrayList<OrderDetailSingleUploadVO>();
        for(OrderDetailSingleUploadVO orderDetailSingleUploadVO : orderDetailSingleUploadVOS){
            List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOS = orderDetailSingleUploadVO.getSupjcdl();
            if(ObjectUtil.isBlank(singleUploadJCBetDetailVOS)){
                return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
            }
            ResultBO<?> resultBO = getStandardBetContent(orderDetailSingleUploadVO.getSupjcdl(),
                    orderDetailSingleUploadVO.getB_s(),orderDetailSingleUploadVO.getL_c_c(),orderDetailSingleUploadVO.getM(),systemToMatch);
            if(resultBO.isError()){
                if(Constants.NUM_1 == orderInfoSingleUploadVO.getIsChooseMatch()){//赛事信息有误，选择场次的，直接返回，文件包含场次的继续下一个
                    return resultBO;
                }else{
                    unUseOrderDetails.add(orderDetailSingleUploadVO);
                    continue;
                }
            }
            orderDetailSingleUploadVO.setS_p_c((String)resultBO.getData());
        }
        if(!ObjectUtil.isBlank(unUseOrderDetails)){
            orderDetailSingleUploadVOS.removeAll(unUseOrderDetails);
        }
        orderInfoSingleUploadVO.setOrderDetailSingleUploadVOList(orderDetailSingleUploadVOS);
        return ResultBO.ok();
    }

    /**
     * 上传文件
     * @param textList
     * @return
     * @throws ResultJsonException
     */
    @Override
    public ResultBO<?> saveFile(List<String> textList,String fileUrl) throws ResultJsonException {
        return super.saveFile(textList,fileUrl);
    }


    /**
     * 组装入库标准的投注内容
     * @return
     */
    private ResultBO<?> getStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList,String bunchStr,
                                         Integer lotteryChildCode,Integer multiple,Map<String,JczqDaoBO> systemToMatch){
        ResultBO<?> resultBO = null;
        List<SingleUploadJCBetDetailVO> list = super.resetSingleUploadJCBetDetailVOList(singleUploadJCBetDetailVOList);
        switch(lotteryChildCode){
            case JCZQConstants.ID_JCZQ://胜平负
            case JCZQConstants.ID_FBF://比分
            case JCZQConstants.ID_FZJQ://总进球
            case JCZQConstants.ID_FBCQ://半全场
                resultBO = getZQStandardBetContent(list, bunchStr, multiple, systemToMatch);
                if(resultBO.isError()){
                    return resultBO;
                }
                resultBO = ResultBO.ok(resultBO.getData());
                break;
            case JCZQConstants.ID_RQS://让球胜平负
                resultBO = getZQRQSPFStandardBetContent(list, bunchStr, multiple, systemToMatch);
                if(resultBO.isError()){
                    return resultBO;
                }
                resultBO = ResultBO.ok(resultBO.getData());
                break;
            case JCZQConstants.ID_FHT://混投
                resultBO = getZQHTStandardBetContent(list, bunchStr, multiple, systemToMatch);
                if(resultBO.isError()){
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
     * 足球胜平负，总进球，全场比分，半全场胜负
     * 组装入库时的标准投注内容。SP值为1
     * @return
     */
    private ResultBO<?> getZQStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList,String bunchStr,Integer multiple,Map<String,JczqDaoBO> systemToMatch){
        StringBuffer buffer = new StringBuffer();
        //161128001(3@1)|161128002(0@1)|161128003(0@1)^3_1
        //1.对阵+投注内容
        for(int i=1;i<=singleUploadJCBetDetailVOList.size();i++){
            String officeMatchCode = singleUploadJCBetDetailVOList.get(i-1).getSn();
            if (ObjectUtil.isBlank(officeMatchCode) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN + SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.STAR)) {//赛事编号没有选择，说明这场赛事是没有选的
                 continue;
            }
            String systemCode = "";
            if(systemToMatch.containsKey(officeMatchCode)){
                systemCode = systemToMatch.get(officeMatchCode).getSystemCode();
            }else{
                JczqDaoBO jczqDaoBO = jcDataService.findSingleUpMatchDataByOfficialCode(officeMatchCode);
                if(!ObjectUtil.isBlank(jczqDaoBO)){
                    systemCode = jczqDaoBO.getSystemCode();
                    systemToMatch.put(officeMatchCode,jczqDaoBO);
                }else{
                    return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
                }
            }
            if(i<singleUploadJCBetDetailVOList.size()){
                buffer.append(systemCode).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT).append(SymbolConstants.VERTICAL_BAR);
            }else{
                buffer.append(systemCode).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT);
            }
        }
        //buyScreen设值systemCode，文件包含场次时这个字段要设值。
        //2.串关，倍数
        buffer.append(SymbolConstants.UP_CAP).append(bunchStr).append(SymbolConstants.UP_CAP).append(multiple);
       return ResultBO.ok(buffer.toString());
    }

    /**
     * 足球让球胜平负
     * 组装入库时的标准投注内容。SP值为1.00
     * @return
     */
    private ResultBO<?> getZQRQSPFStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList,String bunchStr,Integer multiple,Map<String,JczqDaoBO> systemToMatch){
        StringBuffer buffer = new StringBuffer();
        //161128001[+1](3@1.57)|161128002[+1](0@4.21)|161128003[+1](0@4.21)^3_1
        //1.对阵+投注内容
        for(int i=1;i<=singleUploadJCBetDetailVOList.size();i++){
            String officeMatchCode = singleUploadJCBetDetailVOList.get(i-1).getSn();
            if (ObjectUtil.isBlank(officeMatchCode) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.STAR)) {//赛事编号没有选择，说明这场赛事是没有选的
                continue;
            }
            String systemCode = "";
            String letNum = "";
            if(systemToMatch.containsKey(officeMatchCode)){
                systemCode = systemToMatch.get(officeMatchCode).getSystemCode();
                letNum = String.valueOf(systemToMatch.get(officeMatchCode).getNewestLetNum());
            }else{
                JczqDaoBO jczqDaoBO = jcDataService.findSingleUpMatchDataByOfficialCode(officeMatchCode);
                if(!ObjectUtil.isBlank(jczqDaoBO)){
                    systemCode = jczqDaoBO.getSystemCode();
                    letNum = String.valueOf(jczqDaoBO.getNewestLetNum());
                    systemToMatch.put(officeMatchCode,jczqDaoBO);
                }else {
                    return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
                }
            }
            if(NumberUtil.isNumeric(letNum)){
                letNum = SymbolConstants.ADD+letNum;
            }
            if(i<singleUploadJCBetDetailVOList.size()){
                buffer.append(systemCode).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNum).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT)
                        .append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT).append(SymbolConstants.VERTICAL_BAR);
            }else{
                buffer.append(systemCode).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNum).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT)
                        .append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                        append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT);
            }
        }
        //2.串关，倍数
        buffer.append(SymbolConstants.UP_CAP).append(bunchStr).append(SymbolConstants.UP_CAP).append(multiple);
        return ResultBO.ok(buffer.toString());
    }

    /**
     * 足球混合投注(只支持胜平负和让球胜平负的混投)
     * 组装入库时的标准投注内容。SP值为1
     * @return
     */
    private ResultBO<?> getZQHTStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList,String bunchStr,Integer multiple,Map<String,JczqDaoBO> systemToMatch){
        StringBuffer buffer = new StringBuffer();
        //161128001_R[+1](3@1.57,0@2.27)_S(1@1.89,0@4.21)^2_1,3_1^868
        //1.对阵+投注内容
        for(int i=1;i<=singleUploadJCBetDetailVOList.size();i++){
            String officeMatchCode = singleUploadJCBetDetailVOList.get(i-1).getSn();
            if (ObjectUtil.isBlank(officeMatchCode) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetailVOList.get(i - 1).getLr().equals(SymbolConstants.STAR)) {//赛事编号没有选择，说明这场赛事是没有选的
                continue;
            }
            if(ObjectUtil.isBlank(singleUploadJCBetDetailVOList.get(i-1).getPlay())){//混投要带玩法标识，因为要区分胜平负和让胜平负
                return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
            }
            String systemCode = "";
            String letNum = "";
            if(systemToMatch.containsKey(officeMatchCode)){
                systemCode = systemToMatch.get(officeMatchCode).getSystemCode();
                letNum = String.valueOf(systemToMatch.get(officeMatchCode).getNewestLetNum());
            }else{
                JczqDaoBO jczqDaoBO = jcDataService.findSingleUpMatchDataByOfficialCode(officeMatchCode);
                if(!ObjectUtil.isBlank(jczqDaoBO)){
                    systemCode = jczqDaoBO.getSystemCode();
                    letNum = String.valueOf(jczqDaoBO.getNewestLetNum());
                    systemToMatch.put(officeMatchCode,jczqDaoBO);
                }else{
                    return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
                }
            }
            if(NumberUtil.isNumeric(letNum)){
                letNum = SymbolConstants.ADD+letNum;
            }
            if(i<singleUploadJCBetDetailVOList.size()){
                if(JCConstants.S.equals(singleUploadJCBetDetailVOList.get(i-1).getPlay()) ){//胜平负
                    buffer.append(systemCode).append(SymbolConstants.UNDERLINE).append(JCConstants.S).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                            append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT).append(SymbolConstants.VERTICAL_BAR);
                }else if (JCConstants.R.equals(singleUploadJCBetDetailVOList.get(i-1).getPlay())){//让球胜平负
                    buffer.append(systemCode).append(SymbolConstants.UNDERLINE).append(JCConstants.R).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNum).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT).
                            append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                            append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT).append(SymbolConstants.VERTICAL_BAR);
                }
            }else{
                if(JCConstants.S.equals(singleUploadJCBetDetailVOList.get(i-1).getPlay()) ){//胜平负
                    buffer.append(systemCode).append(SymbolConstants.UNDERLINE).append(JCConstants.S).append(SymbolConstants.PARENTHESES_LEFT).append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                            append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT);
                }else if (JCConstants.R.equals(singleUploadJCBetDetailVOList.get(i-1).getPlay())){//让球胜平负
                    buffer.append(systemCode).append(SymbolConstants.UNDERLINE).append(JCConstants.R).append(SymbolConstants.MIDDLE_PARENTHESES_LEFT).append(letNum).append(SymbolConstants.MIDDLE_PARENTHESES_RIGHT).append(SymbolConstants.PARENTHESES_LEFT).
                            append(singleUploadJCBetDetailVOList.get(i-1).getLr()).
                            append(SymbolConstants.AT).append(Constants.DOUBLE_1).append(SymbolConstants.PARENTHESES_RIGHT);
                }
            }
        }
        //2.串关，倍数
        buffer.append(SymbolConstants.UP_CAP).append(bunchStr).append(SymbolConstants.UP_CAP).append(multiple);
        return ResultBO.ok(buffer.toString());
    }



}
