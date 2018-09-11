package com.hhly.lottocore.remote.agent.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.agent.bo.AgentInfoBO;
import com.hhly.skeleton.lotto.base.agent.vo.AgentQueryVO;

/**
 * @Description
 * @Author longguoyou
 * @Date  2018/3/3 9:07 
 * @Since 1.8
 */
public interface MemberCenterService {
    /**
     * 获取直属会员总人数
     */
    public ResultBO<?> getDirectMemberTotalNum(AgentQueryVO agentQueryVO) throws Exception;

    /**
     * 获取直属会员信息列表
     */
    public ResultBO<?> getDirectMemberList(AgentQueryVO agentQueryVO) throws Exception;

    /**
     * 获取下级会员总人数
     */
    public ResultBO<?> getAgentMemberTotalNum(AgentQueryVO agentQueryVO) throws Exception;

    /**
     * 获取下级会员信息列表
     */
    public ResultBO<?> getAgentMemberList(AgentQueryVO agentQueryVO) throws Exception;

    /**
     * 根据token获取代理基本信息
     * @param token
     * @return
     * @throws Exception
     */
    public ResultBO<?> getAgentHomeBaseInfo(String token) throws Exception;

    /**
     * 内部调用，不对外
     * @param agentQueryVO
     * @return
     * @throws Exception
     */
    public AgentInfoBO findAgentInfoByUserId(AgentQueryVO agentQueryVO)throws Exception;

    /**
     * 代理会员订单详情
     * @param agentQueryVO
     * @return
     * @throws Exception
     */
    public ResultBO<?> getAgentOrderInfoList(AgentQueryVO agentQueryVO)throws Exception;

    /**
     * 代理会员订单详情顶部信息(昵称、累计投注金额)
     * @param agentQueryVO
     * @return
     * @throws Exception
     */
    public ResultBO<?> getAgentOrderTotalMoney(AgentQueryVO agentQueryVO)throws Exception;
    
    /**
     * 获取用户代理状态(判断用户状态是禁用还是启用状态)
     */
    public ResultBO<?> getAgentStatus(String token) throws Exception;
}
