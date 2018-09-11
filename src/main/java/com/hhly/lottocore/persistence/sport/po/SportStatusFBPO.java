package com.hhly.lottocore.persistence.sport.po;


import com.hhly.skeleton.lotto.base.sport.vo.FBDataVO;

import java.util.Date;

public class SportStatusFBPO {
    private Long id;

    private Long sportAgainstInfoId;

    private Short statusWdf;

    private Short statusLetWdf;

    private Short statusGoal;

    private Short statusHfWdf;

    private Short statusScore;

    private String modifyBy;

    private Date modifyTime;

    private Date updateTime;

    private Date createTime;

    public SportStatusFBPO() {
    }

    public SportStatusFBPO(FBDataVO vo) {
        this.id = vo.getSportStatusFbId();
        this.sportAgainstInfoId = vo.getAgainstId();
        this.statusWdf = vo.getStatusWdf();
        this.statusLetWdf = vo.getStatusLetWdf();
        this.statusGoal = vo.getStatusGoal();
        this.statusHfWdf = vo.getStatusHfWdf();
        this.statusScore = vo.getStatusScore();
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

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public Short getStatusWdf() {
        return statusWdf;
    }

    public void setStatusWdf(Short statusWdf) {
        this.statusWdf = statusWdf;
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

    public Short getStatusScore() {
        return statusScore;
    }

    public void setStatusScore(Short statusScore) {
        this.statusScore = statusScore;
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