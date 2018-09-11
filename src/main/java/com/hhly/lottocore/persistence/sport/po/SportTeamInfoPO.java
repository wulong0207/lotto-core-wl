package com.hhly.lottocore.persistence.sport.po;

import java.util.Date;

public class SportTeamInfoPO {
    private Long id;
    
    private Long teamId;

    private String teamDataUrl;

    private String teamFullName;

    private String teamShortName;

    private String teamOrder;

    private String logoUrl;

    private Date modifyTime;

    private String modifyBy;

    private String createBy;

    private Date updateTime;

    private Date createTime;

    private String remark;

    public SportTeamInfoPO() {
    }

    public SportTeamInfoPO(Long id,Long sportTeamInfoId, String teamDataUrl, String teamFullName, String teamShortName, String teamOrder, String logoUrl, Date modifyTime, String modifyBy, String createBy, Date updateTime, Date createTime, String remark) {
        this.id = id;
        this.teamId=sportTeamInfoId;
        this.teamDataUrl = teamDataUrl;
        this.teamFullName = teamFullName;
        this.teamShortName = teamShortName;
        this.teamOrder = teamOrder;
        this.logoUrl = logoUrl;
        this.modifyTime = modifyTime;
        this.modifyBy = modifyBy;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.createTime = createTime;
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamDataUrl() {
        return teamDataUrl;
    }

    public void setTeamDataUrl(String teamDataUrl) {
        this.teamDataUrl = teamDataUrl == null ? null : teamDataUrl.trim();
    }

    public String getTeamFullName() {
        return teamFullName;
    }

    public void setTeamFullName(String teamFullName) {
        this.teamFullName = teamFullName;
    }

    public String getTeamShortName() {
        return teamShortName;
    }

    public void setTeamShortName(String teamShortName) {
        this.teamShortName = teamShortName;
    }

    public String getTeamOrder() {
        return teamOrder;
    }

    public void setTeamOrder(String teamOrder) {
        this.teamOrder = teamOrder;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
    
}