package com.hhly.lottocore.remote.ordergroup.service.impl;

import com.hhly.lottocore.persistence.group.dao.OrderGroupMapper;
import com.hhly.lottocore.persistence.group.dao.OrderGroupUserMapper;
import com.hhly.lottocore.persistence.group.po.OrderGroupPO;
import com.hhly.lottocore.remote.ordergroup.service.OrderGroupService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderCopyEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.group.bo.OrderGroupDetailBO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupQueryVO;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author longguoyou
 * @Date  2018/4/28 15:16
 * @Since 1.8
 */
@Service("orderGroupService")
public class OrderGroupServiceImpl implements OrderGroupService{

    private static Logger logger = LoggerFactory.getLogger(OrderGroupServiceImpl.class);

    @Autowired
    private IPageService pageService;

    @Autowired
    private OrderGroupMapper orderGroupMapper;

    @Autowired
    private OrderGroupUserMapper orderGroupUserMapper;

    @Override
    public ResultBO<?> addOrderGroup(final OrderGroupPO orderGroupPO) throws Exception {
        //初始化相关字段值
        orderGroupPO.setGrpbuyStatus(1);
        orderGroupPO.setIsTop(0);
        orderGroupPO.setIsRecommend(0);
        orderGroupPO.setBuyCount(0);
        orderGroupPO.setProgress(0d);
        orderGroupPO.setProgressAmount(0d);
        return ResultBO.ok(orderGroupMapper.insert(orderGroupPO));
    }

    @Override
    public ResultBO<?> queryOrderGroupV11(final OrderGroupQueryVO orderGroupQueryVO) throws Exception {
        //判断
        Assert.paramNotNull(orderGroupQueryVO.getPageSize(), "pageSize");
        Assert.paramNotNull(orderGroupQueryVO.getPageIndex(), "pageIndex");
//        Assert.paramNotNull(queryVO.getOrderCode(), "orderCode");
        //设置查询条件
        setCondition(orderGroupQueryVO);
        //设置排序
        ResultBO<?> resultBO = setOrderBy(orderGroupQueryVO);
        if(resultBO.isError()){return resultBO;}
        //设值
        PagingBO<OrderGroupDetailBO> pageData = pageService.getPageData(orderGroupQueryVO,
                new ISimplePage<OrderGroupDetailBO>() {
                    @Override
                    public int getTotal() {
                        return orderGroupMapper.queryOrderGroupListCountV11(orderGroupQueryVO);
                    }

                    @Override
                    public List<OrderGroupDetailBO> getData() {
                        return orderGroupMapper.queryOrderGroupListV11(orderGroupQueryVO);
                    }
                });
        logger.info("合买二期 --> 查询合买大厅列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("合买二期 --> 查询合买大厅列表信息：detailList=" + pageData.getData().size() + " 条");
        for(OrderGroupDetailBO bean : pageData.getData()){
            if(!ObjectUtil.isBlank(bean.getTitle())){
                if(Base64.isBase64(bean.getTitle())){
                    bean.setTitle(new String(Base64.decodeBase64(bean.getTitle().getBytes("UTF-8"))));
                }
            }
            if(!ObjectUtil.isBlank(bean.getIsTop()) && !ObjectUtil.isBlank(bean.getIsRecommend())){
                bean.setIsRecommend(Constants.NUM_0);
            }
        }
        return ResultBO.ok(pageData);
    }

    @Override
    public ResultBO<?> queryOrderGroup(OrderGroupQueryVO orderGroupQueryVO) throws Exception {
        //判断
        Assert.paramNotNull(orderGroupQueryVO.getPageSize(), "pageSize");
        Assert.paramNotNull(orderGroupQueryVO.getPageIndex(), "pageIndex");
        orderGroupQueryVO.setSortField("b.grpbuy_status ASC, b.is_top DESC, b.is_recommend DESC, b.progress DESC, b.create_time");
        orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
        //设值
        PagingBO<OrderGroupDetailBO> pageData = pageService.getPageData(orderGroupQueryVO,
                new ISimplePage<OrderGroupDetailBO>() {
                    @Override
                    public int getTotal() {
                        return orderGroupMapper.queryOrderGroupListCount(orderGroupQueryVO);
                    }

                    @Override
                    public List<OrderGroupDetailBO> getData() {
                        return orderGroupMapper.queryOrderGroupList(orderGroupQueryVO);
                    }
                });
        logger.info("合买一期 --> 查询合买大厅列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("合买一期 --> 查询合买大厅列表信息：detailList=" + pageData.getData().size() + " 条");
        return ResultBO.ok(pageData);
    }

    /**
     * 设置查询条件
     * @param orderGroupQueryVO
     */
    private void setCondition(final OrderGroupQueryVO orderGroupQueryVO) {
        //PC端:方案金额orderAmount、合买进度progress、提成commission
        //移动端：合买进度progress、保底进度guarantee、提成commission
        if(!ObjectUtil.isBlank(orderGroupQueryVO.getOrderAmount())){//PC端合买订单金额
            //null=不限，1=100以下，2=100-499,3=500-999,4=1000-4999,5=5000以上
            if(orderGroupQueryVO.getOrderAmount() == Constants.NUM_1){
                orderGroupQueryVO.setOrderAmountDb("AND a.order_amount < 100");
            }else if(orderGroupQueryVO.getOrderAmount() == Constants.NUM_2){
                orderGroupQueryVO.setOrderAmountDb("AND a.order_amount >= 100 AND a.order_amount <= 499");
            }else if(orderGroupQueryVO.getOrderAmount() == Constants.NUM_3){
                orderGroupQueryVO.setOrderAmountDb("AND a.order_amount >= 500 AND a.order_amount <= 999");
            }else if(orderGroupQueryVO.getOrderAmount() == Constants.NUM_4){
                orderGroupQueryVO.setOrderAmountDb("AND a.order_amount >= 1000 AND a.order_amount <= 4999");
            }else if(orderGroupQueryVO.getOrderAmount() == Constants.NUM_5){
                orderGroupQueryVO.setOrderAmountDb("AND a.order_amount >= 5000");
            }
        }
        if(!ObjectUtil.isBlank(orderGroupQueryVO.getGuarantee())){//移动端保底
            //null=不限，1=全额保底，2=50%-90%，3=50%以下
            if(orderGroupQueryVO.getGuarantee() == Constants.NUM_1){
                orderGroupQueryVO.setGuaranteeDb("AND b.guarantee_ratio > 90");//全额保底的就是大于90%
            }else if(orderGroupQueryVO.getGuarantee() == Constants.NUM_2){
                orderGroupQueryVO.setGuaranteeDb("AND b.guarantee_ratio >= 50 AND b.guarantee_ratio <= 90");
            }else if(orderGroupQueryVO.getGuarantee() == Constants.NUM_3){
                orderGroupQueryVO.setGuaranteeDb("AND b.guarantee_ratio < 50");
            }
        }
        if(!ObjectUtil.isBlank(orderGroupQueryVO.getPlatform())){
            if(orderGroupQueryVO.getPlatform() == Constants.NUM_1){//PC端 合买进度
                //0=未满员，1=50%-90%，2=90%以上，3=已撤单
                if(orderGroupQueryVO.getProgress() != null){
                    if(orderGroupQueryVO.getProgress() == Constants.NUM_0){
                        orderGroupQueryVO.setProgressDb("AND b.progress < 100");
                    }else if(orderGroupQueryVO.getProgress() == Constants.NUM_1){
                        orderGroupQueryVO.setProgressDb("AND b.progress >= 50 AND b.progress <= 90");
                    }else if(orderGroupQueryVO.getProgress() == Constants.NUM_2){
                        orderGroupQueryVO.setProgressDb("AND b.progress >= 90");
                    }else if(orderGroupQueryVO.getProgress() == Constants.NUM_3){
                        orderGroupQueryVO.setProgressDb("AND b.grpbuy_status = 4");
                    }
                }
                if(orderGroupQueryVO.getCommission() != null) {//提成 有0值，不能使用ObjectUtil 判断
                    //null=不限提成，0=无提成，1: 小于1%，....，5：小于5%，....，10：小于10%
                    if(orderGroupQueryVO.getCommission() == Constants.NUM_0){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio = 0");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_1){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 1");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_2){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 2");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_3){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 3");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_4){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 4");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_5){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 5");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_6){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 6");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_7){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 7");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_8){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 8");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_9){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 9");
                    }else if(orderGroupQueryVO.getCommission() == Constants.NUM_10){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio <= 10");
                    }
                }
            }else{//其它平台（Wap、ios、android）
                if(!ObjectUtil.isBlank(orderGroupQueryVO.getProgress())){//合买进度
                    //null=不限，1=已满员，2=50%-90%，3=50%以下
                    if(orderGroupQueryVO.getProgress() == Constants.NUM_1){
                        orderGroupQueryVO.setProgressDb("AND b.progress = 100");
                    }else if(orderGroupQueryVO.getProgress() == Constants.NUM_2){
                        orderGroupQueryVO.setProgressDb("AND b.progress >= 50 AND b.progress <= 90");
                    }else if(orderGroupQueryVO.getProgress() == Constants.NUM_3){
                        orderGroupQueryVO.setProgressDb("AND b.progress < 50");
                    }
                }
                if(!ObjectUtil.isBlank(orderGroupQueryVO.getCommission())){
                    if(orderGroupQueryVO.getCommission() == Constants.NUM_1){
                        orderGroupQueryVO.setCommissionDb("AND b.commission_ratio = 0");
                    }
                }
            }
        }
    }

    /**
     * 设置排序条件,判断是否超多个排序字段
     * @param orderGroupQueryVO
     */
    private ResultBO<?> setOrderBy(final OrderGroupQueryVO orderGroupQueryVO) {
        int count = 0;
        //默认排序
        orderGroupQueryVO.setSortField("b.is_top DESC, b.is_recommend DESC, b.grpbuy_status ASC, b.progress DESC, b.create_time");
        orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
        if(orderGroupQueryVO.getBuyCount() != null){
            orderGroupQueryVO.setSortField("b.is_top DESC, b.is_recommend DESC, b.grpbuy_status ASC, b.buy_count");
            if(orderGroupQueryVO.getBuyCount() == Constants.NUM_0){
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
            }else{
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
            }
            count++;
        }
        if(orderGroupQueryVO.getBuyProgress() != null){
            orderGroupQueryVO.setSortField("b.is_top DESC, b.is_recommend DESC, b.progress");
            if(orderGroupQueryVO.getBuyProgress() == Constants.NUM_0){
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
            }else{
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
            }
            count++;
        }
        if(orderGroupQueryVO.getBuyAmount() != null){
            orderGroupQueryVO.setSortField("b.is_top DESC, b.is_recommend DESC,b.grpbuy_status ASC, a.order_amount");
            if(orderGroupQueryVO.getBuyAmount() == Constants.NUM_0){
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
            }else{
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
            }
            count++;
        }
        if(orderGroupQueryVO.getBuyRemain() != null){
            orderGroupQueryVO.setSortField("(a.order_amount - b.progress_amount)");
            if(orderGroupQueryVO.getBuyRemain() == Constants.NUM_0){
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
            }else{
                orderGroupQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
            }
            count++;
        }
        if(count > Constants.NUM_1){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        return ResultBO.ok();
    }
}
