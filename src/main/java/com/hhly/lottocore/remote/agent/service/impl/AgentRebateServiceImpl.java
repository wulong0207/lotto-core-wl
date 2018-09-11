package com.hhly.lottocore.remote.agent.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.agent.dao.AgentRebateDaoMapper;
import com.hhly.lottocore.remote.agent.service.AgentRebateService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.agent.vo.AgentQueryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @Description
 * @Author longguoyou
 * @Date  2018/3/3 9:13
 * @Since 1.8
 */
@Service("agentRebateService")
public class AgentRebateServiceImpl implements AgentRebateService{

    /** log 日志 */
    private static Logger logger = LoggerFactory.getLogger(AgentRebateServiceImpl.class);

    /** 返佣配置信息 DAO */
    @Autowired
    private AgentRebateDaoMapper agentRebateDaoMapper;

    @Autowired
    private UserInfoCacheService userInfoCacheService;


    @Override
    public ResultBO<?> getAgentRebateInfoList(AgentQueryVO agentQueryVO) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(agentQueryVO.getToken());
        if(resultBO.isError())
            return resultBO;
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
        agentQueryVO.setUserId(userInfoBO.getId());
        List list = agentRebateDaoMapper.selectAgentRebateByUserId(agentQueryVO);
        if(ObjectUtil.isBlank(list)){
            agentQueryVO.setDefaultFlag(1);//取系统默认
            list = agentRebateDaoMapper.selectAgentRebateByUserId(agentQueryVO);
        }
        return ResultBO.ok(list);
    }

}
