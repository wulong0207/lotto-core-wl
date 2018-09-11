package com.hhly.lottocore.remote.ordercopy.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueLinkDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueInfoPO;
import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueLinkPO;
import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.lottocore.remote.ordercopy.service.FocusService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.FocusOptEnum.OptEnum;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.mq.msg.MessageModel;
import com.hhly.skeleton.base.mq.msg.OperateNodeMsg;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.ordercopy.bo.MUserIssueInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.QueryUserIssueLinkBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueLinkVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

@Service("focusService")
public class FocusServiceImpl implements FocusService {
	
	private static Logger logger = LoggerFactory.getLogger(FocusServiceImpl.class);
	
	@Autowired
	private IPageService pageService;
	@Autowired
	private UserInfoCacheService userInfoCacheService;
	
	@Autowired
    private MUserIssueLinkDaoMapper mUserIssueLinkDaoMapper;

    @Autowired
    private MUserIssueInfoDaoMapper mUserIssueInfoDaoMapper;
    
    @Resource(name="orderCopyFocusMessageProvider")
	private MessageProvider orderCopyFocusMessageProvider;
    
    @Value("${msg_queue}")
    private String msgQueue;


	
	/**
	 * 新增关注：加索引版</br>
	 * 1、存在则更新；2、不存在则新增 
	 * @param mUserIssueLinkPO
	 * @return
	 */
	private ResultBO<?> addFocus(MUserIssueLinkPO mUserIssueLinkPO,Integer userId) {
		//新增关注，设置参数
		mUserIssueLinkPO.setUserId(userId);
		MUserIssueLinkVO mUserIssueLinkVO = new MUserIssueLinkVO();
		mUserIssueLinkVO.setUserIssueId(mUserIssueLinkPO.getUserIssueId());
		mUserIssueLinkVO.setUserId(mUserIssueLinkPO.getUserId());
		/**是否第一次*/
		int isFirst = mUserIssueLinkDaoMapper.selectByConditionCount(mUserIssueLinkVO);
		mUserIssueLinkVO.setDataStatus(false);//已取消关注
		mUserIssueLinkVO.setPageIndex(0);
		mUserIssueLinkVO.setPageSize(10);
		List<QueryUserIssueLinkBO> list = mUserIssueLinkDaoMapper.selectByCondition(mUserIssueLinkVO);
		mUserIssueLinkVO.setDataStatus(true);//关注
		if(list.size() > 1){//只能存在一条记录 或 不存在
			logger.debug("新增关注信息前，查询数据异常：" + list.size());
			return ResultBO.err();
		}
		//入库
		int count = 0;
		try {
			if(list.size() == 0){//不存在，则新增
				count = mUserIssueLinkDaoMapper.insertSelective(mUserIssueLinkPO);
			}else{//存在一条，则更新
				MUserIssueLinkPO updatePO = new MUserIssueLinkPO();
				updatePO.setId(list.get(0).getId());
				updatePO.setBeginTime(new Date());
				updatePO.setDataStatus(true);//设置为关注
				updatePO.setUpdateTime(new Date());
				count = mUserIssueLinkDaoMapper.updateByPrimaryKeySelective(updatePO);
			}
		} catch (Exception e) {
			logger.error("新增关注：加索引版", e);
			return ResultBO.err(MessageCodeConstants.OPERATION_NO_REPEAT_SERVICE);
		}
		logger.debug("新增关注， 影响数据：" + count + " 条");
		if(count > 0){
			MUserIssueInfoBO mUserIssueInfoBO = mUserIssueInfoDaoMapper.selectByPrimaryKey(mUserIssueLinkPO.getUserIssueId().longValue());
			MUserIssueInfoPO mUserIssueInfoPO = new MUserIssueInfoPO();
			if(!ObjectUtil.isBlank(mUserIssueInfoBO)){
				mUserIssueInfoPO.setId(mUserIssueInfoBO.getId());
				mUserIssueInfoPO.setFocusNum(mUserIssueInfoBO.getFocusNum());
				ResultBO<?> resultBO = updateSummaryOfFocus(mUserIssueInfoPO, OptEnum.ADD);
				if(resultBO.isError()){return resultBO;}
				/**关注消息*/
				if(isFirst == 0){
					sendMqOfFocus(Long.valueOf(mUserIssueLinkVO.getUserIssueId()),mUserIssueLinkVO.getUserId());
				}
			}
		}
		return ResultBO.ok();
	}
    
	/**
	 * 发生关注消息
	 * @author longguoyou
	 * @date 2017年11月14日
	 * @param userId 关注用户ID
	 */
	private void sendMqOfFocus(Long id, Integer userId) {
		//通过发单用户主键查询，发单用户ID
		MUserIssueInfoBO mUserIssueInfoBO = mUserIssueInfoDaoMapper.selectByPrimaryKey(id);
		if(!ObjectUtil.isBlank(mUserIssueInfoBO)){
			MessageModel messageModel = new MessageModel();
			messageModel.setKey("nodeMsgSend");
			messageModel.setMessageSource("lotto");
			OperateNodeMsg operateNodeMsg = new OperateNodeMsg();
			operateNodeMsg.setNodeId(16);
			operateNodeMsg.setNodeData(mUserIssueInfoBO.getUserId() + SymbolConstants.SEMICOLON + userId);
			messageModel.setMessage(operateNodeMsg);
			orderCopyFocusMessageProvider.sendMessage(msgQueue, messageModel);
		}
	}

	/**
	 * 取消关注：加索引版</br>
	 * 1、查询是否存在记录</br>
	 * 2、判断记录数，等于零，报操作异常，等于1，正常更新，大于1，操作异常
	 * @param userId
	 * @param userIssueId
	 * @return
	 */
	private ResultBO<?> deleteFocus(Integer userId, Integer userIssueId) {
		MUserIssueLinkPO mUserIssueLinkPO = new MUserIssueLinkPO();
		mUserIssueLinkPO.setUserId(userId);
		mUserIssueLinkPO.setUserIssueId(userIssueId);
		MUserIssueLinkVO mUserIssueLinkVO = new MUserIssueLinkVO();
		mUserIssueLinkVO.setUserId(userId);
		mUserIssueLinkVO.setUserIssueId(userIssueId);
		mUserIssueLinkVO.setDataStatus(true);
		mUserIssueLinkVO.setPageIndex(0);
		mUserIssueLinkVO.setPageSize(2);
		List<QueryUserIssueLinkBO> listBO = mUserIssueLinkDaoMapper.selectByCondition(mUserIssueLinkVO);
		if(listBO.size() != 1){
			logger.debug("更新关注前，数据查询异常：总记录数 [" + listBO.size() + "]");
			return ResultBO.err(MessageCodeConstants.OPERATION_NO_REPEAT_SERVICE);
		}
		if(!ObjectUtil.isBlank(listBO)){
			mUserIssueLinkPO.setId(listBO.get(0).getId());//主键
			mUserIssueLinkPO.setEndTime(new Date());//结束关注时间
			mUserIssueLinkPO.setUpdateTime(new Date());//更新时间
			mUserIssueLinkPO.setDataStatus(false);//1--> 0
			int count = mUserIssueLinkDaoMapper.updateByPrimaryKeySelective(mUserIssueLinkPO);
			logger.debug("取消关注， 影响数据：" + count + " 条");
			if(count > 0){//更新成功，才减总数
				MUserIssueInfoBO mUserIssueInfoBO = mUserIssueInfoDaoMapper.selectByPrimaryKey(mUserIssueLinkPO.getUserIssueId().longValue());
				MUserIssueInfoPO mUserIssueInfoPO = new MUserIssueInfoPO();
				if(!ObjectUtil.isBlank(mUserIssueInfoBO)){
					mUserIssueInfoPO.setId(mUserIssueInfoBO.getId());
					mUserIssueInfoPO.setFocusNum(mUserIssueInfoBO.getFocusNum());
					ResultBO<?> resultBO = updateSummaryOfFocus(mUserIssueInfoPO, OptEnum.DELETE);
					return resultBO;
				}
			}
		}
	    return ResultBO.ok();	
	}
	
	

	
	@Override
	public ResultBO<?> updateFocus(MUserIssueLinkPO mUserIssueLinkPO, String flag) {
		//判断
		Assert.paramNotNull(mUserIssueLinkPO.getUserIssueId(), "userIssueId");
		ResultBO<?> resultBo = userInfoCacheService.checkToken(mUserIssueLinkPO.getToken());
		if(resultBo.isError())
			return resultBo;
		UserInfoBO userInfoBO = (UserInfoBO) resultBo.getData();
		if(ObjectUtil.isBlank(flag)){
			mUserIssueLinkPO.setBeginTime(new Date());
			mUserIssueLinkPO.setDataStatus(true);
			return this.addFocus(mUserIssueLinkPO,userInfoBO.getId());
		}
		return this.deleteFocus(userInfoBO.getId(), mUserIssueLinkPO.getUserIssueId());
	}
	

	@Override
	public ResultBO<?> updateSummaryOfFocus(MUserIssueInfoPO mUserIssueInfoPO, OptEnum optEnum) {
		switch(optEnum){
		case ADD :   mUserIssueInfoPO.setFocusNum(mUserIssueInfoPO.getFocusNum() + 1); break;
		case DELETE: mUserIssueInfoPO.setFocusNum(mUserIssueInfoPO.getFocusNum()-1<0?0:(mUserIssueInfoPO.getFocusNum()- 1)); break;
		default: break;
		}
		int count = mUserIssueInfoDaoMapper.updateByPrimaryKeySelective(mUserIssueInfoPO);
		if(count == 0){logger.info("更新发单用户关注总人数异常！"); return ResultBO.err();}
		return ResultBO.ok();
	}
	
	@Override
	public boolean isFocus(MUserIssueLinkVO mUserIssueLinkVO) {
		int count = mUserIssueLinkDaoMapper.selectByConditionCount(mUserIssueLinkVO);
		if(count > 0){return true;}
		return false;
	}

	@Override
	public ResultBO<?> queryFocusByMUserIssueLinkVO(final MUserIssueLinkVO mUserIssueLinkVO) {
		//判断
		Assert.paramNotNull(mUserIssueLinkVO.getPageSize(), "pageSize");
		Assert.paramNotNull(mUserIssueLinkVO.getPageIndex(), "pageIndex");
	    Assert.paramNotNull(mUserIssueLinkVO.getToken(), "token");
	    Assert.paramNotNull(mUserIssueLinkVO.getUserIssueId(), "userIssueId");
	    //设值
		PagingBO<QueryUserIssueLinkBO> pageData = pageService.getPageData(mUserIssueLinkVO,
				new ISimplePage<QueryUserIssueLinkBO>() {
					@Override
					public int getTotal() {
						return mUserIssueLinkDaoMapper.selectByConditionCount(mUserIssueLinkVO);
					}

					@Override
					public List<QueryUserIssueLinkBO> getData() {
						return mUserIssueLinkDaoMapper.selectByCondition(mUserIssueLinkVO);
					}
				});
		logger.info("查询到关注信息：count= " + pageData.getTotal() + " 条");
		logger.info("查询到关注信息：detailList=" + pageData.getData().size() + " 条");
		return ResultBO.ok(pageData.getData());
	}
	
//	/**
//	 * 检查token , 并返回用户ID
//	 * @author longguoyou
//	 * @date 2017年10月12日
//	 * @param token
//	 * @return
//	 */
//	private ResultBO<?> checkUserTokenAndReturnUserId(String token){
//		ResultBO<?> result = userInfoCacheService.checkToken1(token);
//		if(result.isError())
//    		return result;
//
//
//
//		return result;
//	}
}
