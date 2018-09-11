package com.hhly.lottocore.controller;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.remote.agent.service.AgentRebateService;
import com.hhly.lottocore.remote.agent.service.MemberCenterService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.agent.vo.AgentQueryVO;

/**
 * @Description 代理系统 - 个人中心 控制器，供lotto-agent http调用
 * @Author longguoyou
 * @Date  2018/3/3 9:00 
 * @Since 1.8
 */

@RestController
@RequestMapping("/agent")
public class MemeberCenterController {

    private static final Logger logger = Logger.getLogger(MemeberCenterController.class);

     @Resource(name="memberCenterService")
     private MemberCenterService memberCenterService;

     @Resource(name="agentRebateService")
     private AgentRebateService agentRebateService;
     
     @Autowired
     private UserInfoCacheService userInfoCacheService;

    /**
     * 获取 我的直属会员总人数
     * @return
     */
    @RequestMapping(value = "/getDirectMemberTotalNum", method = RequestMethod.POST)
     public ResultBO<?> getDirectMemberTotalNum(@RequestBody AgentQueryVO agentQueryVO){
        ResultBO<?> resultBO = null;
        try {
            resultBO = memberCenterService.getDirectMemberTotalNum(agentQueryVO);
        } catch (Exception e) {
            logger.error("获取我的直属会员总人数失败！",e);
            return ResultBO.err();
        }
        return resultBO;
     }

    /**
     * 获取 我的直属会员列表
     * @return
     */
    @RequestMapping(value = "/queryDirectMemberList", method = RequestMethod.POST)
    public ResultBO<?> queryDirectMemberList(@RequestBody AgentQueryVO queryVO){
        ResultBO<?> resultBO = null;
        try {
            resultBO = memberCenterService.getDirectMemberList(queryVO);
        } catch (Exception e) {
            logger.error("获取我的直属会员列表失败！",e);
            return ResultBO.err();
        }
        return resultBO;
    }

    /**
     * 获取 我的下级代理总人数
     * @return
     */
    @RequestMapping(value = "/getAgentMemberTotalNum", method = RequestMethod.POST)
    public ResultBO<?> getAgentMemberTotalNum(@RequestBody AgentQueryVO queryVO){
        ResultBO<?> resultBO = null;
        try {
            resultBO = memberCenterService.getAgentMemberTotalNum(queryVO);
        } catch (Exception e) {
            logger.error("获取我的下级代理总人数失败！",e);
            return ResultBO.err();
        }
        return resultBO;
    }

    /**
     * 获取 我的下级代理列表
     * @return
     */
    @RequestMapping(value = "/queryAgentMemberList", method = RequestMethod.POST)
    public ResultBO<?> queryAgentMemberList(@RequestBody AgentQueryVO queryVO){
        ResultBO<?> resultBO = null;
        try {
            resultBO = memberCenterService.getAgentMemberList(queryVO);
        } catch (Exception e) {
            logger.error("获取我的下级代理列表失败！",e);
            return ResultBO.err();
        }
        return resultBO;
    }

    /**
     * 获取 返佣变更记录列表
     * @return
     */
    @RequestMapping(value = "/queryAgentRebateInfoList", method = RequestMethod.POST)
    public ResultBO<?> queryAgentRebateInfoList(@RequestBody AgentQueryVO queryVO){
        ResultBO<?> resultBO = null;
        try {
            resultBO = agentRebateService.getAgentRebateInfoList(queryVO);
        } catch (Exception e) {
            logger.error("获取 返佣变更记录列表失败！",e);
            return ResultBO.err();
        }
        return resultBO;
    }

    /**
     * 获取代理人首页基本信息
     * @return
     */
    @RequestMapping(value = "/getAgentHomeBaseInfo", method = RequestMethod.GET)
    public ResultBO<?> getAgentHomeBaseInfo(String token){
        if(ObjectUtil.isBlank(token)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        try {
            return  memberCenterService.getAgentHomeBaseInfo(token);
        }catch (Exception e){
            logger.error("查询代理信息失败！",e);
            return ResultBO.err();
        }
    }
    
    /**
     * 根据token判断用户是否登录
     * @desc 
     * @create 2018年3月10日
     * @param token
     * @return ResultBO<?>
     */
    @RequestMapping(value = "/checkToken")
    public ResultBO<?> checkUserLogin(String token){
        if(ObjectUtil.isBlank(token)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        return userInfoCacheService.checkToken(token);
    }
    
    /**
     * 获取代理会员订单详情列表
     * @return
     */
    @RequestMapping(value = "/getAgentOrderInfoList", method = RequestMethod.POST)
    public ResultBO<?> getAgentOrderInfoList(@RequestBody AgentQueryVO queryVO){
        try {
            return  memberCenterService.getAgentOrderInfoList(queryVO);
        }catch (Exception e){
            logger.error("获取代理会员订单详情列表失败！",e);
            return ResultBO.err();
        }
    }

    /**
     * 获取代理会员订单统计金额
     * @return
     */
    @RequestMapping(value = "/getAgentOrderTotalMoney", method = RequestMethod.POST)
    public ResultBO<?> getAgentOrderTotalMoney(@RequestBody AgentQueryVO queryVO){
        try {
            return  memberCenterService.getAgentOrderTotalMoney(queryVO);
        }catch (Exception e){
            logger.error("获取代理会员订单统计金额失败！",e);
            return ResultBO.err();
        }
    }
    
    
    @RequestMapping(value = "/getAgentStatus", method = RequestMethod.POST)
    public ResultBO<?> getAgentStatus(@RequestBody AgentQueryVO queryVO){
        if(ObjectUtil.isBlank(queryVO.getToken())){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        try {
            return  memberCenterService.getAgentStatus(queryVO.getToken());
        }catch (Exception e){
            logger.error("获取用户代理状态出错",e);
            return ResultBO.errMessage("10002", "获取用户代理状态出错");
        }
    }
}
