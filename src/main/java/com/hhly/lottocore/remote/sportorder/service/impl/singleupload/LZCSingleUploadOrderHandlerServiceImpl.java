package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.remote.sportorder.service.impl.singleupload.AbstractSingleUploadOrderHandler;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.common.SingleUploadEnum.EncodingType;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.OldFootballConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.CalculatorUtil;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.MathUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.SingleUploadJCBetDetailVO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBetDetail;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 老足彩单式上传。校验+组装标准下单格式
 * @date 2017/6/14 12:13
 * @company 益彩网络科技公司
 */
@Service("lzcSingleUploadOrderHandlerServiceImpl")
public class LZCSingleUploadOrderHandlerServiceImpl extends  AbstractSingleUploadOrderHandler implements SingleUploadOrderHandlerService {

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    /**
     * 老足彩单式上传基本校验
     * @return
     */
    public ResultBO<?> baseValidate(OrderInfoSingleUploadVO orderInfoSingleUploadVO)throws ResultJsonException {
    	ResultBO<?> resultBO = userInfoCacheService.checkToken(orderInfoSingleUploadVO.getToken());
        if (resultBO.isError())
            return resultBO;
        
        UserInfoBO userInfo = (UserInfoBO) resultBO.getData();
        orderInfoSingleUploadVO.setUserId(userInfo.getId());
        return super.baseValidate(orderInfoSingleUploadVO);
    }

    /**
     * 老足彩组装标准的下单格式
     * @param orderInfoSingleUploadVO
     * @return
     * @throws ResultJsonException
     */
    public ResultBO<?> transferOrderInfoVO(OrderInfoSingleUploadVO orderInfoSingleUploadVO, QiniuUploadVO qiniuUploadVO)throws ResultJsonException {
        orderInfoSingleUploadVO.setPlayCode(JCZQConstants.getShortName(orderInfoSingleUploadVO.getLotteryChildCode()));
        //1.设置订单明细信息
        if(Constants.NUM_1 == orderInfoSingleUploadVO.getIsPageModify()){//入参方式组装
            ResultBO<?> resultBO = setStandardBetContent(orderInfoSingleUploadVO);
            if (resultBO.isError()){
                return resultBO;
            }
            if(ObjectUtil.isBlank(orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList())){
                return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
            }
        }else if(Constants.NUM_2 == orderInfoSingleUploadVO.getIsPageModify()){//解析文件组装内容
            ResultBO<?> resultBO = parseFileGetOrderDetails(orderInfoSingleUploadVO,qiniuUploadVO);
            if (resultBO.isError()){
                return resultBO;
            }
        }
        //2.组装标准格式
        return super.transferOrderInfoVO(orderInfoSingleUploadVO);
    }


    /**
     * 解析文件获取订单明细
     * @param orderInfoSingleUploadVO
     * @return
     */
    private ResultBO<?> parseFileGetOrderDetails(OrderInfoSingleUploadVO orderInfoSingleUploadVO, QiniuUploadVO qiniuUploadVO) {
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
        //2. 获取文件正确的行数
        resultBO = getSingleUploadSuccessList(orderInfoSingleUploadVO, uploadDatas);
        if(resultBO.isError()){
            return resultBO;
        }

        List<SingleUploadSuccessResultBO> successList = (List<SingleUploadSuccessResultBO>)resultBO.getData();
        //3.组装标准的投注明细，包括betcontent投注内容，并以此计算金额
        if(!ObjectUtil.isBlank(successList)){
            resultBO = setOrderDetailSingleUploadList(orderInfoSingleUploadVO, successList);
            if (resultBO.isError()){
                return resultBO;
            }
        }else {
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        return ResultBO.ok();
    }

    /**
     * 获取文件正确的行
     * @param orderInfoSingleUploadVO
     * @param uploadDatas
     * @return
     */
    private ResultBO<?> getSingleUploadSuccessList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, String[] uploadDatas) {
        List<SingleUploadSuccessResultBO> successList = new ArrayList<SingleUploadSuccessResultBO>();
        ResultBO<?> resultBO = getStandarTransfer(orderInfoSingleUploadVO.getShiftContent(), orderInfoSingleUploadVO.getPlayCode());
        if(resultBO.isError()){
            return resultBO;
        }
        SingleUploadJCVO lzcSingleUploadVO = new SingleUploadJCVO();
        lzcSingleUploadVO.setLotteryCode(orderInfoSingleUploadVO.getLotteryCode());
        lzcSingleUploadVO.setShiftContent(orderInfoSingleUploadVO.getShiftContent());
        lzcSingleUploadVO.setLotteryChildCode(orderInfoSingleUploadVO.getLotteryChildCode());
        resultBO = getStandarTransfer(lzcSingleUploadVO.getShiftContent(), OldFootballConstants.getShortName(lzcSingleUploadVO.getLotteryCode()));
        if(resultBO.isError()){
            return resultBO;
        }
        Map<String, String> mapTransfer = (Map<String,String>)resultBO.getData();
        lzcSingleUploadVO.setTransfer(mapTransfer);
        Map<String,SingleUploadSuccessResultBO> successResult = new ConcurrentHashMap<String,SingleUploadSuccessResultBO>();
        if(!ObjectUtil.isBlank(uploadDatas)){
            for(int i = 0; i < uploadDatas.length; i++){
                if(successResult.keySet().contains(uploadDatas[i])){
                    SingleUploadSuccessResultBO successResultBO = new SingleUploadSuccessResultBO(i+1, uploadDatas[i], null,
                            successResult.get(uploadDatas[i]).getMultiple(), successResult.get(uploadDatas[i]).getCount());
                    successResultBO.setBetDetails(successResult.get(uploadDatas[i]).getBetDetails());
                    successList.add(successResultBO);
                    continue;
                }
                resultBO = super.verify(lzcSingleUploadVO, uploadDatas[i], i+1);
                if(resultBO.isError()){
                    continue;
                }
                SingleUploadSuccessResultBO successResultBO = (SingleUploadSuccessResultBO)resultBO.getData();
                successList.add(successResultBO);
                successResult.put(uploadDatas[i],successResultBO);
            }
        }
        return ResultBO.ok(successList);
    }


    /**
     * 设置订单明细信息
     * @param orderInfoSingleUploadVO
     * @param successList
     * @return
     */
    private ResultBO<?> setOrderDetailSingleUploadList(OrderInfoSingleUploadVO orderInfoSingleUploadVO, List<SingleUploadSuccessResultBO> successList) {
        List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOList = new ArrayList<OrderDetailSingleUploadVO>();
        Double totalAmount=0d;
        List<SingleUploadSuccessResultBO> unUseResult = new ArrayList<SingleUploadSuccessResultBO>();
        for(SingleUploadSuccessResultBO singleUploadSuccessResultBO : successList){
            OrderDetailSingleUploadVO orderDetailSingleUploadVO = new OrderDetailSingleUploadVO();
            orderDetailSingleUploadVO.setM(singleUploadSuccessResultBO.getMultiple());
            List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList = new ArrayList<SingleUploadJCBetDetailVO>();
            for(SingleUploadJCBetDetail singleUploadJCBetDetail: singleUploadSuccessResultBO.getBetDetails()){
                if (singleUploadJCBetDetail.getLotteryResult().equals(SymbolConstants.NUMBER_SIGN) || singleUploadJCBetDetail.getLotteryResult().equals(SymbolConstants.STAR)) {
                    continue;
                }
                SingleUploadJCBetDetailVO singleUploadJCBetDetailVO = new SingleUploadJCBetDetailVO();
                singleUploadJCBetDetailVO.setLr(singleUploadJCBetDetail.getLotteryResult());
                singleUploadJCBetDetailVOList.add(singleUploadJCBetDetailVO);
            }
            orderDetailSingleUploadVO.setSupjcdl(singleUploadJCBetDetailVOList);
            orderDetailSingleUploadVO.setL_c_c(orderInfoSingleUploadVO.getLotteryChildCode());
            //获取标准的投注内容格式
            ResultBO<?> resultBO = getStandardBetContent(orderDetailSingleUploadVO.getSupjcdl());
            if(resultBO.isError()){
                unUseResult.add(singleUploadSuccessResultBO);
                continue;

            }
            String betContent = (String)resultBO.getData();
            orderDetailSingleUploadVO.setS_p_c(betContent);
            orderDetailSingleUploadVO.setP_c(singleUploadSuccessResultBO.getBettingContent());
            int betNum = Constants.NUM_1;//老足彩单式上传都是一注 SportsZsUtil.getSportsManyNote(betContent, orderInfoSingleUploadVO.getLotteryCode());
            orderDetailSingleUploadVO.setB_n(betNum);
            Double amount = CalculatorUtil.calculateAmount(betNum,singleUploadSuccessResultBO.getMultiple(),Constants.NUM_2);
            totalAmount = MathUtil.add(amount,totalAmount);
            orderDetailSingleUploadVO.setA(amount);
            orderDetailSingleUploadVO.setC_t(OrderEnum.BetContentType.SINGLE.getValue());
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
     * @return
     */
    private ResultBO<?> setStandardBetContent(OrderInfoSingleUploadVO orderInfoSingleUploadVO) {
        List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOS = orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList();
        List<OrderDetailSingleUploadVO> unUseOrderDetails = new ArrayList<OrderDetailSingleUploadVO>();
        for(OrderDetailSingleUploadVO orderDetailSingleUploadVO : orderDetailSingleUploadVOS){
            List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOS = orderDetailSingleUploadVO.
                    getSupjcdl();
            if(ObjectUtil.isBlank(singleUploadJCBetDetailVOS)){
                return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
            }
            ResultBO<?> resultBO = getStandardBetContent(orderDetailSingleUploadVO.getSupjcdl());
            if(resultBO.isError()){
                unUseOrderDetails.add(orderDetailSingleUploadVO);
                continue;

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
     * 组装老足彩入库标准内容（14场/任九）
     * @return
     */
    private ResultBO<?> getStandardBetContent(List<SingleUploadJCBetDetailVO> singleUploadJCBetDetailVOList){
        if(ObjectUtil.isBlank(singleUploadJCBetDetailVOList)){
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        StringBuffer buffer = new StringBuffer();
        for(SingleUploadJCBetDetailVO singleUploadJCBetDetailVO : singleUploadJCBetDetailVOList){
            if(SymbolConstants.NUMBER_SIGN.equals(singleUploadJCBetDetailVO.getLr()) || SymbolConstants.STAR.equals(singleUploadJCBetDetailVO.getLr())){
                buffer.append(SymbolConstants.UNDERLINE).append(SymbolConstants.VERTICAL_BAR);
            }else{
                buffer.append(singleUploadJCBetDetailVO.getLr()).append(SymbolConstants.VERTICAL_BAR);
            }

        }
        String betContent = StringUtil.interceptEndSymbol(buffer.toString(),SymbolConstants.VERTICAL_BAR);
        return ResultBO.ok(betContent);
    }




}
