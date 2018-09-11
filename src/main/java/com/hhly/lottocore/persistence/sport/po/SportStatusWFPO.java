package com.hhly.lottocore.persistence.sport.po;

import com.hhly.skeleton.cms.sportmgr.vo.WFDataVO;

import java.util.Date;

public class SportStatusWFPO {
    private Long id;

    private Long sportAgainstInfoId;

    private Short statusLetWf;

    private String  modifyBy;

    private Date modifyTime;

    private Date updateTime;

    private Date createTime;

    public SportStatusWFPO() {
    }

    public SportStatusWFPO(WFDataVO vo) {
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