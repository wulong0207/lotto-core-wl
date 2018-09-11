package com.hhly.lottocore.persistence.sport.po;

import java.util.Date;

public class SportDataBjUDSDPO {
    private Long id;

    private Long sportAgainstInfoId;

    private Float spUpSingle;

    private Float spUpDouble;

    private Float spDownSingle;

    private Float spDownDouble;

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

    public Float getSpUpSingle() {
        return spUpSingle;
    }

    public void setSpUpSingle(Float spUpSingle) {
        this.spUpSingle = spUpSingle;
    }

    public Float getSpUpDouble() {
        return spUpDouble;
    }

    public void setSpUpDouble(Float spUpDouble) {
        this.spUpDouble = spUpDouble;
    }

    public Float getSpDownSingle() {
        return spDownSingle;
    }

    public void setSpDownSingle(Float spDownSingle) {
        this.spDownSingle = spDownSingle;
    }

    public Float getSpDownDouble() {
        return spDownDouble;
    }

    public void setSpDownDouble(Float spDownDouble) {
        this.spDownDouble = spDownDouble;
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