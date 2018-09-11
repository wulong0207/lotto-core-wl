package com.hhly.lottocore.remote.agent.service.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.agent.dao.MemberCenterDaoMapper;
import com.hhly.lottocore.remote.agent.service.MemberCenterService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderCopyEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.UserInfoBOUtil;
import com.hhly.skeleton.lotto.base.agent.bo.AgentInfoBO;
import com.hhly.skeleton.lotto.base.agent.bo.AgentMemberInfoBO;
import com.hhly.skeleton.lotto.base.agent.bo.AgentOrderInfoListBO;
import com.hhly.skeleton.lotto.base.agent.bo.DirectlyMemberInfoBO;
import com.hhly.skeleton.lotto.base.agent.vo.AgentQueryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

@Service("memberCenterService")
public class MemberCenterServiceImpl implements MemberCenterService{

    /** log 日志 */
    private static Logger logger = LoggerFactory.getLogger(MemberCenterServiceImpl.class);

    /** 个人中心DAO */
    @Autowired
    private MemberCenterDaoMapper memberCenterDaoMapper;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private IPageService pageService;

    @Value("${before_file_url}")
    protected String beforeFileUrl;


    @Override
    public ResultBO<?> getDirectMemberTotalNum(AgentQueryVO agentQueryVO) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(agentQueryVO.getToken());
        if(resultBO.isError())
            return resultBO;
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
//        userInfoBO.setId(319);//测试使用
        agentQueryVO.setUserId(userInfoBO.getId());
//        AgentQueryVO param = new AgentQueryVO();
//        param.setUserId(userInfoBO.getId());
//        AgentInfoBO agentInfoBO = findAgentInfoByUserId(param);
//        agentQueryVO.setAgentLevel(ObjectUtil.isBlank(agentInfoBO)? -1 : agentInfoBO.getAgentLevel()+ Constants.NUM_1);
        return ResultBO.ok(memberCenterDaoMapper.selectDirectlyMemberCount(agentQueryVO));
    }

    @Override
    public ResultBO<?> getDirectMemberList(AgentQueryVO agentQueryVO) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(agentQueryVO.getToken());
        if(resultBO.isError())
            return resultBO;
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
//        userInfoBO.setId(319);//测试使用
        agentQueryVO.setUserId(userInfoBO.getId());
//        AgentQueryVO param = new AgentQueryVO();
//        param.setUserId(userInfoBO.getId());
//        AgentInfoBO agentInfoBO = findAgentInfoByUserId(param);
//        agentQueryVO.setAgentLevel(ObjectUtil.isBlank(agentInfoBO)? -1 : agentInfoBO.getAgentLevel()+ Constants.NUM_1);
        processDif(agentQueryVO,true);
        PagingBO<DirectlyMemberInfoBO> pageData = pageService.getPageData(agentQueryVO, new ISimplePage<DirectlyMemberInfoBO>() {

            @Override
            public int getTotal() {
                return memberCenterDaoMapper.selectDirectlyMemberCount(agentQueryVO);
            }

            @Override
            public List<DirectlyMemberInfoBO> getData() {
                return memberCenterDaoMapper.selectDirectlyMember(agentQueryVO);
            }

        });
        List<DirectlyMemberInfoBO> list = pageData.getData();
        for(DirectlyMemberInfoBO bean :list){
//            if(!ObjectUtil.isBlank(bean.getOrderAmount())){
//                bean.setOrderAmount(MathUtil.round(Double.valueOf(bean.getOrderAmount()),0));
//            }
            bean.setHeadUrl(UserInfoBOUtil.getHeadUrl(bean.getHeadUrl(), beforeFileUrl));
            bean.setPhoneNumber(getPhoneNumber(bean.getPhoneNumber()));
        }
        logger.info("查询直属会员列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询直属会员列表信息：detailList=" + list.size() + " 条");
        return ResultBO.ok(list);
    }

    @Override
    public ResultBO<?> getAgentMemberTotalNum(AgentQueryVO agentQueryVO) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(agentQueryVO.getToken());
        if(resultBO.isError())
            return resultBO;
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
//        userInfoBO.setId(319);//测试使用
        agentQueryVO.setUserId(userInfoBO.getId());
        AgentQueryVO param = new AgentQueryVO();
        param.setUserId(userInfoBO.getId());
        AgentInfoBO agentInfoBO = findAgentInfoByUserId(param);
        agentQueryVO.setAgentLevel(ObjectUtil.isBlank(agentInfoBO)? -1 : agentInfoBO.getAgentLevel()+ Constants.NUM_1);
        return ResultBO.ok(memberCenterDaoMapper.selectUnderlingAgentMemberCount(agentQueryVO));
    }

    @Override
    public ResultBO<?> getAgentMemberList(AgentQueryVO agentQueryVO) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(agentQueryVO.getToken());
        if(resultBO.isError())
            return resultBO;
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
//        userInfoBO.setId(319);//测试
        agentQueryVO.setUserId(userInfoBO.getId());
        AgentQueryVO param = new AgentQueryVO();
        param.setUserId(userInfoBO.getId());
        AgentInfoBO agentInfoBO = findAgentInfoByUserId(param);
        agentQueryVO.setAgentLevel(ObjectUtil.isBlank(agentInfoBO)? -1 : agentInfoBO.getAgentLevel()+ Constants.NUM_1);
        processDif(agentQueryVO,false);

        PagingBO<AgentMemberInfoBO> pageData = pageService.getPageData(agentQueryVO, new ISimplePage<AgentMemberInfoBO>() {

            @Override
            public int getTotal() {
                return memberCenterDaoMapper.selectUnderlingAgentMemberCount(agentQueryVO);
            }

            @Override
            public List<AgentMemberInfoBO> getData() {
                return memberCenterDaoMapper.selectUnderlingAgentMember(agentQueryVO);
            }
        });
        List<AgentMemberInfoBO> list = pageData.getData();
        for(AgentMemberInfoBO bean : list){
            bean.setHeadUrl(UserInfoBOUtil.getHeadUrl(bean.getHeadUrl(), beforeFileUrl));
            bean.setPhoneNumber(getPhoneNumber(bean.getPhoneNumber()));
        }
        logger.info("查询下级代理会员列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询下级代理会员列表信息：detailList=" + list.size() + " 条");
        return ResultBO.ok(list);
    }

    @Override
    public ResultBO<?> getAgentHomeBaseInfo(String token) throws Exception {
        Integer userId = userUtil.getUserIdByToken(token);
        if(ObjectUtil.isBlank(userId)){
            return ResultBO.err(MessageCodeConstants.TOKEN_LOSE_SERVICE);
        }
        return ResultBO.ok(memberCenterDaoMapper.getAgentHomeBaseInfo(userId));
    }

    @Override
    public AgentInfoBO findAgentInfoByUserId(AgentQueryVO agentQueryVO) throws Exception {
        return memberCenterDaoMapper.findAgentInfoByUserId(agentQueryVO);
    }

    @Override
    public ResultBO<?> getAgentOrderInfoList(AgentQueryVO agentQueryVO) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(agentQueryVO.getToken());
        if(resultBO.isError())
            return resultBO;
//        AgentInfoBO agentInfoBO = findAgentInfoByUserId(agentQueryVO);
//        agentInfoBO.setUserId(319);
//        if(ObjectUtil.isBlank(agentInfoBO)){
//            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
//        }
        AgentQueryVO validateParam = new AgentQueryVO();
        validateParam.setToken(agentQueryVO.getToken());
//        validateParam.setAgentLevel(agentInfoBO.getAgentLevel());
        validateParam.setAgentId(agentQueryVO.getAgentId());
        //验证是否属于直属会员，防止用户自己改参数
        ResultBO<?> result = getDirectMemberTotalNum(validateParam);
        if(result.isError())
            return result;
        if((Integer)result.getData() != 1){
           return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
//        agentQueryVO.setUserId(agentInfoBO.getUserId());
        agentQueryVO.setUserId(agentQueryVO.getAgentId());
        agentQueryVO.setSortField("a.buy_time");
        agentQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
        PagingBO<AgentOrderInfoListBO> pageData = pageService.getPageData(agentQueryVO, new ISimplePage<AgentOrderInfoListBO>() {

            @Override
            public int getTotal() {
                return memberCenterDaoMapper.selectAgentOrderInfoListCount(agentQueryVO);
            }

            @Override
            public List<AgentOrderInfoListBO> getData() {
                return memberCenterDaoMapper.selectAgentOrderInfoList(agentQueryVO);
            }
        });
        List<AgentOrderInfoListBO> list = pageData.getData();
        for(AgentOrderInfoListBO bean: list){
            bean.setLogoUrl(UserInfoBOUtil.getHeadUrl(bean.getLogoUrl(), beforeFileUrl));
        }
        logger.info("查询用户代理订单列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询用户代理订单列表信息：detailList=" + list.size() + " 条");
        return ResultBO.ok(list);
    }

    @Override
    public ResultBO<?> getAgentOrderTotalMoney(AgentQueryVO agentQueryVO) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(agentQueryVO.getToken());
        if(resultBO.isError())
            return resultBO;
//        AgentInfoBO agentInfoBO = findAgentInfoByUserId(agentQueryVO);
//        if(ObjectUtil.isBlank(agentInfoBO)){
//            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
//        }
        AgentQueryVO validateParam = new AgentQueryVO();
        validateParam.setToken(agentQueryVO.getToken());
//        validateParam.setAgentLevel(agentInfoBO.getAgentLevel());
        validateParam.setAgentId(agentQueryVO.getAgentId());
        //验证是否属于直属会员，防止用户自己改参数
        ResultBO<?> result = getDirectMemberTotalNum(validateParam);
        if(resultBO.isError())
            return resultBO;
        if((Integer)result.getData() != 1){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        return ResultBO.ok(memberCenterDaoMapper.getAgentOrderTotalMoney(agentQueryVO.getAgentId()));
    }

    /**
     * 处理排序逻辑
     * 默认按注册时间倒序。1：注册时间顺序；2：注册时间倒序；3：按投注金额顺序；4：按投注金额倒序
     * @param agentQueryVO
     * @param flag 是否直属会员
     */
    private static void processDif(AgentQueryVO agentQueryVO,boolean flag){
           if(ObjectUtil.isBlank(agentQueryVO.getSortType())){
            agentQueryVO.setSortField("a.regist_time");
               agentQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
               return;
           }
            switch (agentQueryVO.getSortType()){
                case 1:
                agentQueryVO.setSortField("a.regist_time");
                    agentQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
                    break;
                case 2:
                agentQueryVO.setSortField("a.regist_time");
                    agentQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                    break;
                case 3:
                    if(flag){
                        agentQueryVO.setSortField("e.direct_buy_money");
                    }else{
                        agentQueryVO.setSortField("e.agent_buy_money");
                    }
                    agentQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.ASC.getValue());
                    break;
                case 4:
                    if(flag){
                        agentQueryVO.setSortField("e.direct_buy_money");
                        agentQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                    }else{
                        agentQueryVO.setSortField("e.agent_buy_money");
                        agentQueryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
                    }
                    break;
            }
    }

    /**
     * 隐藏部分手机号码
     * @param phoneNumber
     * @return
     */
    private static String getPhoneNumber(String phoneNumber){
        if(ObjectUtil.isBlank(phoneNumber)){
            return null;
        }
        char[] chars = phoneNumber.toCharArray();
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<chars.length;i++){
            if(i>2 && i< 7){
                buffer.append("*");
            }else{
                buffer.append(chars[i]);
            }
        }
        return buffer.toString();
    }

	@Override
	public ResultBO<?> getAgentStatus(String token) throws Exception {
        ResultBO<?> resultBO = userInfoCacheService.checkToken(token);
        if(resultBO.isError()) {
        	return resultBO;
        }
        UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
        Integer agentStatus = memberCenterDaoMapper.findAgentStatusByUserId(userInfoBO.getId());
		// 因为agentStatus数据库非空，所以如果为空，则表示没有找到
		if (agentStatus == null) {
			// 0 禁用 1启用 2非代理
			return ResultBO.ok(Constants.NUM_2);
		}
		return ResultBO.ok(agentStatus);
	}
    
}
