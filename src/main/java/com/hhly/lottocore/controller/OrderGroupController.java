package com.hhly.lottocore.controller;

import com.hhly.lottocore.persistence.group.po.OrderGroupContentPO;
import com.hhly.lottocore.remote.ordergroup.service.IOrderGroupService;
import com.hhly.lottocore.remote.ordergroup.service.OrderGroupContentService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupContentVO;

import com.hhly.skeleton.lotto.base.group.vo.OrderGroupInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 合买
 * @date 2018/5/4 9:43
 * @company 益彩网络科技公司
 */
@RestController
@RequestMapping("/orderGroup")
public class OrderGroupController {

    private static Logger logger = LoggerFactory.getLogger(OrderGroupController.class);

    @Autowired
    private OrderGroupContentService orderGroupContentService;

    @Autowired
    private IOrderGroupService iOrderGroupService;


    /**
     * 跟单插入 跟单信息表（和支付对接）
     * @return
     */
    @RequestMapping(value = "/addOrderGroupContent",method = RequestMethod.POST)
    public ResultBO addOrderGroupContent(@RequestBody OrderGroupContentVO orderGroupContentVO){
         if(ObjectUtil.isBlank(orderGroupContentVO) || ObjectUtil.isBlank(orderGroupContentVO.getOrderCode()) || ObjectUtil.isBlank(orderGroupContentVO.getBuyAmount()) || ObjectUtil.isBlank(orderGroupContentVO.getUserId())){
             return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
         }
         try {
             return orderGroupContentService.insertOrderGroupContent(orderGroupContentVO);
         }catch (Exception e){
             logger.error("插入合买跟单信息失败！",e);
             return ResultBO.err();
         }
    }

    /**
     * 发起合买支付成功后，调用
     * @param orderGroupInfoVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addGroupContent",method = RequestMethod.POST)
    public ResultBO<?> addGroupContent(@RequestBody OrderGroupInfoVO orderGroupInfoVO)throws Exception{
        return iOrderGroupService.addOrderGroupContent(orderGroupInfoVO.getOrderCode());
    }

}
