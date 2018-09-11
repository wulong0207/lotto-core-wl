package com.hhly.lottocore.persistence.dic.po;

import java.util.Date;

import com.hhly.skeleton.cms.dicmgr.vo.DicDataDetailVO;

public class DicDataDetailPO {
	
    private Integer id;

    private String dicCode;

    private String dicDataName;

    private String dicDataValue;

    private Short isFixed;

    private Short status;

    private Short orderBy;

    private Date updateTime;

    private Date createTime;

    public DicDataDetailPO() {
		
	}

    public DicDataDetailPO(DicDataDetailVO vo) {
    	this.id = vo.getId();
        this.dicCode = vo.getDicCode();
        this.dicDataName = vo.getDicDataName();
        this.dicDataValue = vo.getDicDataValue();
        this.isFixed = vo.getIsFixed();
        this.status = vo.getStatus();
        this.orderBy = vo.getOrderBy();
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDicCode() {
        return dicCode;
    }

    public void setDicCode(String dicCode) {
        this.dicCode = dicCode == null ? null : dicCode.trim();
    }

    public String getDicDataName() {
        return dicDataName;
    }

    public void setDicDataName(String dicDataName) {
        this.dicDataName = dicDataName == null ? null : dicDataName.trim();
    }

    public String getDicDataValue() {
        return dicDataValue;
    }

    public void setDicDataValue(String dicDataValue) {
        this.dicDataValue = dicDataValue == null ? null : dicDataValue.trim();
    }

    public Short getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Short isFixed) {
        this.isFixed = isFixed;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Short getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Short orderBy) {
        this.orderBy = orderBy;
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