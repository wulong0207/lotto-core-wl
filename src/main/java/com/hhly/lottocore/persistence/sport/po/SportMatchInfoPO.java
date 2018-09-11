package com.hhly.lottocore.persistence.sport.po;

import com.hhly.skeleton.cms.sportmgr.vo.SportDataBaseVO;

import java.util.Date;

public class SportMatchInfoPO {
    private Long id;
    private Long matchId;

    private String matchDataUrl;

    private String matchFullName;

    private String matchShortName;

    private String logoUrl;

    private Short matchType;

    private String modifyBy;

    private Date modifyTime;

    private Date updateTime;

    private Date createTime;

    private String remark;

    public SportMatchInfoPO() {
    }

    public SportMatchInfoPO(SportDataBaseVO vo) {
        this.id = vo.getMatchPrimaryId();
        this.matchId=vo.getMatchId();
        this.matchShortName = vo.getMatchShortName();
        this.logoUrl = vo.getLogoUrl();
        this.modifyBy = vo.getModifyBy();
        this.modifyTime = vo.getModifyTime();
        this.updateTime = vo.getModifyTime();
        this.matchDataUrl=vo.getMatchDataUrl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatchDataUrl() {
        return matchDataUrl;
    }

    public void setMatchDataUrl(String matchDataUrl) {
        this.matchDataUrl = matchDataUrl == null ? null : matchDataUrl.trim();
    }

    public String getMatchFullName() {
        return matchFullName;
    }

    public void setMatchFullName(String matchFullName) {
        this.matchFullName = matchFullName;
    }

    public String getMatchShortName() {
        return matchShortName;
    }

    public void setMatchShortName(String matchShortName) {
        this.matchShortName = matchShortName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl == null ? null : logoUrl.trim();
    }

    public Short getMatchType() {
        return matchType;
    }

    public void setMatchType(Short matchType) {
        this.matchType = matchType;
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

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
    
}