package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailSingleUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoSingleUploadVO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 单式上传总入口
 * @date 2017/6/14 12:04
 * @company 益彩网络科技公司
 */
@Component("singleUploadOrderExcetor")
public class SingleUploadOrderExecutor {

    private static Logger logger = LoggerFactory.getLogger(SingleUploadOrderExecutor.class);

    /*竞彩单式上传*/
    @Autowired
    private SingleUploadOrderHandlerService jcSingleUploadOrderHandlerServiceImpl;

    @Autowired
    private SingleUploadOrderHandlerService lzcSingleUploadOrderHandlerServiceImpl;

    @Autowired
    @Qualifier("bjSingleUploadOrderHandlerService")
    private SingleUploadOrderHandlerService bjSingleUploadOrderHandlerService;
    
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
     * 单式上传处理业务入口
     * @param orderInfoSingleUploadVO
     * @return
     * @throws ResultJsonException
     */
    public ResultBO<?> execute(OrderInfoSingleUploadVO orderInfoSingleUploadVO)throws ResultJsonException{
        if(ObjectUtil.isBlank(orderInfoSingleUploadVO) || ObjectUtil.isBlank(orderInfoSingleUploadVO.getLotteryChildCode())){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        Integer lotteryCode =Integer.valueOf(
                String.valueOf(orderInfoSingleUploadVO.getLotteryChildCode()).substring(Constants.NUM_0, Constants.NUM_3));
        orderInfoSingleUploadVO.setLotteryCode(lotteryCode);
        //1.单式上传校验
        SingleUploadOrderHandlerService singleUploadOrderHandlerService = getSingleUploadOrderHandler(orderInfoSingleUploadVO.getLotteryChildCode());
        ResultBO<?> resultBO =  singleUploadOrderHandlerService.baseValidate(orderInfoSingleUploadVO);
        if(resultBO.isError()){
            return  resultBO;
        }
        //2.组装标准下单格式内容
        String orgFileURL = orderInfoSingleUploadVO.getFileURL();
        String newFileURL = orgFileURL.replace("_1_","_2_");
        orderInfoSingleUploadVO.setNewFileURL(newFileURL);
        
        QiniuUploadVO qiniuUploadVO = new QiniuUploadVO(accessKey, secretKey, bucketName, uploadLimit, fileType, savePath, Long.parseLong(limitSize));
		qiniuUploadVO.setUploadURL(uploadURL);
		
        resultBO = singleUploadOrderHandlerService.transferOrderInfoVO(orderInfoSingleUploadVO, qiniuUploadVO);
        if(resultBO.isError()){
            return  resultBO;
        }
        //3.上传文件
        //上传之前都是原始文件，立即投注是第二个文件，都是创建新的
        ResultBO<?> resultBO1 = singleUploadOrderHandlerService.saveFile(getBetContent(orderInfoSingleUploadVO.getOrderDetailSingleUploadVOList()),newFileURL);
        if(resultBO1.isError()){
            return resultBO1;
        }
        return resultBO;
    }

    private List<String> getBetContent(List<OrderDetailSingleUploadVO> orderDetailSingleUploadVOList){
        List<String> contents = new ArrayList<String>();
         for(OrderDetailSingleUploadVO orderDetailSingleUploadVO : orderDetailSingleUploadVOList){
             contents.add(orderDetailSingleUploadVO.getP_c());
         }
         return contents;
    }

    /**
     * 根据彩种获取业务处理类
     * @param lotteryChildCode
     * @return
     */
    private SingleUploadOrderHandlerService getSingleUploadOrderHandler(Integer lotteryChildCode){
        Integer lotteryCode = Integer.valueOf(String.valueOf(lotteryChildCode).substring(Constants.NUM_0, Constants.NUM_3));
        LotteryEnum.Lottery lottery = LotteryEnum.Lottery.getLottery(lotteryCode);
        Assert.notNull(lottery, "40502");
        switch (lottery) {
            case FB:
                return jcSingleUploadOrderHandlerServiceImpl;
            case SFC:
            case ZC_NINE:
                return lzcSingleUploadOrderHandlerServiceImpl;
            case BJDC:
            case SFGG:
                return bjSingleUploadOrderHandlerService;
            default:
                throw new ResultJsonException(ResultBO.err("40502"));
        }
    }




}
