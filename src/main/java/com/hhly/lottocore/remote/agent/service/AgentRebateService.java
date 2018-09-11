package com.hhly.lottocore.remote.agent.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.agent.vo.AgentQueryVO;

/**
 * Created by longgy607 on 2018/3/3.
 */
public interface AgentRebateService {
    public ResultBO<?> getAgentRebateInfoList(AgentQueryVO agentQueryVO) throws Exception;
}
