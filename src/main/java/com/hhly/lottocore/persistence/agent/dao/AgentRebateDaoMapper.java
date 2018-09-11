package com.hhly.lottocore.persistence.agent.dao;

import com.hhly.skeleton.lotto.base.agent.vo.AgentQueryVO;

import java.util.List;

/**
 * @Description 返佣配置信息
 * @Author longguoyou
 * @Date  2018/3/2 10:35 
 * @Since 1.8
 */
public interface AgentRebateDaoMapper {

    List selectAgentRebateByUserId(AgentQueryVO queryVO);

    List selectDefaultAgentRebate();

}