package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.List;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoSingleUploadVO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc
 * @date 2017/6/14 12:22
 * @company 益彩网络科技公司
 */
public interface SingleUploadOrderHandlerService {
    /**
     * 单式上传基本校验
     * @return
     */
    ResultBO<?> baseValidate(OrderInfoSingleUploadVO orderInfoSingleUploadVO)throws ResultJsonException;


    /**
     * 组装标准的下单格式
     * @param orderInfoSingleUploadVO
     * @return
     * @throws ResultJsonException
     */
    ResultBO<?> transferOrderInfoVO(OrderInfoSingleUploadVO orderInfoSingleUploadVO, QiniuUploadVO qiniuUploadVO)throws ResultJsonException;

    /**
     *
     * @param textList
     * @param fileUrl
     * @return
     * @throws ResultJsonException
     */
    ResultBO<?> saveFile(List<String> textList,String fileUrl)throws ResultJsonException;



}
