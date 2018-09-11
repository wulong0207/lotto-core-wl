package com.hhly.lottocore.persistence.ordercopy.po;

import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueLevelVO;

import java.util.Date;

public class MUserIssueLevelPO {
    /**
     * 自动增长id
     */
    private Integer id;

    /**
     * 发单用户id
     */
    private Integer userIssueInfoId;

    /**
     * 彩种编号(大)
     */
    private Short lotteryCode;

    /**
     * 用户等级,1:专家
     */
    private Integer level;

    /**
     * 是否自动:0否;1是
     */
    private Integer isAutomatic;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改用户
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    public MUserIssueLevelPO() {
    }

    public MUserIssueLevelPO(MUserIssueLevelVO vo) {
        this.id = vo.getId();
        this.userIssueInfoId = vo.getUserIssueInfoId();
        this.lotteryCode = vo.getLotteryCode();
        this.level = vo.getLevel();
        this.isAutomatic = vo.getIsAutomatic();
        this.createTime = vo.getCreateTime();
        this.modifyBy = vo.getModifyBy();
        this.modifyTime = vo.getModifyTime();
        this.updateTime = vo.getUpdateTime();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserIssueInfoId() {
        return userIssueInfoId;
    }

    public void setUserIssueInfoId(Integer userIssueInfoId) {
        this.userIssueInfoId = userIssueInfoId;
    }

    public Short getLotteryCode() {
        return lotteryCode;
    }

    public void setLotteryCode(Short lotteryCode) {
        this.lotteryCode = lotteryCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIsAutomatic() {
        return isAutomatic;
    }

    public void setIsAutomatic(Integer isAutomatic) {
        this.isAutomatic = isAutomatic;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy == null ? null : modifyBy.trim();
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
}