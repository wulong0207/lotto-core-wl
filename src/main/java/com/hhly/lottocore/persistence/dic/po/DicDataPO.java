package com.hhly.lottocore.persistence.dic.po;

import java.util.Date;

import com.hhly.skeleton.cms.dicmgr.vo.DicDataVO;

public class DicDataPO {

    private String dicName;

    private String dicCode;

    private Date udpateTime;

    private Date createTime;
    
    private String remark;
   
    public DicDataPO() {
	}
     
    public DicDataPO(DicDataVO vo) {
		this.dicName = vo.getDicName();
		this.dicCode = vo.getDicCode();
		this.remark = vo.getRemark();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName == null ? null : dicName.trim();
    }

    public String getDicCode() {
        return dicCode;
    }

    public void setDicCode(String dicCode) {
        this.dicCode = dicCode == null ? null : dicCode.trim();
    }

    public Date getUdpateTime() {
        return udpateTime;
    }

    public void setUdpateTime(Date udpateTime) {
        this.udpateTime = udpateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}