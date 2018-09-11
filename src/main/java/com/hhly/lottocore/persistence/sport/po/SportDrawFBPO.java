package com.hhly.lottocore.persistence.sport.po;


import com.hhly.skeleton.lotto.base.sport.vo.FBDataVO;

import java.util.Date;

public class SportDrawFBPO {
    private Long id;

    private Long sportAgainstInfoId;

    private String halfScore;

    private String fullScore;

    private Short fullSpf;

    private Short letSpf;

    private Short letNum;

    private String score;

    private Short goalNum;

    private String hfWdf;

    private Date drawTime;

    private String modifyBy;

    private Date modifyTime;

    private Date updateTime;

    private Date createTime;

    public SportDrawFBPO() {
    }

    public SportDrawFBPO(FBDataVO vo) {
        this.id = vo.getSportDrawFbId();
        this.sportAgainstInfoId = vo.getAgainstId();
        this.halfScore = vo.getHalfScore();
        this.fullScore = vo.getFullScore();
        this.fullSpf = vo.getFullSpf();
        this.letSpf = vo.getLetSpf();
        this.letNum = vo.getLetNum();
        this.score = vo.getScore();
        this.goalNum = vo.getGoalNum();
        this.hfWdf = vo.getHfWdf();
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

    public String getHalfScore() {
        return halfScore;
    }

    public void setHalfScore(String halfScore) {
        this.halfScore = halfScore == null ? null : halfScore.trim();
    }

    public String getFullScore() {
        return fullScore;
    }

    public void setFullScore(String fullScore) {
        this.fullScore = fullScore == null ? null : fullScore.trim();
    }

    public Short getFullSpf() {
        return fullSpf;
    }

    public void setFullSpf(Short fullSpf) {
        this.fullSpf = fullSpf;
    }

    public Short getLetSpf() {
        return letSpf;
    }

    public void setLetSpf(Short letSpf) {
        this.letSpf = letSpf;
    }

    public Short getLetNum() {
        return letNum;
    }

    public void setLetNum(Short letNum) {
        this.letNum = letNum;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score == null ? null : score.trim();
    }

    public Short getGoalNum() {
        return goalNum;
    }

    public void setGoalNum(Short goalNum) {
        this.goalNum = goalNum;
    }

    public String getHfWdf() {
        return hfWdf;
    }

    public void setHfWdf(String hfWdf) {
        this.hfWdf = hfWdf == null ? null : hfWdf.trim();
    }

    public Date getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(Date drawTime) {
        this.drawTime = drawTime;
    }

    public Long getSportAgainstInfoId() {
        return sportAgainstInfoId;
    }

    public void setSportAgainstInfoId(Long sportAgainstInfoId) {
        this.sportAgainstInfoId = sportAgainstInfoId;
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