package com.hhly.lottocore.persistence.agent.dao;

import com.hhly.skeleton.lotto.base.agent.bo.AgentHomeInfoBO;
import com.hhly.skeleton.lotto.base.agent.bo.AgentInfoBO;
import com.hhly.skeleton.lotto.base.agent.bo.AgentOrderInfoBO;
import com.hhly.skeleton.lotto.base.agent.vo.AgentQueryVO;

import java.util.List;

/**
 * @Description 代理：个人中心（直属会员、下级代理）查询
 * @Author longguoyou
 * @Date  2018/3/2 10:35
 * @Since 1.8
 */
public interface MemberCenterDaoMapper {

    List selectDirectlyMember(AgentQueryVO agentQueryVO);

    int selectDirectlyMemberCount(AgentQueryVO agentQueryVO);

    List selectUnderlingAgentMember(AgentQueryVO agentQueryVO);

    int selectUnderlingAgentMemberCount(AgentQueryVO agentQueryVO);

    AgentHomeInfoBO getAgentHomeBaseInfo(Integer userId);

    AgentInfoBO findAgentInfoByUserId(AgentQueryVO agentQueryVO);

    List selectAgentOrderInfoList(AgentQueryVO agentQueryVO);

    int selectAgentOrderInfoListCount(AgentQueryVO agentQueryVO);

    AgentOrderInfoBO getAgentOrderTotalMoney(Integer userId);
    
    Integer findAgentStatusByUserId(Integer userId);

}