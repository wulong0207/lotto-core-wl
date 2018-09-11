package com.hhly.lottocore.persistence.order.po;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.hhly.skeleton.base.common.ChaseEnum.ChaseStatus;
import com.hhly.skeleton.base.common.OrderEnum.PayStatus;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddIssueVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @author huangb
 *
 * @Date 2016年12月20日
 *
 * @Desc 追号计划信息
 */
public class OrderAddPO {

	/**
	 * 增长ID
	 */
	private Long id;
	/**
	 * 自定义追号编号
	 */
	private String orderAddCode;
	/**
	 * 彩种编号
	 */
	private Integer lotteryCode;
	/**
	 * 彩期编号
	 */
	private String issueCode;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 用户购买下单时间
	 */
	private Date buyTime;
	/**
	 * 追号计划结束的时间
	 */
	private Date endTime;
	/**
	 * 渠道表自定义的ID
	 */
	private String channelId;
	/**
	 * 追号金额
	 */
	private Double addAmount;
	/**
	 * 方案倍数(该倍数无实际操作意义，仅仅是各追号子彩期倍数的和)
	 */
	private Integer multipleNum;
	/**
	 * 追号类型； 1：固定选号；2：随机选号
	 */
	private Short addType;
	/**
	 * 追号投注数(主要保存随机追号时的注数；选号追号也可以保留注数)
	 */
	private Integer addCount;
	/**
	 * 中奖税前奖金
	 */
	private Double preBonus;
	/**
	 * 中奖税后奖金
	 */
	private Double aftBonus;
	/**
	 * 活动来源ID
	 */
	private String activityId;
	/**
	 * 支付状态； 1：等待支付；2：支付成功；3：未支付过期；4：支付失败；5：用户取消；6：退款
	 */
	private Short payStatus;
	/**
	 * 追号状态； 1：追号中；2：中奖停追；3：追号结束；4：用户撤单；5：系统撤单；
	 */
	private Short addStatus;
	/**
	 * 停止类型； 1：奖项；2：金额；3：永追
	 */
	private Short stopType;
	/**
	 * 追号条件；例如：奖项：二等奖；永追：空；金额：5000
	 */
	private String stopCondition;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 是否大乐透追加0：否；1：是
	 */
	private Short isDltAdd;
	/**
	 * 追号总期数
	 */
	private Integer issueAmount;
	/**
	 * 已追期数
	 */
	private Integer hadIssue;
	/**
	 * 红包CODE
	 */
	private String redCode;

	/**
	 * 追号内容列表
	 */
	private List<OrderAddContentPO> orderAddContentList;
	/**
	 * 追号期号列表
	 */
	private List<OrderAddIssuePO> orderAddIssueList;
	/********* 20170525 add ********/
	/**
	 * 支付截止时间
	 */
	private Date payEndTime;
	/**
	 * 是否包含当前期 0:否， 1:是
	 */
	private Short containCurIssue;
	/********* 20170607 add ********/
	/**
	 * 投注平台:1：Web,2:Wap;3:Android;4:IOS;
	 */
	private Short platform;

	public OrderAddPO() {
	}

	/**
	 * 添加追号计划用的构造方法
	 * @param orderAddCode 自定义追号编号
	 * @param orderAdd
	 */
	public OrderAddPO(String orderAddCode, OrderAddVO orderAdd) {
		this.orderAddCode = orderAddCode;
		this.lotteryCode = orderAdd.getLotteryCode();
		this.issueCode = orderAdd.getIssueCode();
		this.userId = orderAdd.getUserId();
		this.channelId = orderAdd.getChannelId();
		this.addAmount = orderAdd.getAddAmount();
		this.multipleNum = orderAdd.getMultipleNum();
		this.addType = orderAdd.getAddType();
		this.addCount = orderAdd.getAddCount();
		this.activityId = orderAdd.getActivityId();
		this.payStatus = PayStatus.WAITING_PAY.getValue();
		this.addStatus = ChaseStatus.CHASING.getValue();
		this.stopType = orderAdd.getStopType();
		this.stopCondition = orderAdd.getStopCondition();
		this.isDltAdd = orderAdd.getIsDltAdd();
		if (ObjectUtil.isBlank(orderAdd.getOrderAddContentList())) {
			this.orderAddContentList = Collections.emptyList();
		} else {
			List<OrderAddContentPO> orderAddCntList = new ArrayList<OrderAddContentPO>(orderAdd.getOrderAddContentList().size());
			OrderAddContentPO orderAddCnt = null;
			for (OrderDetailVO temp : orderAdd.getOrderAddContentList()) {
				orderAddCnt = new OrderAddContentPO(this.orderAddCode, temp);
				orderAddCntList.add(orderAddCnt);
			}
			this.orderAddContentList = orderAddCntList;
		}
		if (ObjectUtil.isBlank(orderAdd.getOrderAddIssueList())) {
			this.orderAddIssueList = Collections.emptyList();
		} else {
			List<OrderAddIssuePO> orderAddIssList = new ArrayList<OrderAddIssuePO>(orderAdd.getOrderAddIssueList().size());
			OrderAddIssuePO orderAddIss = null;
			// 追号彩期排序，确定头期和尾期
			Collections.sort(orderAdd.getOrderAddIssueList(), new Comparator<OrderAddIssueVO>() {
				@Override
				public int compare(OrderAddIssueVO o1, OrderAddIssueVO o2) {
					if (o1.getIssueCode().compareTo(o2.getIssueCode()) > Constants.NUM_0) {
						return 1;
					} else if(o1.getIssueCode().compareTo(o2.getIssueCode()) < Constants.NUM_0) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			// 设置彩期标识
			int i = Constants.NUM_1;
			for (OrderAddIssueVO temp : orderAdd.getOrderAddIssueList()) {
				orderAddIss = new OrderAddIssuePO(this.orderAddCode, temp, i);
				orderAddIssList.add(orderAddIss);
				i++;
			}
			this.orderAddIssueList = orderAddIssList;
		}
		this.issueAmount = this.orderAddIssueList.size();
		this.hadIssue = Constants.NUM_0;
		this.payEndTime = orderAdd.getPayEndTime();
		this.containCurIssue = orderAdd.getContainCurIssue();
		this.platform = orderAdd.getPlatform();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderAddCode() {
		return orderAddCode;
	}

	public void setOrderAddCode(String orderAddCode) {
		this.orderAddCode = orderAddCode;
	}

	public Integer getLotteryCode() {
		return lotteryCode;
	}

	public void setLotteryCode(Integer lotteryCode) {
		this.lotteryCode = lotteryCode;
	}

	public String getIssueCode() {
		return issueCode;
	}

	public void setIssueCode(String issueCode) {
		this.issueCode = issueCode;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(Date buyTime) {
		this.buyTime = buyTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Double getAddAmount() {
		return addAmount;
	}

	public void setAddAmount(Double addAmount) {
		this.addAmount = addAmount;
	}

	public Integer getMultipleNum() {
		return multipleNum;
	}

	public void setMultipleNum(Integer multipleNum) {
		this.multipleNum = multipleNum;
	}

	public Short getAddType() {
		return addType;
	}

	public void setAddType(Short addType) {
		this.addType = addType;
	}

	public Double getPreBonus() {
		return preBonus;
	}

	public void setPreBonus(Double preBonus) {
		this.preBonus = preBonus;
	}

	public Double getAftBonus() {
		return aftBonus;
	}

	public void setAftBonus(Double aftBonus) {
		this.aftBonus = aftBonus;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Short getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Short payStatus) {
		this.payStatus = payStatus;
	}

	public Short getAddStatus() {
		return addStatus;
	}

	public void setAddStatus(Short addStatus) {
		this.addStatus = addStatus;
	}

	public Short getStopType() {
		return stopType;
	}

	public void setStopType(Short stopType) {
		this.stopType = stopType;
	}

	public String getStopCondition() {
		return stopCondition;
	}

	public void setStopCondition(String stopCondition) {
		this.stopCondition = stopCondition;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getAddCount() {
		return addCount;
	}

	public void setAddCount(Integer addCount) {
		this.addCount = addCount;
	}

	public Short getIsDltAdd() {
		return isDltAdd;
	}

	public void setIsDltAdd(Short isDltAdd) {
		this.isDltAdd = isDltAdd;
	}

	public Integer getIssueAmount() {
		return issueAmount;
	}

	public void setIssueAmount(Integer issueAmount) {
		this.issueAmount = issueAmount;
	}

	public Integer getHadIssue() {
		return hadIssue;
	}

	public void setHadIssue(Integer hadIssue) {
		this.hadIssue = hadIssue;
	}

	public String getRedCode() {
		return redCode;
	}

	public void setRedCode(String redCode) {
		this.redCode = redCode;
	}

	public List<OrderAddContentPO> getOrderAddContentList() {
		return orderAddContentList;
	}

	public void setOrderAddContentList(List<OrderAddContentPO> orderAddContentList) {
		this.orderAddContentList = orderAddContentList;
	}

	public List<OrderAddIssuePO> getOrderAddIssueList() {
		return orderAddIssueList;
	}

	public void setOrderAddIssueList(List<OrderAddIssuePO> orderAddIssueList) {
		this.orderAddIssueList = orderAddIssueList;
	}

	public Date getPayEndTime() {
		return payEndTime;
	}

	public void setPayEndTime(Date payEndTime) {
		this.payEndTime = payEndTime;
	}

	public Short getContainCurIssue() {
		return containCurIssue;
	}

	public void setContainCurIssue(Short containCurIssue) {
		this.containCurIssue = containCurIssue;
	}

	public Short getPlatform() {
		return platform;
	}

	public void setPlatform(Short platform) {
		this.platform = platform;
	}
	
}
