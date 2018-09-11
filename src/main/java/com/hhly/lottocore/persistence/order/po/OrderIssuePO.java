package com.hhly.lottocore.persistence.order.po;

import java.util.Date;

import com.hhly.skeleton.cms.ordermgr.vo.OrderIssueVO;

public class OrderIssuePO {
	private Integer id;
	
    private String remark;
    
    /**
     * 显示隐藏开关。默认为 1：显示， 0：隐藏
     */
    private Integer isShow;

    /**
     * 置顶标签 1：置顶；0：不置顶 默认0
     */
    private Integer isTop;
    /**
     * 推荐标签 1：推荐；0：不推荐 默认0
     */
    private Integer isRecommend;
    
    private String recommendReason;
    
    private String modifyBy;

    private Date modifyTime;
    
    private Date updateTime;

	public OrderIssuePO(OrderIssueVO vo) {
		this.id = vo.getId();
		this.remark = vo.getRemark();
		this.isShow = vo.getIsShow();
		this.isTop = vo.getIsTop();
		this.isRecommend = vo.getIsRecommend();
		this.recommendReason = vo.getRecommendReason();
		this.modifyBy = vo.getModifyBy();
		this.modifyTime = vo.getModifyTime();
		this.updateTime = vo.getUpdateTime();
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}

	public Integer getIsTop() {
		return isTop;
	}

	public void setIsTop(Integer isTop) {
		this.isTop = isTop;
	}

	public Integer getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Integer isRecommend) {
		this.isRecommend = isRecommend;
	}

	public String getRecommendReason() {
		return recommendReason;
	}

	public void setRecommendReason(String recommendReason) {
		this.recommendReason = recommendReason;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	@Override
	public String toString() {
		return "OrderIssuePO [id=" + id + ", remark=" + remark + ", isShow=" + isShow + ", isTop=" + isTop
				+ ", isRecommend=" + isRecommend + ", recommendReason=" + recommendReason + ", modifyBy=" + modifyBy
				+ ", modifyTime=" + modifyTime + ", updateTime=" + updateTime + "]";
	}
}