package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SingleUploadEnum.EncodingType;
import com.hhly.skeleton.base.constants.*;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.qiniu.QiniuUpload;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.*;
import com.hhly.skeleton.lotto.base.order.vo.*;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;



import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;



import java.io.*;
import java.util.*;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 单式上传 1.单式上传特有的校验 2.组装下单标准格式
 * @date 2017/6/14 10:59
 * @company 益彩网络科技公司
 */
public class AbstractSingleUploadOrderHandler extends SingleUploadOrderValidateMethod {

    private static Logger logger = LoggerFactory.getLogger(AbstractSingleUploadOrderHandler.class);
    
    @Value("${single_upload_dir}")
	private String singleUploadDir;
   
    /** 7牛accessKey  **/
   	@Value("${accessKey}")
   	private String accessKey;
   	/** 7牛secretKey **/
   	@Value("${secretKey}")
   	private String secretKey;
   	/** bucketName **/
   	@Value("${bucketName}")
   	private String bucketName;
   	/** 允许批量上传文件数量  **/
   	@Value("${uploadLimit}")
   	private Integer uploadLimit;
   	/** 允许上传文件类型 **/
   	@Value("${fileType}")
   	private String fileType;
   	/** 文件访问路径  **/
   	@Value("${uploadURL}")
   	private String uploadURL;
   	/**域名和文件名中间的路径*/
   	@Value("${savePath}")
   	private String savePath;
   	/**允许批量上传文件大小*/
   	@Value("${limitSize}")
   	private String limitSize;

    /**
     * 单式上传基本校验
     *
     * @return
     */
    protected ResultBO<?> baseValidate(OrderInfoSingleUploadVO orderInfoSingleUploadVO) throws ResultJsonException {
        if (ObjectUtil.isBlank(orderInfoSingleUploadVO.getIsPageModify()) || ObjectUtil.isBlank(orderInfoSingleUploadVO.getLotteryChildCode())
                || ObjectUtil.isBlank(orderInfoSingleUploadVO.getMultipleNum())
                || ObjectUtil.isBlank(orderInfoSingleUploadVO.getLotteryIssue()) || ObjectUtil.isBlank(orderInfoSingleUploadVO.getBuyType())
                || ObjectUtil.isBlank(orderInfoSingleUploadVO.getFileURL()) || ObjectUtil.isBlank(orderInfoSingleUploadVO.getChannelId())) {
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        if (Constants.NUM_1 == orderInfoSingleUploadVO.getIsPageModify()) {//入参的方式
            if (ObjectUtil.isBlank(orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList())
                    || ObjectUtil.isBlank(orderInfoSingleUploadVO.getOrderAmount())) {
                return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
            }
        }

        //校验倍数,包括明细，最大50000倍
        if (orderInfoSingleUploadVO.getMultipleNum() > JCConstants.MAX_LIMIT_MULTIPLE) {
            return ResultBO.err(MessageCodeConstants.MULTIPLE_LIMIT_SERVICE);
        }
        if (!ObjectUtil.isBlank(orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList())) {
            for (OrderDetailSingleUploadVO orderDetailSingleUploadVO : orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList()) {
                if (orderDetailSingleUploadVO.getM() > JCConstants.MAX_LIMIT_MULTIPLE) {
                    return ResultBO.err(MessageCodeConstants.MULTIPLE_LIMIT_SERVICE);
                }
            }
        }


        SingleUploadJCVO singleUploadJCVO = new SingleUploadJCVO();
        singleUploadJCVO.setFilePath(orderInfoSingleUploadVO.getFileURL());
        orderInfoSingleUploadVO.setFileURL(orderInfoSingleUploadVO.getFileURL());
        if (orderInfoSingleUploadVO.getFileURL().indexOf("_1_") < 0) {//校验是否上传的文件名称是否规范
            return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        QiniuUploadVO qiniuUploadVO = new QiniuUploadVO(accessKey, secretKey, bucketName, uploadLimit, fileType, savePath, Long.parseLong(limitSize));
		qiniuUploadVO.setUploadURL(uploadURL);
        ResultBO<?> resultBO = FileUtil.readFileFromQiniu(singleUploadJCVO, qiniuUploadVO,  EncodingType.GBK.getShortName());
        if(resultBO.isError()){
            return resultBO;
        }
        List<String> ret = (List<String>)resultBO.getData();
        String[] readFile = ret.toArray(new String[ret.size()]);
        if(!ObjectUtil.isBlank(readFile) && readFile.length == 0){//校验文件是否已经上传
        	 return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
        return ResultBO.ok();
    }

    /**
     * 组装标准的下单格式
     *
     * @param orderInfoSingleUploadVO
     * @return
     * @throws ResultJsonException
     */
    protected ResultBO<?> transferOrderInfoVO(OrderInfoSingleUploadVO orderInfoSingleUploadVO) throws ResultJsonException {
        OrderInfoVO orderInfo = new OrderInfoVO();

        orderInfo.setBuyScreen(orderInfoSingleUploadVO.getBuyScreen());
        orderInfo.setBuyType(orderInfoSingleUploadVO.getBuyType());
        orderInfo.setChannelId(orderInfoSingleUploadVO.getChannelId());
        if(orderInfoSingleUploadVO.getLotteryCode() == JCZQConstants.ID_JCZQ_B){
            orderInfo.setLotteryCode(orderInfoSingleUploadVO.getLotteryChildCode());
        }else{
            orderInfo.setLotteryCode(orderInfoSingleUploadVO.getLotteryCode());
        }
        orderInfo.setLotteryIssue(orderInfoSingleUploadVO.getLotteryIssue());
        orderInfo.setMultipleNum(orderInfoSingleUploadVO.getMultipleNum());
        orderInfo.setOrderAmount(orderInfoSingleUploadVO.getOrderAmount());
        orderInfo.setPlatform(orderInfoSingleUploadVO.getPlatform());
        orderInfo.setToken(orderInfoSingleUploadVO.getToken());
        orderInfo.setTabType(Constants.NUM_1);//都是过关投注，没有2选1，北京单场等
        Integer isDltAdd = 0;
        orderInfo.setIsDltAdd(isDltAdd.shortValue());
        List<OrderDetailVO> orderDetailList = new ArrayList<OrderDetailVO>();
        List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOs = orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList();
        for (OrderDetailSingleUploadVO orderDetailSingleUploadVO : orderDetailSingleUploadVOs) {
            OrderDetailVO orderDetailVO = new OrderDetailVO();
            orderDetailVO.setAmount(orderDetailSingleUploadVO.getA());
            orderDetailVO.setBuyNumber(orderDetailSingleUploadVO.getB_n());
            orderDetailVO.setContentType(orderDetailSingleUploadVO.getC_t());
            orderDetailVO.setLotteryChildCode(orderDetailSingleUploadVO.getL_c_c());
            orderDetailVO.setMultiple(orderDetailSingleUploadVO.getM());
            orderDetailVO.setPlanContent(orderDetailSingleUploadVO.getS_p_c());
            orderDetailVO.setCodeWay(Constants.NUM_3);
            orderDetailList.add(orderDetailVO);
            if (orderInfoSingleUploadVO.getLotteryCode() == JCZQConstants.ID_JCZQ_B) {
                String[] betContentArr = FormatConversionJCUtil.stringSplitArray(orderDetailSingleUploadVO.getS_p_c(), SymbolConstants.VERTICAL_BAR, true);
                for(int j = 0; j < betContentArr.length; j++){
                    //赛事编号加到Set集
                    orderInfo.getMatchSet().add(betContentArr[j].substring(0, 10));
                }
            } else if (orderInfoSingleUploadVO.getLotteryCode() == BJDCConstants.ID_BJDC_B || orderInfoSingleUploadVO.getLotteryCode() == BJDCConstants.ID_SFGG_B) {
                String[] betContentArr = FormatConversionJCUtil.stringSplitArray(orderDetailSingleUploadVO.getS_p_c(), SymbolConstants.VERTICAL_BAR, true);
                for (int j = 0; j < betContentArr.length; j++) {
                    String betContent = null;
                    if (betContentArr[j].contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)) {
                        betContent = betContentArr[j].substring(0, betContentArr[j].indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT));
                    } else {
                        betContent = betContentArr[j].substring(0, betContentArr[j].indexOf(SymbolConstants.PARENTHESES_LEFT));
                    }

                    //赛事编号加到Set集
                    orderInfo.getMatchSet().add(betContent);
                }
            }
        }
        /*String os = System.getProperty("os.name");
        String filePath = orderInfoSingleUploadVO.getNewFileURL();
        if (!os.toLowerCase().startsWith("win")) {
            filePath = filePath.split(singleUploadDir)[1];
        }*/
        orderInfo.setBettingContentUrl(orderInfoSingleUploadVO.getNewFileURL());
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.setIsSingleOrder(Integer.valueOf(Constants.NUM_1).shortValue());
        orderInfo.setCategoryId(Integer.valueOf(Constants.NUM_6).shortValue());
        return ResultBO.ok(orderInfo);
    }

    /**
     * 保存文件到服务器，立即上传都是创建新的文件
     *
     * @param textList
     * @param fileUrl
     * @return
     * @throws ResultJsonException
     */
    protected ResultBO<?> saveFile(List<String> textList, String fileUrl) throws ResultJsonException {
        try {
            QiniuUploadVO qiniuUploadVO = new QiniuUploadVO(accessKey, secretKey, bucketName, uploadLimit, fileType, savePath, Long.parseLong(limitSize));
            qiniuUploadVO.setUploadURL(uploadURL);
            qiniuUploadVO.setFileRelativePath(fileUrl);
            QiniuUpload qiniuUpload = new QiniuUpload(qiniuUploadVO);
            // 以字节流数组流的方式上传
            StringBuilder b = new StringBuilder();
            for(String str : textList){
                b.append(str+SymbolConstants.NEW_LINE);
            }
            byte[] uploadBytes = b.toString().getBytes();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(uploadBytes);
            ResultBO<?> resultBO = qiniuUpload.uploadFileNotRename(byteInputStream);
            if(resultBO.isError()){
                return resultBO;
            }
        }catch (Exception e){
            logger.error("单式上传上传文件失败！", e);
            return ResultBO.err();
        }
        return ResultBO.ok();

    }


    /**
     * 重置list 去除# * 所包含对阵
     *
     * @param list
     */
    protected List<SingleUploadJCBetDetailVO> resetSingleUploadJCBetDetailVOList(List<SingleUploadJCBetDetailVO> list) {
        List<SingleUploadJCBetDetailVO> result = new ArrayList<SingleUploadJCBetDetailVO>();
        if (list.isEmpty()) {
            return result;
        }

        for (SingleUploadJCBetDetailVO vo : list) {
            if (vo.getLr().equals(SymbolConstants.NUMBER_SIGN) || vo.getLr().equals(SymbolConstants.NUMBER_SIGN + SymbolConstants.NUMBER_SIGN) || vo.getLr().equals(SymbolConstants.STAR)) {//赛事编号没有选择，说明这场赛事是没有选的
                continue;
            }
            result.add(vo);
        }
        return result;
    }
}
