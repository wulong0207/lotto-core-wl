package com.hhly.lottocore.remote.ordergroup.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupContentVO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupInfoVO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupQueryVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderSingleQueryVo;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;

/**
 * Created by longgy607 on 2018/4/28.
 */
public interface IOrderGroupService {
    /**
     * 发起合买
     * @param orderGroupInfoVO
     * @return
     * @thr Exception
     */
    ResultBO<?> addOrderGroup(OrderGroupInfoVO orderGroupInfoVO) throws Exception;

    /**
     * 合买大厅列表
     * @param qrderGroupQueryVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderGroupList(OrderGroupQueryVO qrderGroupQueryVO)throws Exception;

    /**
     * 合买大厅列表 v1.1（合买二期）
     * @param qrderGroupQueryVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderGroupListV11(OrderGroupQueryVO qrderGroupQueryVO)throws Exception;

    /**
     * 发起合买成功后，并且支付成功，调用
     * @param orderCode
     * @return
     * @throws Exception
     */
    ResultBO<?> addOrderGroupContent(String orderCode)throws Exception;

    /**
     * 查询全部合买用户
     * @param orderGroupContentVO
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderGroupContentList(OrderGroupContentVO orderGroupContentVO) throws Exception;


    /**
     * 校验此订单是否可以跟单，包括支付，满员，截止时间校验
     * @param orderSingleQueryVo
     * @return
     * @throws Exception
     */
    ResultBO<?> validOrderGroupContent(OrderSingleQueryVo orderSingleQueryVo) throws Exception;

    /**
     * 个人首页
     * @param orderSingleQueryVo
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderGroupPersonInfo(OrderSingleQueryVo orderSingleQueryVo)throws Exception;

    /**
     * 合买个人首页 战绩相信列表
     * @param orderSingleQueryVo
     * @return
     * @throws Exception
     */
    ResultBO<?> queryOrderStandingList(OrderSingleQueryVo orderSingleQueryVo) throws Exception;

    /**
     * 查询合买中奖排行榜
     * @return
     * @throws Exception
     */
    ResultBO<?> queryGroupRankingList()throws Exception;

    /**
     * 查询合买名人列表
     * @return
     * @throws Exception
     */
    ResultBO<?> queryGroupFamousList()throws Exception;



}
