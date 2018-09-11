package com.hhly.lottocore.persistence.sport.po;

import com.hhly.skeleton.cms.sportmgr.vo.SportDataBaseVO;

import java.util.Date;

public class SportAgainstInfoPO {
    private Long id;

    private Integer lotteryCode;

    private String issueCode;

    private Long sportMatchInfoId;
    
    private String matchName;

    private Long homeTeamId;
    
    private String homeName;

    private Long visitiTeamId;
    
    private String visitiName;

    private String matchAnalysisUrl;

    private String matchInfoUrl;

    private Short isRecommend;

    private String matchLabelColor;

    private String officialMatchCode;

    private String systemCode;

    private Short matchStatus;

    private String officialId;

    private Date startTime;

    private Date saleEndTime;

    private Short isNeutral;

    private String stadium;

    private String weather;
    
    private String oddsWin;
    
    private String oddsDraw;
    
    private String oddsFail;

    private String modifyBy;

    private Date modifyTime;

    private String createBy;

    private Date updateTime;

    private Date createTime;

    private String remark;

    public SportAgainstInfoPO() {
    }

    public SportAgainstInfoPO(SportDataBaseVO vo) {
        this.id = vo.getAgainstId();
        this.sportMatchInfoId = vo.getMatchPrimaryId();
        this.matchName = vo.getMatchFullName();
        this.homeTeamId = vo.getHomeTeamPrimaryId();
        this.homeName = vo.getHomeTeamFullName();
        this.visitiTeamId = vo.getGuestTeamPrimaryId();
        this.visitiName = vo.getGuestTeamFullName();
        this.matchAnalysisUrl = vo.getAnalysisUrl();
        this.matchInfoUrl = vo.getMatchInfoUrl();
        this.isRecommend = vo.getIsRecommend()==null?null:Short.valueOf(vo.getIsRecommend());
        this.matchLabelColor = vo.getColor();
        this.officialMatchCode = vo.getOfficialMatchCode();
        this.matchStatus = vo.getMatchStatus() == null ? null : Short.valueOf(vo.getMatchStatus());
        this.officialId = vo.getOfficialId();
        this.startTime = vo.getStartTime();
        this.saleEndTime = vo.getSaleEndTime();
        this.isNeutral = vo.getIsNeutral()==null?null:Short.valueOf(vo.getIsNeutral());
        this.modifyBy = vo.getModifyBy();
        this.modifyTime = vo.getModifyTime();
        this.createBy = vo.getCreateBy();
        this.updateTime = vo.getModifyTime();
        this.stadium = vo.getStadium(); 	
        this.weather = vo.getWeather();
        this.remark = vo.getRemark();
        this.oddsWin = vo.getOddsWin();
        this.oddsDraw = vo.getOddsDraw();
        this.oddsFail = vo.getOddsFail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public Integer getLotteryCode() {
        return lotteryCode;
    }

    public void setLotteryCode(Integer lotteryCode) {
        this.lotteryCode = lotteryCode;
    }

    public String getIssueCode() {
        return issueCode;
    }

    public void setIssueCode(String issueCode) {
        this.issueCode = issueCode == null ? null : issueCode.trim();
    }

    public Long getSportMatchInfoId() {
        return sportMatchInfoId;
    }

    public void setSportMatchInfoId(Long sportMatchInfoId) {
        this.sportMatchInfoId = sportMatchInfoId;
    }

    public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public Long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public String getHomeName() {
		return homeName;
	}

	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public Long getVisitiTeamId() {
        return visitiTeamId;
    }

    public void setVisitiTeamId(Long visitiTeamId) {
        this.visitiTeamId = visitiTeamId;
    }

    public String getVisitiName() {
		return visitiName;
	}

	public void setVisitiName(String visitiName) {
		this.visitiName = visitiName;
	}

	public String getMatchAnalysisUrl() {
        return matchAnalysisUrl;
    }

    public void setMatchAnalysisUrl(String matchAnalysisUrl) {
        this.matchAnalysisUrl = matchAnalysisUrl == null ? null : matchAnalysisUrl.trim();
    }

    public String getMatchInfoUrl() {
        return matchInfoUrl;
    }

    public void setMatchInfoUrl(String matchInfoUrl) {
        this.matchInfoUrl = matchInfoUrl == null ? null : matchInfoUrl.trim();
    }

    public Short getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(Short isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getMatchLabelColor() {
        return matchLabelColor;
    }

    public void setMatchLabelColor(String matchLabelColor) {
        this.matchLabelColor = matchLabelColor == null ? null : matchLabelColor.trim();
    }

    public String getOfficialMatchCode() {
        return officialMatchCode;
    }

    public void setOfficialMatchCode(String officialMatchCode) {
        this.officialMatchCode = officialMatchCode;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode == null ? null : systemCode.trim();
    }

    public Short getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(Short matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getOfficialId() {
        return officialId;
    }

    public void setOfficialId(String officialId) {
        this.officialId = officialId == null ? null : officialId.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(Date saleEndTime) {
        this.saleEndTime = saleEndTime;
    }

    public Short getIsNeutral() {
        return isNeutral;
    }

    public void setIsNeutral(Short isNeutral) {
        this.isNeutral = isNeutral;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
    
    public String getOddsWin() {
		return oddsWin;
	}

	public void setOddsWin(String oddsWin) {
		this.oddsWin = oddsWin;
	}

	public String getOddsDraw() {
		return oddsDraw;
	}

	public void setOddsDraw(String oddsDraw) {
		this.oddsDraw = oddsDraw;
	}

	public String getOddsFail() {
		return oddsFail;
	}

	public void setOddsFail(String oddsFail) {
		this.oddsFail = oddsFail;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}