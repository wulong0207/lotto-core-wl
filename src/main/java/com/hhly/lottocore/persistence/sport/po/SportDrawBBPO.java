package com.hhly.lottocore.persistence.sport.po;

import com.hhly.skeleton.cms.sportmgr.vo.BBDataVO;

import java.util.Date;

public class SportDrawBBPO {
    private Long id;

    private Long sportAgainstInfoId;

    private String firstScore;

    private String secondScore;

    private String thirdScore;

    private String fourthScore;

    private String outTimeScore;

    private String fullScore;

    private Short fullWf;

    private String letWf;

    private String sizeScore;

    private String winScore;

    private Date drawTime;

    private String modifyBy;

    private Date modifyTime;

    private Date updateTime;

    private Date createTime;

    public SportDrawBBPO() {
    }

    public SportDrawBBPO(BBDataVO vo) {
        this.id = vo.getSportDrawBbId();
        this.sportAgainstInfoId = vo.getAgainstId();
        this.firstScore = vo.getFirstScore();
        this.secondScore = vo.getSecondScore();
        this.thirdScore = vo.getThirdScore();
        this.fourthScore = vo.getFourthScore();
        this.outTimeScore = vo.getOutTimeScore();
        this.fullScore = vo.getFullScore();
        this.fullWf = vo.getFullWf();
        this.letWf = vo.getLetWf();
        this.sizeScore = vo.getSizeScore();
        this.winScore = vo.getWinScore();
        this.drawTime = vo.getDrawTime();
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

    public String getFirstScore() {
        return firstScore;
    }

    public void setFirstScore(String firstScore) {
        this.firstScore = firstScore == null ? null : firstScore.trim();
    }

    public String getSecondScore() {
        return secondScore;
    }

    public void setSecondScore(String secondScore) {
        this.secondScore = secondScore == null ? null : secondScore.trim();
    }

    public String getThirdScore() {
        return thirdScore;
    }

    public void setThirdScore(String thirdScore) {
        this.thirdScore = thirdScore == null ? null : thirdScore.trim();
    }

    public String getFourthScore() {
        return fourthScore;
    }

    public void setFourthScore(String fourthScore) {
        this.fourthScore = fourthScore == null ? null : fourthScore.trim();
    }

    public String getFullScore() {
        return fullScore;
    }

    public void setFullScore(String fullScore) {
        this.fullScore = fullScore == null ? null : fullScore.trim();
    }

    public Short getFullWf() {
        return fullWf;
    }

    public void setFullWf(Short fullWf) {
        this.fullWf = fullWf;
    }

    public String getLetWf() {
        return letWf;
    }

    public void setLetWf(String letWf) {
        this.letWf = letWf == null ? null : letWf.trim();
    }

    public String getSizeScore() {
        return sizeScore;
    }

    public void setSizeScore(String sizeScore) {
        this.sizeScore = sizeScore == null ? null : sizeScore.trim();
    }

    public String getWinScore() {
        return winScore;
    }

    public void setWinScore(String winScore) {
        this.winScore = winScore == null ? null : winScore.trim();
    }

    public Date getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(Date drawTime) {
        this.drawTime = drawTime;
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

    public String getOutTimeScore() {
        return outTimeScore;
    }

    public void setOutTimeScore(String outTimeScore) {
        this.outTimeScore = outTimeScore;
    }
}