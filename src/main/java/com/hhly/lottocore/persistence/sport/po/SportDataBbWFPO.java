package com.hhly.lottocore.persistence.sport.po;

import java.math.BigDecimal;
import java.util.Date;

public class SportDataBbWFPO {
    private Long id;

    private Long sportAgainstInfoId;

    private BigDecimal letScore;

    private BigDecimal spFail;

    private BigDecimal spWin;

    private Short spType;

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

    public BigDecimal getLetScore() {
        return letScore;
    }

    public void setLetScore(BigDecimal letScore) {
        this.letScore = letScore;
    }

    public BigDecimal getSpFail() {
        return spFail;
    }

    public void setSpFail(BigDecimal spFail) {
        this.spFail = spFail;
    }

    public BigDecimal getSpWin() {
        return spWin;
    }

    public void setSpWin(BigDecimal spWin) {
        this.spWin = spWin;
    }

    public Short getSpType() {
        return spType;
    }

    public void setSpType(Short spType) {
        this.spType = spType;
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