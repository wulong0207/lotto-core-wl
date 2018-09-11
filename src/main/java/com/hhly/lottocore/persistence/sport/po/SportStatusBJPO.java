package com.hhly.lottocore.persistence.sport.po;

import com.hhly.skeleton.cms.sportmgr.vo.BJDataVO;
import com.hhly.skeleton.cms.sportmgr.vo.WFDataVO;

import java.util.Date;

public class SportStatusBJPO {
    private Long id;

    private Long sportAgainstInfoId;

    private Short statusLetWdf;

    private Short statusGoal;

    private Short statusHfWdf;

    private Short statusUdSd;

    private Short statusScore;
    
    private Short statusLetWf;

    private String modifyBy;

    private Date modifyTime;

    private Date updateTime;

    private Date createTime;

    public SportStatusBJPO() {
    }

    public SportStatusBJPO(BJDataVO vo) {
        this.id = vo.getSportStatusBjId();
        this.sportAgainstInfoId = vo.getAgainstId();
        this.statusLetWdf = vo.getStatusLetWdf();
        this.statusGoal = vo.getStatusGoal();
        this.statusHfWdf = vo.getStatusHfWdf();
        this.statusUdSd = vo.getStatusUdSd();
        this.statusScore = vo.getStatusScore();
        this.modifyBy = vo.getModifyBy();
        this.modifyTime = vo.getModifyTime();
        this.updateTime = vo.getModifyTime();
    }
    
    public SportStatusBJPO(WFDataVO vo) {
    	 this.id = vo.getSportStatusWfId();
         this.sportAgainstInfoId = vo.getAgainstId();
         this.statusLetWf = vo.getStatusLetWf();
         this.modifyBy = vo.getModifyBy();
         this.modifyTime = vo.getModifyTime();
         this.updateTime = vo.getModifyTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSportAgainstInfoId() {
        return sportAgainstInfoId;
    }

    public void setSportAgainstInfoId(Long sportAgainstInfoId) {
        this.sportAgainstInfoId = sportAgainstInfoId;
    }

    public Short getStatusLetWdf() {
        return statusLetWdf;
    }

    public void setStatusLetWdf(Short statusLetWdf) {
        this.statusLetWdf = statusLetWdf;
    }

    public Short getStatusGoal() {
        return statusGoal;
    }

    public void setStatusGoal(Short statusGoal) {
        this.statusGoal = statusGoal;
    }

    public Short getStatusHfWdf() {
        return statusHfWdf;
    }

    public void setStatusHfWdf(Short statusHfWdf) {
        this.statusHfWdf = statusHfWdf;
    }

    public Short getStatusUdSd() {
        return statusUdSd;
    }

    public void setStatusUdSd(Short statusUdSd) {
        this.statusUdSd = statusUdSd;
    }

    public Short getStatusScore() {
        return statusScore;
    }

    public void setStatusScore(Short statusScore) {
        this.statusScore = statusScore;
    }
    
    public Short getStatusLetWf() {
		return statusLetWf;
	}

	public void setStatusLetWf(Short statusLetWf) {
		this.statusLetWf = statusLetWf;
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
}