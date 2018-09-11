package com.hhly.lottocore.persistence.sport.po;

import com.hhly.skeleton.cms.sportmgr.vo.BJDataVO;

import java.util.Date;

public class SportDrawBJPO {
    private Long id;

    private Long sportAgainstInfoId;

    private String halfScore;

    private String fullScore;

    private Short letNum;

    private Short letWdf;

    private Short goalNum;

    private String hfWdf;

    private Short udSd;

    private String score;

    private Double spLetWdf;

    private Double spGoalNum;

    private Double spHfWdf;

    private Double spUdSd;

    private Double spScore;

    private Date drawTime;

    private String modifyBy;

    private Date modifyTime;

    private Date updateTime;

    private Date createTime;

    public SportDrawBJPO() {
    }

    public SportDrawBJPO(BJDataVO vo) {
        this.id = vo.getSportDrawBj();
        this.sportAgainstInfoId = vo.getAgainstId();
        this.halfScore = vo.getHalfScore();
        this.fullScore = vo.getFullScore();
        this.letNum = vo.getLetNum();
        this.letWdf = vo.getLetWdf();
        this.goalNum = vo.getGoalNum();
        this.hfWdf = vo.getHfWdf();
        this.udSd = vo.getUdSd();
        this.score = vo.getScore();
        this.spLetWdf = vo.getSpLetWdf();
        this.spGoalNum = vo.getSpGoalNum();
        this.spHfWdf = vo.getSpHfWdf();
        this.spUdSd = vo.getSpUdSd();
        this.spScore = vo.getSpScore();
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

    public Short getLetNum() {
        return letNum;
    }

    public void setLetNum(Short letNum) {
        this.letNum = letNum;
    }

    public Short getLetWdf() {
        return letWdf;
    }

    public void setLetWdf(Short letWdf) {
        this.letWdf = letWdf;
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

    public Short getUdSd() {
        return udSd;
    }

    public void setUdSd(Short udSd) {
        this.udSd = udSd;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score == null ? null : score.trim();
    }

    public Double getSpLetWdf() {
        return spLetWdf;
    }

    public void setSpLetWdf(Double spLetWdf) {
        this.spLetWdf = spLetWdf;
    }

    public Double getSpGoalNum() {
        return spGoalNum;
    }

    public void setSpGoalNum(Double spGoalNum) {
        this.spGoalNum = spGoalNum;
    }

    public Double getSpHfWdf() {
        return spHfWdf;
    }

    public void setSpHfWdf(Double spHfWdf) {
        this.spHfWdf = spHfWdf;
    }

    public Double getSpUdSd() {
        return spUdSd;
    }

    public void setSpUdSd(Double spUdSd) {
        this.spUdSd = spUdSd;
    }

    public Double getSpScore() {
        return spScore;
    }

    public void setSpScore(Double spScore) {
        this.spScore = spScore;
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
}