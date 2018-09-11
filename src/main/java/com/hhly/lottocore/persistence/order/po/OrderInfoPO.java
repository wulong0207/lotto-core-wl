package com.hhly.lottocore.persistence.order.po;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;

/**
 * @author huangb
 *
 * @Date 2016年11月30日
 *
 * @Desc 订单信息
 */
public class OrderInfoPO {
	/**
	 * 编号
	 */
	private Long id;
	/**
	 * 订单编号
	 */
	private String orderCode;
	/**
	 * 彩种代码
	 */
	private Integer lotteryCode;
	/**
	 * 彩种名称
	 */
	private String lotteryName;
	/**
	 * 子玩法代码
	 */
	private Integer lotteryChildCode;
	/**
	 * 彩期
	 */
	private String lotteryIssue;
	/**
	 * 开奖号码
	 */
	private String drawCode;
	/**
	 * 购买下单时间
	 */
	private Date buyTime;
	/**
	 * 高频，数字彩，老足彩：取lottery_issue official_end_time； 其它竞技彩：取lottery_issue
	 * official_end_time与订单中最早开赛赛事的比赛时间进行比较，若大于，取比赛时间，若小于取official_end_time
	 */
	private Date endTicketTime;
	/**
	 * lottery_type的end_check_time 与 end_ticket_time 进行计算。
	 */
	private Date endCheckTime;
	/**
	 * 订单有所票出票成功后 的时间
	 */
	private Date comeOutTime;
	/**
	 * 此订单完成开奖的时间
	 */
	private Date lotteryTime;
	/**
	 * 此订单完成派奖的时间
	 */
	private Date sendTime;
	/**
	 * 用户
	 */
	private Integer userId;
	/**
	 * 订单总额
	 */
	private Double orderAmount;
	/**
	 * 此订单总倍数
	 */
	private Integer multipleNum;
	/**
	 * 拆票的总数量
	 */
	private Integer splitNum;
	/**
	 * 数字彩：中奖等级，竞技彩：过关方式；高频彩：玩法名称
	 */
	private String winningDetail;
	/**
	 * 税前奖金
	 */
	private Double preBonus;
	/**
	 * 税后奖金
	 */
	private Double aftBonus;
	/**
	 * 1：代购；2：追号；3：合买
	 */
	private Short buyType;
	/**
	 * 1：等待支付；2：支付成功；3：未支付过期；4：支付失败；5：用户取消；6：退款
	 */
	private Short payStatus;
	/**
	 * 1：待上传；2：待拆票；3：拆票中；4：待出票；5:出票中；6：已出票；7：出票失败
	 */
	private Short orderStatus;
	/**
	 * 1：未开奖；2：未中奖；3：已中奖；4：已派奖
	 */
	private Short winningStatus;
	/**
	 * 渠道
	 */
	private String channelId;
	/**
	 * 开奖后生成的加奖奖金
	 */
	private Double addedBonus;
	/**
	 * 支付时使用的红包编号
	 */
	private String redCodeUsed;
	/**
	 * 开奖后生成的优惠券中的红包编号(系统自动发放的红包编号)
	 */
	private String redCodeGet;
	
	/**
	 * 活动ID
	 */
	private String activitySource;
	/**
	 * 修改时间
	 */
	private Date modifyTime;
	/**
	 * 修改人
	 */
	private String modifyBy;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 否允许检票:0：不允许，1：允许
	 */
	private Short checkTicket;
	/**
	 * 是否大乐透加号0：否；1：是
	 */
	private Short isDltAdd;
	
	private String maxBuyScreen;
	
	/**
	 * 竞技彩购买的场次编号
	 */
	private String buyScreen;
	/**
	 * 订单的系统截止时间
	 */
	private Date endSysTime;
	
	/**
	 * 总注数
	 */
	private Integer buyNumber;
	
	/**
	 * 平台
	 */
	private Short platform;
	/**
	 * 单式上传文件地址
	 */
	private String bettingContentUrl;
	/**
	 * 投注类型
	 */
	private Short categoryId;

	/**
	 * 抄单类型
	 */
	private Short orderType;
	
	/**
	 * 本站截止时间
	 */
	private Date endLocalTime;
	/**
	 * 预计最大奖金
	 */
	private String maxBonus;

	/**
	 * 兑换码
	 */
	private String redeemCode;
	
	
	public Short getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Short categoryId) {
		this.categoryId = categoryId;
	}

	public Short getPlatform() {
		return platform;
	}

	public void setPlatform(Short platform) {
		this.platform = platform;
	}

	public String getBettingContentUrl() {
		return bettingContentUrl;
	}

	public void setBettingContentUrl(String bettingContentUrl) {
		this.bettingContentUrl = bettingContentUrl;
	}

	public Integer getBuyNumber() {
		return buyNumber;
	}

	public void setBuyNumber(Integer buyNumber) {
		this.buyNumber = buyNumber;
	}

	private List<OrderDetailPO> orderDetailList;
	public OrderInfoPO() {
	}

	public OrderInfoPO(OrderInfoVO orderInfo) {
		this.orderCode = orderInfo.getOrderCode();
		this.lotteryCode = orderInfo.getLotteryCode();
		this.lotteryName = orderInfo.getLotteryName();
		this.lotteryIssue = orderInfo.getLotteryIssue();
		this.lotteryChildCode = orderInfo.getLotteryChildCode();
		this.drawCode = orderInfo.getDrawCode();
		this.userId = orderInfo.getUserId();
		this.orderAmount = orderInfo.getOrderAmount();
		this.multipleNum = orderInfo.getMultipleNum();
		this.buyType = orderInfo.getBuyType();
		this.payStatus = orderInfo.getPayStatus();
		this.orderStatus = orderInfo.getOrderStatus();
		this.channelId = orderInfo.getChannelId();
		this.isDltAdd = orderInfo.getIsDltAdd();
		this.buyScreen = orderInfo.getBuyScreen();
		this.endSysTime = orderInfo.getEndSysTime();
		this.endCheckTime = orderInfo.getEndCheckTime();
		this.endTicketTime = orderInfo.getEndTicketTime();
		this.winningStatus = orderInfo.getWinningStatus();
		this.buyNumber = orderInfo.getBuyNumber();
		this.platform = orderInfo.getPlatform();
		this.bettingContentUrl = orderInfo.getBettingContentUrl();
		this.categoryId = orderInfo.getCategoryId();
		this.activitySource = orderInfo.getActivityCode();
		this.orderType = orderInfo.getOrderType();
		this.endLocalTime = orderInfo.getEndLocalTime();
		this.maxBonus = orderInfo.getMaxBonus();
		this.redeemCode = orderInfo.getRedeemCode();
		Set<Integer> lotteryChildSet = new HashSet<Integer>();
		if (orderInfo.getOrderDetailList() == null || orderInfo.getOrderDetailList().isEmpty()) {
			this.orderDetailList = Collections.emptyList();
		} else {
			List<OrderDetailPO> orderDtlList = new ArrayList<OrderDetailPO>(orderInfo.getOrderDetailList().size());
			OrderDetailPO orderDtl = null;
			for (OrderDetailVO temp : orderInfo.getOrderDetailList()) {
				if(!ObjectUtil.isBlank(temp.getLotteryChildCode())){//收集子玩法
					lotteryChildSet.add(temp.getLotteryChildCode());
				}
				orderDtl = new OrderDetailPO(this.orderCode, temp);//设置订单编号
				if(JCZQConstants.checkLotteryId(orderInfo.getLotteryCode()) || JCLQConstants.checkLotteryId(orderInfo.getLotteryCode()) || BJDCConstants.checkLotteryId(orderInfo.getLotteryChildCode())){
					dealBuyScreen(orderInfo,orderDtl);
				}
//				orderDtl.setBuyScreen(this.buyScreen);//设置场次编号
				orderDtlList.add(orderDtl);
			}
			this.orderDetailList = orderDtlList;
		}
		if(JCLQConstants.checkLotteryId(orderInfo.getLotteryCode())){
			this.lotteryCode = Lottery.BB.getName();
			this.lotteryChildCode = orderInfo.getLotteryCode();
		}
		if(JCZQConstants.checkLotteryId(orderInfo.getLotteryCode())){
			this.lotteryCode = Lottery.FB.getName();
			this.lotteryChildCode = orderInfo.getLotteryCode();

		}
		//截止出票时间比最终检票时间早，取最早时间
//		if(this.endTicketTime.before(this.endCheckTime)){
//			this.endCheckTime = this.endTicketTime;
//		}
		//支付截止时间 ，处理计算过程： 官方截止  - 超注数需提前时间  + 缓冲支付时间
//		Date payEndTime = Constants.getOrderPayEndTime(this.lotteryCode, DateUtil.getDifferenceTime(orderInfo.getEndTicketTime(), orderInfo.getEndSysTime()), orderInfo.getEndSysTime());
//		this.endSysTime = payEndTime;
		//竞足、竞篮混投
		if(orderInfo.getLotteryCode() == JCZQConstants.ID_FHT || orderInfo.getLotteryCode() == JCLQConstants.ID_JCLQ_HHGG){
			//奖金优化方案种含有两个以上子玩法就为混投，否则orderInfo 的lotteryCode设置为子玩法的编号
		    if(!ObjectUtil.isBlank(lotteryChildSet) && lotteryChildSet.size() == 1){
		    	this.lotteryChildCode = orderInfo.getOrderDetailList().get(0).getLotteryChildCode();
		    }
		}
		//最大购买场次编号
		if(!ObjectUtil.isBlank(orderInfo.getBuyScreen())){
			List<String> list = new ArrayList<String>();
			for(String item : orderInfo.getBuyScreen().split(SymbolConstants.COMMA)){
				list.add(item);
			}
			Collections.sort(list, new Comparator<String>() {
				@Override
				public int compare(String strOne, String strTwo) {
					return Double.valueOf(strTwo).compareTo(Double.valueOf(strOne));
				}
			});
			this.maxBuyScreen = list.get(0);
		}
	}
	/**
	 * 处理：购买赛事编号与投注内容不一致问题
	 * @author longguoyou
	 * @date 2017年7月10日
	 * @param orderInfo
	 * @param orderDtl
	 */
	private static void dealBuyScreen(OrderInfoVO orderInfo, OrderDetailPO orderDtl){
		//订单购买赛事编号
		String[] buyScreens = orderInfo.getBuyScreen().split(SymbolConstants.COMMA);
		//存放投注内容解析的赛事编号
		StringBuffer bufferScreens = new StringBuffer();
		//解析投注内容
		String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(orderDtl.getPlanContent());//解析投注详情
		//替换“#”号
		if(contents[0].contains(SymbolConstants.NUMBER_SIGN)){
			contents[0] = contents[0].replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
		}
		//投注内容的场次编号
		String[] betContent = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
		for(int j = 0; j < betContent.length; j++){
			bufferScreens.append(betContent[j].substring(0, Constants.getLengthOfSystemCode(orderInfo.getLotteryCode())));
			bufferScreens.append(SymbolConstants.COMMA);
		}
		//解析完毕投注内容赛事编号字符串，有逗号分隔
		String betScreensStr = bufferScreens.toString().substring(0, bufferScreens.toString().length()-1);
		//解析完毕的赛事编号字符串数组
		String[] betScreens = betScreensStr.split(SymbolConstants.COMMA);
		//单式上传投注内容赛事编号和购买赛事编号不一致的情况
		if(buyScreens.length != betScreens.length){
			//防止顺序乱，从赛事编号小到大排序
			Arrays.sort(betScreens);
			orderDtl.setBuyScreen(betScreensStr.toString());
		}else{
			orderDtl.setBuyScreen(orderInfo.getBuyScreen());
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	

	public Integer getLotteryCode() {
        return lotteryCode;
    }

    public void setLotteryCode(Integer lotteryCode) {
        this.lotteryCode = lotteryCode;
    }

    public String getLotteryName() {
		return lotteryName;
	}

	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}
	
	public Integer getLotteryChildCode() {
		return lotteryChildCode;
	}

	public void setLotteryChildCode(Integer lotteryChildCode) {
		this.lotteryChildCode = lotteryChildCode;
	}

	public String getLotteryIssue() {
		return lotteryIssue;
	}

	public void setLotteryIssue(String lotteryIssue) {
		this.lotteryIssue = lotteryIssue;
	}

	public String getDrawCode() {
		return drawCode;
	}

	public void setDrawCode(String drawCode) {
		this.drawCode = drawCode;
	}

	public Date getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(Date buyTime) {
		this.buyTime = buyTime;
	}

	public Date getEndTicketTime() {
		return endTicketTime;
	}

	public void setEndTicketTime(Date endTicketTime) {
		this.endTicketTime = endTicketTime;
	}

	public Date getEndCheckTime() {
		return endCheckTime;
	}

	public void setEndCheckTime(Date endCheckTime) {
		this.endCheckTime = endCheckTime;
	}

	public Date getComeOutTime() {
		return comeOutTime;
	}

	public void setComeOutTime(Date comeOutTime) {
		this.comeOutTime = comeOutTime;
	}

	public Date getLotteryTime() {
		return lotteryTime;
	}

	public void setLotteryTime(Date lotteryTime) {
		this.lotteryTime = lotteryTime;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}


	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Integer getMultipleNum() {
		return multipleNum;
	}

	public void setMultipleNum(Integer multipleNum) {
		this.multipleNum = multipleNum;
	}

	public Integer getSplitNum() {
		return splitNum;
	}

	public void setSplitNum(Integer splitNum) {
		this.splitNum = splitNum;
	}

	public String getWinningDetail() {
		return winningDetail;
	}

	public void setWinningDetail(String winningDetail) {
		this.winningDetail = winningDetail;
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

	public Short getBuyType() {
		return buyType;
	}

	public void setBuyType(Short buyType) {
		this.buyType = buyType;
	}

	public Short getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Short payStatus) {
		this.payStatus = payStatus;
	}

	public Short getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Short orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Short getWinningStatus() {
		return winningStatus;
	}

	public void setWinningStatus(Short winningStatus) {
		this.winningStatus = winningStatus;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Double getAddedBonus() {
		return addedBonus;
	}

	public void setAddedBonus(Double addedBonus) {
		this.addedBonus = addedBonus;
	}

	public String getActivitySource() {
		return activitySource;
	}

	public void setActivitySource(String activitySource) {
		this.activitySource = activitySource;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<OrderDetailPO> getOrderDetailList() {
		return orderDetailList;
	}

	public void setOrderDetailList(List<OrderDetailPO> orderDetailList) {
		this.orderDetailList = orderDetailList;
	}
	public Short getCheckTicket() {
		return checkTicket;
	}

	public void setCheckTicket(Short checkTicket) {
		this.checkTicket = checkTicket;
	}

	public Short getIsDltAdd() {
		return isDltAdd;
	}

	public void setIsDltAdd(Short isDltAdd) {
		this.isDltAdd = isDltAdd;
	}
	
	public String getMaxBuyScreen() {
		return maxBuyScreen;
	}

	public void setMaxBuyScreen(String maxBuyScreen) {
		this.maxBuyScreen = maxBuyScreen;
	}

	public String getBuyScreen() {
		return buyScreen;
	}

	public void setBuyScreen(String buyScreen) {
		this.buyScreen = buyScreen;
	}

	public Date getEndSysTime() {
		return endSysTime;
	}

	public void setEndSysTime(Date endSysTime) {
		this.endSysTime = endSysTime;
	}

	public String getRedCodeUsed() {
		return redCodeUsed;
	}

	public void setRedCodeUsed(String redCodeUsed) {
		this.redCodeUsed = redCodeUsed;
	}

	public String getRedCodeGet() {
		return redCodeGet;
	}

	public void setRedCodeGet(String redCodeGet) {
		this.redCodeGet = redCodeGet;
	}

	public Short getOrderType() {
		return orderType;
	}

	public void setOrderType(Short orderType) {
		this.orderType = orderType;
	}

	public Date getEndLocalTime() {
		return endLocalTime;
	}

	public void setEndLocalTime(Date endLocalTime) {
		this.endLocalTime = endLocalTime;
	}

	public String getMaxBonus() {
		return maxBonus;
	}

	public void setMaxBonus(String maxBonus) {
		this.maxBonus = maxBonus;
	}

	public String getRedeemCode() {
		return redeemCode;
	}

	public void setRedeemCode(String redeemCode) {
		this.redeemCode = redeemCode;
	}

	@Override
	public String toString() {
		return "OrderInfoPO [id=" + id + ", orderCode=" + orderCode + ", lotteryCode=" + lotteryCode + ", lotteryName="
				+ lotteryName + ", lotteryChildCode=" + lotteryChildCode + ", lotteryIssue=" + lotteryIssue
				+ ", drawCode=" + drawCode + ", buyTime=" + buyTime + ", endTicketTime=" + endTicketTime
				+ ", endCheckTime=" + endCheckTime + ", comeOutTime=" + comeOutTime + ", lotteryTime=" + lotteryTime
				+ ", sendTime=" + sendTime + ", userId=" + userId + ", orderAmount=" + orderAmount + ", multipleNum="
				+ multipleNum + ", splitNum=" + splitNum + ", winningDetail=" + winningDetail + ", preBonus=" + preBonus
				+ ", aftBonus=" + aftBonus + ", buyType=" + buyType + ", payStatus=" + payStatus + ", orderStatus="
				+ orderStatus + ", winningStatus=" + winningStatus + ", channelId=" + channelId + ", addedBonus="
				+ addedBonus + ", redCodeUsed=" + redCodeUsed + ", redCodeGet=" + redCodeGet + ", activitySource="
				+ activitySource + ", modifyTime=" + modifyTime + ", modifyBy=" + modifyBy + ", updateTime="
				+ updateTime + ", createTime=" + createTime + ", remark=" + remark + ", checkTicket=" + checkTicket
				+ ", isDltAdd=" + isDltAdd + ", maxBuyScreen=" + maxBuyScreen + ", buyScreen=" + buyScreen
				+ ", endSysTime=" + endSysTime + ", buyNumber=" + buyNumber + ", orderDetailList=" + orderDetailList
				+ "]";
	}
	
	
    public static void main(String[] args) {
		String[] ss = {"1702101","1702100"};
		Arrays.sort(ss);
		for(String sss: ss){
			System.out.println(sss);
			
		}
	}
}
