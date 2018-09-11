package com.hhly.lottocore.remote.ordercopy.service.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueInfoDaoMapper;
import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueInfoPO;
import com.hhly.lottocore.remote.ordercopy.service.MUserIssueInfoService;
import com.hhly.lottocore.remote.ordercopy.service.MUserIssueLevelService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.OrderCopyEnum;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.ordercopy.bo.CommissionBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.MUserIssueCountPrizeViewBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.MUserIssueInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.QueryUserIssueInfoBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueInfoVO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * @author lgs on
 * @version 1.0
 * @desc 发单用户service impl
 * @date 2017/9/21.
 * @company 益彩网络科技有限公司
 */
@Service("mUserIssueInfoService")
public class MUserIssueInfoServiceImpl implements MUserIssueInfoService {
    private static Logger logger = LoggerFactory.getLogger(MUserIssueInfoServiceImpl.class);

	@Autowired
	private IPageService pageService;

    @Autowired
    private MUserIssueInfoDaoMapper mUserIssueInfoDaoMapper;
    
    @Autowired
    private MUserIssueLevelService mUserIssueLevelService;

	@Autowired
	private UserInfoCacheService userInfoCacheService;

    
    @Value("${before_file_url}")
    private String beforeFileUrl;

    /**
     * 新增发单用户
     *
     * @param vo
     * @return
     */
    @Override
    public int insert(MUserIssueInfoVO vo) {
        return mUserIssueInfoDaoMapper.insertSelective(new MUserIssueInfoPO(vo));
    }

    /**
     * 根据用户id查询 发单信息。
     *
     * @param userId
     * @return
     */
    @Override
    public MUserIssueInfoBO findUserIssueInfoBoByUserId(Long userId) {
        return mUserIssueInfoDaoMapper.findUserIssueInfoByUserId(userId);
    }

    /**
     * 根据用户id查询 发单信息。
     *
     * @param id
     * @return
     */
    @Override
    public MUserIssueInfoBO findUserIssueInfoBoById(Long id) {
        return mUserIssueInfoDaoMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询用户是否发过单
     *
     * @param userId
     * @return
     */
    @Override
    public Integer findUserIssueInfoCountByUserId(Long userId) {
        return mUserIssueInfoDaoMapper.findUserIssueInfoCountByUserId(userId);
    }

	@Override
	public ResultBO<?> queryUserIssueInfo(final QueryVO queryVO) {
		//判断
		Assert.paramNotNull(queryVO.getPageIndex(), "pageIndex");
		Assert.paramNotNull(queryVO.getPageSize(), "pageSize");
		Assert.paramNotNull(queryVO.getSortCondition(), "sortCondition");
		Assert.paramNotNull(queryVO.getQueryType(), "queryType");
		//设值
		//推荐专家列表
		if(queryVO.getQueryType() == 1){
			if(queryVO.getSortCondition() == 1){
		    	queryVO.setSortField("a.hit_rate desc, f.copy_issue_num");
			}else if(queryVO.getSortCondition() == 2){
				queryVO.setSortField("a.issue_num desc, f.copy_issue_num");
			}else{//最大连红 ，需要变更 2017.12.20
				queryVO.setSortField("a.continue_hit desc, f.copy_issue_num");
			}
		}
		//与我相关
		else if(queryVO.getQueryType() == 2){
			/**推荐最多*/
			if(queryVO.getSortCondition() == 2){
				queryVO.setSortField("a.issue_num");
			}else if(queryVO.getSortCondition() == 1){/**命中率*/
				queryVO.setSortField("a.hit_rate");
			}else{//最大连红 ，需要变更 2017.12.20
				queryVO.setSortField("a.continue_hit");//最大连红
			}
		}
		//排序
		queryVO.setSortOrder(OrderCopyEnum.OrderByTypeEnum.DESC.getValue());
		
		//查询
		PagingBO<QueryUserIssueInfoBO> pageData = pageService.getPageData(queryVO, new ISimplePage<QueryUserIssueInfoBO>(){

			@Override
			public int getTotal() {
				return mUserIssueInfoDaoMapper.selectByConditionCount(queryVO);
			}

			@Override
			public List<QueryUserIssueInfoBO> getData() {
				return mUserIssueInfoDaoMapper.selectByCondition(queryVO);
			}

        });
		List<QueryUserIssueInfoBO> list = pageData.getData();
		logger.info("查询到关注信息：count= " + pageData.getTotal() + " 条");
		logger.info("查询到关注信息：detailList=" + list.size() + " 条");
		
		if(!ObjectUtil.isBlank(list)){
        	for(QueryUserIssueInfoBO bean : list){
        		/**查询当前未截止可跟投的方案数量： 有推荐*/
//        		List<Map<Integer,Long>> listIssueNum = orderIssueInfoDaoMapper.getNumOfOrderIssue(Arrays.asList(bean.getId()));
//        		if(!ObjectUtil.isBlank(listIssueNum)){
//        			bean.setUpdateNum(listIssueNum.get(0).get("issueNum").intValue());
//        		}
        		if(queryVO.getQueryType() == 2){
        			bean.setLevel(mUserIssueLevelService.getUserIssueLevel(bean.getUserId()));
        		}
        		/** 处理显示中文、数字格式    已前移到lotto*/
//        		bean.setFocusNumStr(IssueUtil.replaceMantissa(bean.getFocusNum()));
//        		bean.setRecentRecord(IssueUtil.getRecentRecordStr(bean.getRecentRecord()));
//        		if(!ObjectUtil.isBlank(bean.getBonusRateDb())){
//        			bean.setBonusRate(IssueUtil.getOnlyPercent(Double.valueOf(bean.getBonusRateDb()+SymbolConstants.ENPTY_STRING)));
//        		}
//        		bean.setHitRate(IssueUtil.getOnlyPercent(bean.getHitRateDb()));
//        		bean.setContinueHit(IssueUtil.getContinueHitStr(bean.getContinueHitDb()));
//				bean.setHeadUrl(UserInfoBOUtil.getHeadUrl(bean.getHeadUrl(),beforeFileUrl));

        	}
        }
		if(queryVO.getQueryType() == 3){
			List<QueryUserIssueInfoBO> retList = pageData.getData();
			Collections.shuffle(retList);
			return ResultBO.ok(retList);
		}
		return ResultBO.ok(pageData.getData());
	}

	@Override
	public int queryUserIssueInfoCount(QueryVO queryVO) {
		return mUserIssueInfoDaoMapper.selectByConditionCount(queryVO);
	}


    /***
     * 更新用户发单信息
     * @param vo
     * @return
     */
    @Override
    public int updateUserIssueInfo(MUserIssueInfoVO vo) {
        return mUserIssueInfoDaoMapper.updateByPrimaryKeySelective(new MUserIssueInfoPO(vo));
    }


    /**
     * 查询发单用户进7天统计信息
     *
     * @param vo
     * @return
     */
    @Override
    public ResultBO<?> findUserIssuePrizeCount(MUserIssueInfoVO vo) {

        if (ObjectUtil.isBlank(vo.getLotteryCode())) {
            vo.setLotteryCode(String.valueOf(LotteryEnum.Lottery.FB.getName()));
        }

        if (!StringUtil.isBlank(vo.getToken())) {
            ResultBO resultBO = userInfoCacheService.checkToken(vo.getToken());
            if (resultBO.isError()) {
                return ResultBO.err();
            }
            UserInfoBO userInfoBO = (UserInfoBO) resultBO.getData();
            MUserIssueInfoBO bo = mUserIssueInfoDaoMapper.findUserIssueInfoByUserId(userInfoBO.getId().longValue());
            if (!ObjectUtil.isBlank(bo)) {
                vo.setId(bo.getId());
            }
        }
        List<MUserIssueCountPrizeViewBO> prizeList = mUserIssueInfoDaoMapper.selectIssueUserPrizeOrder(vo);

        if (prizeList.isEmpty()) {
            return ResultBO.ok(prizeList);
        }

        return ResultBO.ok(prizeList);
    }

	@Override
	public List<CommissionBO> getCommissionDetailsSumCommission(QueryVO queryVO) {
		return mUserIssueInfoDaoMapper.getCommissionDetailsSumCommission(queryVO);
	}

}
