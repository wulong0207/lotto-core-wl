package com.hhly.lottocore.persistence.order.po;

import java.util.Date;

import com.hhly.skeleton.base.common.LotteryChildEnum.LotteryChild;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @author huangb
 *
 * @Date 2016年11月30日
 *
 * @Desc 订单明细
 */
public class OrderDetailPO {
	/**
	 * 编号
	 */
	private Long id;
	/**
	 * 订单编号
	 */
	private String orderCode;
	/**
	 * 上传的投注内容地址
	 */
	private String bettingContentUrl;
	/**
	 * 竞技彩购买的场次编号
	 */
	private String buyScreen;
	/**
	 * 数字彩，低频彩所选的投注号码；若为上传，需要保存选好方式
	 */
	private String planContent;
	/**
	 * 单个方案的购买倍数
	 */
	private Integer multiple;
	/**
	 * 单个方案的购买金额
	 */
	private Double amount;
	/**
	 * 玩法 ：彩种表里子玩法的名字
	 */
	private String playIntro;
	/**
	 * 选号方式 1：手选；2：机选；3：上传；
	 */
	private Integer codeWay;
	/**
	 * 内容类型 1：单式；2：复式；3：胆拖；4：混合；5：上传
	 */
	private Integer contentType;
	/**
	 * 子彩种ID
	 */
	private Integer lotteryChildCode;
	/**
	 * 注数
	 */
	private Integer buyNumber;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;

	public OrderDetailPO() {
	}

	/**
	 * 构造方法
	 * 
	 * @param orderDetail
	 *            订单明细内容
	 */
	public OrderDetailPO(OrderDetailVO orderDetail) {
		this.orderCode = orderDetail.getOrderCode();
		this.bettingContentUrl = orderDetail.getBettingContentUrl();
		this.buyScreen = orderDetail.getBuyScreen();
		this.planContent = orderDetail.getPlanContent();
		this.multiple = orderDetail.getMultiple();
		this.amount = orderDetail.getAmount();
		this.codeWay = orderDetail.getCodeWay();
		this.contentType = orderDetail.getContentType();
		this.lotteryChildCode = orderDetail.getLotteryChildCode();
		this.updateTime = orderDetail.getUpdateTime();
		this.createTime = orderDetail.getCreateTime();
		// 玩法名称字段后续可能会干掉,有玩法id就行了
		if (!ObjectUtil.isBlank(orderDetail.getLotteryChildCode())) {
			LotteryChild lotteryChild = LotteryChild.valueOf(orderDetail.getLotteryChildCode());
			if(!ObjectUtil.isBlank(lotteryChild)){
				this.playIntro = lotteryChild.getDesc();
			}
		}
		this.buyNumber = orderDetail.getBuyNumber();
	}

	/**
	 * 构造方法
	 * 
	 * @param orderCode
	 *            订单编号
	 * @param orderDetail
	 *            订单明细内容
	 */
	public OrderDetailPO(String orderCode, OrderDetailVO orderDetail) {
		this(orderDetail);
		this.orderCode = orderCode;
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

	public String getBettingContentUrl() {
		return bettingContentUrl;
	}

	public void setBettingContentUrl(String bettingContentUrl) {
		this.bettingContentUrl = bettingContentUrl;
	}

	public String getBuyScreen() {
		return buyScreen;
	}

	public void setBuyScreen(String buyScreen) {
		this.buyScreen = buyScreen;
	}

	public String getPlanContent() {
		return planContent;
	}

	public void setPlanContent(String planContent) {
		this.planContent = planContent;
	}

	public Integer getMultiple() {
		return multiple;
	}

	public void setMultiple(Integer multiple) {
		this.multiple = multiple;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getPlayIntro() {
		return playIntro;
	}

	public void setPlayIntro(String playIntro) {
		this.playIntro = playIntro;
	}

	public Integer getCodeWay() {
		return codeWay;
	}

	public void setCodeWay(Integer codeWay) {
		this.codeWay = codeWay;
	}

	public Integer getContentType() {
		return contentType;
	}

	public void setContentType(Integer contentType) {
		this.contentType = contentType;
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

	public Integer getLotteryChildCode() {
		return lotteryChildCode;
	}

	public void setLotteryChildCode(Integer lotteryChildCode) {
		this.lotteryChildCode = lotteryChildCode;
	}

	public Integer getBuyNumber() {
		return buyNumber;
	}

	public void setBuyNumber(Integer buyNumber) {
		this.buyNumber = buyNumber;
	}

	@Override
	public String toString() {
		return "OrderDetailPO [id=" + id + ", orderCode=" + orderCode + ", bettingContentUrl=" + bettingContentUrl
				+ ", buyScreen=" + buyScreen + ", planContent=" + planContent + ", multiple=" + multiple + ", amount="
				+ amount + ", playIntro=" + playIntro + ", codeWay=" + codeWay + ", contentType=" + contentType
				+ ", lotteryChildCode=" + lotteryChildCode + ", buyNumber=" + buyNumber + ", updateTime=" + updateTime
				+ ", createTime=" + createTime + "]";
	}

	
}
