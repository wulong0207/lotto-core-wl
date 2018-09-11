package com.hhly.lottocore.remote.sportorder.service.impl.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
	
	
	private static final Map<String, String> WAY_VIE;
	
	static {
		WAY_VIE = new HashMap<>();
		
		// 足球
		WAY_VIE.put("30002_3", "newestSpWin");
		WAY_VIE.put("30002_1", "newestSpDraw");
		WAY_VIE.put("30002_0", "newestSpFail");

		WAY_VIE.put("30003_3", "newestLetSpWin");
		WAY_VIE.put("30003_1", "newestLetSpDraw");
		WAY_VIE.put("30003_0", "newestLetSpFail");

		WAY_VIE.put("30004_90", "newestSpWOther");
		WAY_VIE.put("30004_10", "newestSp10");
		WAY_VIE.put("30004_20", "newestSp20");
		WAY_VIE.put("30004_21", "newestSp21");
		WAY_VIE.put("30004_30", "newestSp30");
		WAY_VIE.put("30004_31", "newestSp31");
		WAY_VIE.put("30004_32", "newestSp32");
		WAY_VIE.put("30004_40", "newestSp40");
		WAY_VIE.put("30004_41", "newestSp41");
		WAY_VIE.put("30004_42", "newestSp42");
		WAY_VIE.put("30004_50", "newestSp50");
		WAY_VIE.put("30004_51", "newestSp51");
		WAY_VIE.put("30004_52", "newestSp52");
		WAY_VIE.put("30004_99", "newestSpDOther");
		WAY_VIE.put("30004_00", "newestSp00");
		WAY_VIE.put("30004_11", "newestSp11");
		WAY_VIE.put("30004_22", "newestSp22");
		WAY_VIE.put("30004_33", "newestSp33");
		WAY_VIE.put("30004_09", "newestSpFOther");
		WAY_VIE.put("30004_01", "newestSp01");
		WAY_VIE.put("30004_02", "newestSp02");
		WAY_VIE.put("30004_12", "newestSp12");
		WAY_VIE.put("30004_03", "newestSp03");
		WAY_VIE.put("30004_13", "newestSp13");
		WAY_VIE.put("30004_23", "newestSp23");
		WAY_VIE.put("30004_04", "newestSp04");
		WAY_VIE.put("30004_14", "newestSp14");
		WAY_VIE.put("30004_24", "newestSp24");
		WAY_VIE.put("30004_05", "newestSp05");
		WAY_VIE.put("30004_15", "newestSp15");
		WAY_VIE.put("30004_25", "newestSp25");

		WAY_VIE.put("30005_0", "newestSp0Goal");
		WAY_VIE.put("30005_1", "newestSp1Goal");
		WAY_VIE.put("30005_2", "newestSp2Goal");
		WAY_VIE.put("30005_3", "newestSp3Goal");
		WAY_VIE.put("30005_4", "newestSp4Goal");
		WAY_VIE.put("30005_5", "newestSp5Goal");
		WAY_VIE.put("30005_6", "newestSp6Goal");
		WAY_VIE.put("30005_7", "newestSp7Goal");

		WAY_VIE.put("30006_33", "newestSpWW");
		WAY_VIE.put("30006_31", "newestSpWD");
		WAY_VIE.put("30006_30", "newestSpWF");
		WAY_VIE.put("30006_13", "newestSpDW");
		WAY_VIE.put("30006_11", "newestSpDD");
		WAY_VIE.put("30006_10", "newestSpDF");
		WAY_VIE.put("30006_03", "newestSpFW");
		WAY_VIE.put("30006_01", "newestSpFD");
		WAY_VIE.put("30006_00", "newestSpFF");

		
	}
			
	private String matchId;

	private String matchType;

	private List<String> content;
	
	public Match(){
		content = new ArrayList<>();
	}
	
	public void addContent(String c){
		content.add(c);
	}
	
	public String getField(String key){
		return WAY_VIE.get(key);
	}
	
	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

}
