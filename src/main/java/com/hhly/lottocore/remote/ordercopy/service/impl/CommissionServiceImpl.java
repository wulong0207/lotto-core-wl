package com.hhly.lottocore.remote.ordercopy.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueInfoDaoMapper;
import com.hhly.lottocore.remote.ordercopy.service.CommissionService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.Assert;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.lotto.base.ordercopy.bo.CommissionBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.QueryVO;
@Service("commissionService")
public class CommissionServiceImpl implements CommissionService {
	
    private static Logger logger = LoggerFactory.getLogger(CommissionServiceImpl.class);
	
	@Autowired
	private IPageService pageService;
	
	@Autowired
	private MUserIssueInfoDaoMapper mUserIssueInfoDaoMapper;

	@Override
	public ResultBO<?> queryCommissions(final QueryVO queryVO) {
	    //判断
		Assert.paramNotNull(queryVO.getPageSize(), "pageSize");
		Assert.paramNotNull(queryVO.getPageIndex(), "pageIndex");
	    Assert.paramNotNull(queryVO.getToken(), "token");
	    Assert.paramNotNull(queryVO.getDaysNum(), "daysNum");
	    //设值
	    queryVO.setBeginTime(DateUtil.getBeginTime(DateUtil.addHour(new Date(), - 24 * (queryVO.getDaysNum()-1))));
	    queryVO.setEndTime(DateUtil.getEndTime(DateUtil.getNowDate()));
		PagingBO<CommissionBO> pageData = pageService.getPageData(queryVO,
				new ISimplePage<CommissionBO>() {
					@Override
					public int getTotal() {
						return mUserIssueInfoDaoMapper.queryCommissionCount(queryVO);
					}

					@Override
					public List<CommissionBO> getData() {
						return mUserIssueInfoDaoMapper.queryCommission(queryVO);
					}
				});
		List<CommissionBO> list = pageData.getData();
		logger.info("查询到返佣情况信息：count= " + pageData.getTotal() + " 条");
		logger.info("查询到返佣情况信息：detailList=" + list.size() + " 条");
		//已前移到lotto
//		if(!ObjectUtil.isBlank(list)){
//        	for(CommissionBO bean : list){
//        		bean.setCommissionAmount(MathUtil.round(bean.getCommissionAmount(), 1));
//        		bean.setCreateTimeStr(DateUtil.convertDateToStr(bean.getCreateTime(), DateUtil.FORMAT_MM_DD));
//        		bean.setCreateTime(null);
//        		bean.setFollowAmount(MathUtil.round(bean.getFollowAmount(), 1));
//        	}
//		}
		return ResultBO.ok(pageData.getData());
	}

	@Override
	public ResultBO<?> queryCommissionsDetails(final QueryVO queryVO) {
		//判断
		Assert.paramNotNull(queryVO.getPageSize(), "pageSize");
		Assert.paramNotNull(queryVO.getPageIndex(), "pageIndex");
		Assert.paramNotNull(queryVO.getOrderCode(), "orderCode");
		//设值
		PagingBO<CommissionBO> pageData = pageService.getPageData(queryVO,
				new ISimplePage<CommissionBO>() {
					@Override
					public int getTotal() {
						return mUserIssueInfoDaoMapper.queryCommissionDetailsCount(queryVO);
					}

					@Override
					public List<CommissionBO> getData() {
						return mUserIssueInfoDaoMapper.queryCommissionDetails(queryVO);
					}
				});
		logger.info("查询到返佣明细信息：count= " + pageData.getTotal() + " 条");
		logger.info("查询到返佣明细信息：detailList=" + pageData.getData().size() + " 条");
		return ResultBO.ok(pageData.getData());
	}

	@Override
	public int queryCommissionsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int queryCommissionsDetailsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<CommissionBO> getCommissionDetailsSumCommission(QueryVO queryVO) {
		return mUserIssueInfoDaoMapper.getCommissionDetailsSumCommission(queryVO);
	}
}
