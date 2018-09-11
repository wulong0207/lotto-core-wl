package com.hhly.lottocore.persistence.sport.po;

import java.math.BigDecimal;
import java.util.Date;

public class SportDataBbSSSPO {
    private Long id;

    private Long sportAgainstInfoId;

    private BigDecimal presetScore;

    private BigDecimal spBig;

    private BigDecimal spSmall;

    private Date releaseTime;

    private Date updateTime;

    private Date createTime;

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

    public BigDecimal getPresetScore() {
        return presetScore;
    }

    public void setPresetScore(BigDecimal presetScore) {
        this.presetScore = presetScore;
    }

    public BigDecimal getSpBig() {
        return spBig;
    }

    public void setSpBig(BigDecimal spBig) {
        this.spBig = spBig;
    }

    public BigDecimal getSpSmall() {
        return spSmall;
    }

    public void setSpSmall(BigDecimal spSmall) {
        this.spSmall = spSmall;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
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