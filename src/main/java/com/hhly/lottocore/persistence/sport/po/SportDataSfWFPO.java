package com.hhly.lottocore.persistence.sport.po;

import java.math.BigDecimal;
import java.util.Date;

public class SportDataSfWFPO{
    private Integer id;

    private Integer sportAgainstInfoId;

    private BigDecimal letScore;

    private BigDecimal spWin;

    private BigDecimal spFail;

    private Date releaseTime;

    private Date updateTime;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSportAgainstInfoId() {
        return sportAgainstInfoId;
    }

    public void setSportAgainstInfoId(Integer sportAgainstInfoId) {
        this.sportAgainstInfoId = sportAgainstInfoId;
    }

    public BigDecimal getLetScore() {
        return letScore;
    }

    public void setLetScore(BigDecimal letScore) {
        this.letScore = letScore;
    }

    public BigDecimal getSpWin() {
        return spWin;
    }

    public void setSpWin(BigDecimal spWin) {
        this.spWin = spWin;
    }

    public BigDecimal getSpFail() {
        return spFail;
    }

    public void setSpFail(BigDecimal spFail) {
        this.spFail = spFail;
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