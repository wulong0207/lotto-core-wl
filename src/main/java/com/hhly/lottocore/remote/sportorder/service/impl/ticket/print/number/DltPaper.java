package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.number;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;
/**
 * @desc 大乐透普通投注
 * @author jiangwei
 * @date 2018年7月25日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service
public class DltPaper extends AbstractNumberPaper {
	
	private  int[] multipleNum  = { 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	
	public static Map<String, String> COORDINATE = new HashMap<>();
	
	private static final String  HEADER = "0_0,2_0,4_0,6_0,8_0,10_0,12_0,14_0,16_0,18_0,20_0,22_0,24_0,26_0,28_0,30_0,32_0,34_0,36_0,38_0,40_0,42_0,44_0,46_0,48_0,50_0,52_0,54_0,56_0,58_0,60_0,62_0,64_0,1_1,1_2,1_3,1_5,1_6,1_7,1_13,65_13";
	
	static{
		//玩法
		COORDINATE.put("play_main", "3_6");
		COORDINATE.put("play_secondary", "3_10");
		
		COORDINATE.put("play_complex", "13_13");
		
		COORDINATE.put("play_add", "63_13");
		
		COORDINATE.put("b_01_1", "7_2");
		COORDINATE.put("b_06_1", "7_3");
		COORDINATE.put("b_11_1", "7_4");
		COORDINATE.put("b_16_1", "7_5");
		COORDINATE.put("b_21_1", "7_6");
		COORDINATE.put("b_26_1", "7_7");
		COORDINATE.put("b_31_1", "7_8");
		COORDINATE.put("a_01_1", "7_10");
		COORDINATE.put("a_05_1", "7_11");
		COORDINATE.put("a_09_1", "7_12");
		
		COORDINATE.put("b_02_1", "9_2");
		COORDINATE.put("b_07_1", "9_3");
		COORDINATE.put("b_12_1", "9_4");
		COORDINATE.put("b_17_1", "9_5");
		COORDINATE.put("b_22_1", "9_6");
		COORDINATE.put("b_27_1", "9_7");
		COORDINATE.put("b_32_1", "9_8");
		COORDINATE.put("a_02_1", "9_10");
		COORDINATE.put("a_06_1", "9_11");
		COORDINATE.put("a_10_1", "9_12");
		
		COORDINATE.put("b_03_1", "11_2");
		COORDINATE.put("b_08_1", "11_3");
		COORDINATE.put("b_13_1", "11_4");
		COORDINATE.put("b_18_1", "11_5");
		COORDINATE.put("b_23_1", "11_6");
		COORDINATE.put("b_28_1", "11_7");
		COORDINATE.put("b_33_1", "11_8");
		COORDINATE.put("a_03_1", "11_10");
		COORDINATE.put("a_07_1", "11_11");
		COORDINATE.put("a_11_1", "11_12");
		
		COORDINATE.put("b_04_1", "13_2");
		COORDINATE.put("b_09_1", "13_3");
		COORDINATE.put("b_14_1", "13_4");
		COORDINATE.put("b_19_1", "13_5");
		COORDINATE.put("b_24_1", "13_6");
		COORDINATE.put("b_29_1", "13_7");
		COORDINATE.put("b_34_1", "13_8");
		COORDINATE.put("a_04_1", "13_10");
		COORDINATE.put("a_08_1", "13_11");
		COORDINATE.put("a_12_1", "13_12");

		COORDINATE.put("b_05_1", "15_2");
		COORDINATE.put("b_10_1", "15_3");
		COORDINATE.put("b_15_1", "15_4");
		COORDINATE.put("b_20_1", "15_5");
		COORDINATE.put("b_25_1", "15_6");
		COORDINATE.put("b_30_1", "15_7");
		COORDINATE.put("b_35_1", "15_8");
		
		COORDINATE.put("b_01_2", "17_2");
		COORDINATE.put("b_06_2", "17_3");
		COORDINATE.put("b_11_2", "17_4");
		COORDINATE.put("b_16_2", "17_5");
		COORDINATE.put("b_21_2", "17_6");
		COORDINATE.put("b_26_2", "17_7");
		COORDINATE.put("b_31_2", "17_8");
		COORDINATE.put("a_01_2", "17_10");
		COORDINATE.put("a_05_2", "17_11");
		COORDINATE.put("a_09_2", "17_12");
		                     
		COORDINATE.put("b_02_2", "19_2");
		COORDINATE.put("b_07_2", "19_3");
		COORDINATE.put("b_12_2", "19_4");
		COORDINATE.put("b_17_2", "19_5");
		COORDINATE.put("b_22_2", "19_6");
		COORDINATE.put("b_27_2", "19_7");
		COORDINATE.put("b_32_2", "19_8");
		COORDINATE.put("a_02_2", "19_10");
		COORDINATE.put("a_06_2", "19_11");
		COORDINATE.put("a_10_2", "19_12");
		                     
		COORDINATE.put("b_03_2", "21_2");
		COORDINATE.put("b_08_2", "21_3");
		COORDINATE.put("b_13_2", "21_4");
		COORDINATE.put("b_18_2", "21_5");
		COORDINATE.put("b_23_2", "21_6");
		COORDINATE.put("b_28_2", "21_7");
		COORDINATE.put("b_33_2", "21_8");
		COORDINATE.put("a_03_2", "21_10");
		COORDINATE.put("a_07_2", "21_11");
		COORDINATE.put("a_11_2", "21_12");
		                     
		COORDINATE.put("b_04_2", "23_2");
		COORDINATE.put("b_09_2", "23_3");
		COORDINATE.put("b_14_2", "23_4");
		COORDINATE.put("b_19_2", "23_5");
		COORDINATE.put("b_24_2", "23_6");
		COORDINATE.put("b_29_2", "23_7");
		COORDINATE.put("b_34_2", "23_8");
		COORDINATE.put("a_04_2", "23_10");
		COORDINATE.put("a_08_2", "23_11");
		COORDINATE.put("a_12_2", "23_12");
                             
		COORDINATE.put("b_05_2", "25_2");
		COORDINATE.put("b_10_2", "25_3");
		COORDINATE.put("b_15_2", "25_4");
		COORDINATE.put("b_20_2", "25_5");
		COORDINATE.put("b_25_2", "25_6");
		COORDINATE.put("b_30_2", "25_7");
		COORDINATE.put("b_35_2", "25_8");
		
		COORDINATE.put("b_01_3", "27_2");
		COORDINATE.put("b_06_3", "27_3");
		COORDINATE.put("b_11_3", "27_4");
		COORDINATE.put("b_16_3", "27_5");
		COORDINATE.put("b_21_3", "27_6");
		COORDINATE.put("b_26_3", "27_7");
		COORDINATE.put("b_31_3", "27_8");
		COORDINATE.put("a_01_3", "27_10");
		COORDINATE.put("a_05_3", "27_11");
		COORDINATE.put("a_09_3", "27_12");
		                     
		COORDINATE.put("b_02_3", "29_2");
		COORDINATE.put("b_07_3", "29_3");
		COORDINATE.put("b_12_3", "29_4");
		COORDINATE.put("b_17_3", "29_5");
		COORDINATE.put("b_22_3", "29_6");
		COORDINATE.put("b_27_3", "29_7");
		COORDINATE.put("b_32_3", "29_8");
		COORDINATE.put("a_02_3", "29_10");
		COORDINATE.put("a_06_3", "29_11");
		COORDINATE.put("a_10_3", "29_12");
		                     
		COORDINATE.put("b_03_3", "31_2");
		COORDINATE.put("b_08_3", "31_3");
		COORDINATE.put("b_13_3", "31_4");
		COORDINATE.put("b_18_3", "31_5");
		COORDINATE.put("b_23_3", "31_6");
		COORDINATE.put("b_28_3", "31_7");
		COORDINATE.put("b_33_3", "31_8");
		COORDINATE.put("a_03_3", "31_10");
		COORDINATE.put("a_07_3", "31_11");
		COORDINATE.put("a_11_3", "31_12");
		                     
		COORDINATE.put("b_04_3", "33_2");
		COORDINATE.put("b_09_3", "33_3");
		COORDINATE.put("b_14_3", "33_4");
		COORDINATE.put("b_19_3", "33_5");
		COORDINATE.put("b_24_3", "33_6");
		COORDINATE.put("b_29_3", "33_7");
		COORDINATE.put("b_34_3", "33_8");
		COORDINATE.put("a_04_3", "33_10");
		COORDINATE.put("a_08_3", "33_11");
		COORDINATE.put("a_12_3", "33_12");
                             
		COORDINATE.put("b_05_3", "35_2");
		COORDINATE.put("b_10_3", "35_3");
		COORDINATE.put("b_15_3", "35_4");
		COORDINATE.put("b_20_3", "35_5");
		COORDINATE.put("b_25_3", "35_6");
		COORDINATE.put("b_30_3", "35_7");
		COORDINATE.put("b_35_3", "35_8");
		
		COORDINATE.put("b_01_4", "37_2");
		COORDINATE.put("b_06_4", "37_3");
		COORDINATE.put("b_11_4", "37_4");
		COORDINATE.put("b_16_4", "37_5");
		COORDINATE.put("b_21_4", "37_6");
		COORDINATE.put("b_26_4", "37_7");
		COORDINATE.put("b_31_4", "37_8");
		COORDINATE.put("a_01_4", "37_10");
		COORDINATE.put("a_05_4", "37_11");
		COORDINATE.put("a_09_4", "37_12");
		                     
		COORDINATE.put("b_02_4", "39_2");
		COORDINATE.put("b_07_4", "39_3");
		COORDINATE.put("b_12_4", "39_4");
		COORDINATE.put("b_17_4", "39_5");
		COORDINATE.put("b_22_4", "39_6");
		COORDINATE.put("b_27_4", "39_7");
		COORDINATE.put("b_32_4", "39_8");
		COORDINATE.put("a_02_4", "39_10");
		COORDINATE.put("a_06_4", "39_11");
		COORDINATE.put("a_10_4", "39_12");
		                     
		COORDINATE.put("b_03_4", "41_2");
		COORDINATE.put("b_08_4", "41_3");
		COORDINATE.put("b_13_4", "41_4");
		COORDINATE.put("b_18_4", "41_5");
		COORDINATE.put("b_23_4", "41_6");
		COORDINATE.put("b_28_4", "41_7");
		COORDINATE.put("b_33_4", "41_8");
		COORDINATE.put("a_03_4", "41_10");
		COORDINATE.put("a_07_4", "41_11");
		COORDINATE.put("a_11_4", "41_12");
		                     
		COORDINATE.put("b_04_4", "43_2");
		COORDINATE.put("b_09_4", "43_3");
		COORDINATE.put("b_14_4", "43_4");
		COORDINATE.put("b_19_4", "43_5");
		COORDINATE.put("b_24_4", "43_6");
		COORDINATE.put("b_29_4", "43_7");
		COORDINATE.put("b_34_4", "43_8");
		COORDINATE.put("a_04_4", "43_10");
		COORDINATE.put("a_08_4", "43_11");
		COORDINATE.put("a_12_4", "43_12");
                             
		COORDINATE.put("b_05_4", "45_2");
		COORDINATE.put("b_10_4", "45_3");
		COORDINATE.put("b_15_4", "45_4");
		COORDINATE.put("b_20_4", "45_5");
		COORDINATE.put("b_25_4", "45_6");
		COORDINATE.put("b_30_4", "45_7");
		COORDINATE.put("b_35_4", "45_8");
		
		COORDINATE.put("b_01_5", "47_2");
		COORDINATE.put("b_06_5", "47_3");
		COORDINATE.put("b_11_5", "47_4");
		COORDINATE.put("b_16_5", "47_5");
		COORDINATE.put("b_21_5", "47_6");
		COORDINATE.put("b_26_5", "47_7");
		COORDINATE.put("b_31_5", "47_8");
		COORDINATE.put("a_01_5", "47_10");
		COORDINATE.put("a_05_5", "47_11");
		COORDINATE.put("a_09_5", "47_12");
		                         
		COORDINATE.put("b_02_5", "49_2");
		COORDINATE.put("b_07_5", "49_3");
		COORDINATE.put("b_12_5", "49_4");
		COORDINATE.put("b_17_5", "49_5");
		COORDINATE.put("b_22_5", "49_6");
		COORDINATE.put("b_27_5", "49_7");
		COORDINATE.put("b_32_5", "49_8");
		COORDINATE.put("a_02_5", "49_10");
		COORDINATE.put("a_06_5", "49_11");
		COORDINATE.put("a_10_5", "49_12");
		                     
		COORDINATE.put("b_03_5", "51_2");
		COORDINATE.put("b_08_5", "51_3");
		COORDINATE.put("b_13_5", "51_4");
		COORDINATE.put("b_18_5", "51_5");
		COORDINATE.put("b_23_5", "51_6");
		COORDINATE.put("b_28_5", "51_7");
		COORDINATE.put("b_33_5", "51_8");
		COORDINATE.put("a_03_5", "51_10");
		COORDINATE.put("a_07_5", "51_11");
		COORDINATE.put("a_11_5", "51_12");
		                         
		COORDINATE.put("b_04_5", "53_2");
		COORDINATE.put("b_09_5", "53_3");
		COORDINATE.put("b_14_5", "53_4");
		COORDINATE.put("b_19_5", "53_5");
		COORDINATE.put("b_24_5", "53_6");
		COORDINATE.put("b_29_5", "53_7");
		COORDINATE.put("b_34_5", "53_8");
		COORDINATE.put("a_04_5", "53_10");
		COORDINATE.put("a_08_5", "53_11");
		COORDINATE.put("a_12_5", "53_12");
                               
		COORDINATE.put("b_05_5", "55_2");
		COORDINATE.put("b_10_5", "55_3");
		COORDINATE.put("b_15_5", "55_4");
		COORDINATE.put("b_20_5", "55_5");
		COORDINATE.put("b_25_5", "55_6");
		COORDINATE.put("b_30_5", "55_7");
		COORDINATE.put("b_35_5", "55_8");
		
		COORDINATE.put("multiple_1", "59_3");
		COORDINATE.put("multiple_2", "59_4");
		COORDINATE.put("multiple_3", "59_5");
		COORDINATE.put("multiple_4", "59_6");
		COORDINATE.put("multiple_5", "59_7");
		COORDINATE.put("multiple_6", "59_8");
		COORDINATE.put("multiple_7", "59_9");
		COORDINATE.put("multiple_8", "59_10");
		COORDINATE.put("multiple_9", "59_11");
		COORDINATE.put("multiple_0", "59_12");
		
		COORDINATE.put("multiple_10", "57_3");
		COORDINATE.put("multiple_20", "57_4");
		COORDINATE.put("multiple_30", "57_5");
		COORDINATE.put("multiple_40", "57_6");
		COORDINATE.put("multiple_50", "57_7");
		COORDINATE.put("multiple_60", "57_8");
		COORDINATE.put("multiple_70", "57_9");
		COORDINATE.put("multiple_80", "57_10");
		COORDINATE.put("multiple_90", "57_11");
		
		COORDINATE.put("many_1", "63_3");
	}
	
	@Override
	public String header(Ticket ticket) {
		return HEADER;
	}

	@Override
	public String play(Ticket ticket) {
		return  ","+COORDINATE.get("play_main");
	}

	@Override
	public String content(Ticket ticket) {
		StringBuilder sb = new StringBuilder(",");
		sb.append(COORDINATE.get("many_1"));
	    if("2".equals(ticket.getContentType())){
			sb.append(",");
			sb.append(COORDINATE.get("play_complex"));
		}
	    if(ticket.getLottoAdd() == 1){
	    	sb.append(",");
	    	sb.append(COORDINATE.get("play_add"));
	    }
	    String[] contents = ticket.getContent().split(";");
		for (int i = 0; i < contents.length; i++) {
			StringBuilder temp = new StringBuilder();
			boolean isBefore = true;
			for (char c : contents[i].toCharArray()) {
				if(',' == c || '+' == c){
					if(isBefore){
						temp.insert(0,"b_");
					}else{
						temp.insert(0,"a_");
					}
					temp.append("_");
					temp.append(i+1);
					sb.append(",");
					sb.append(COORDINATE.get(temp.toString()));
					temp.setLength(0);
					if('+' == c){
						isBefore = false;
					}
				}else{
					temp.append(c);
				}
			}
			if(isBefore){
				temp.insert(0,"b_");
			}else{
				temp.insert(0,"a_");
			}
			temp.append("_");
			temp.append(i+1);
			sb.append(",");
			sb.append(COORDINATE.get(temp.toString()));
			
		}
		return sb.toString();
	}
	
	@Override
	protected Map<String, String> getCoordinateMap() {
		return COORDINATE;
	}

	@Override
	protected int[] getMultipleNum() {
		return multipleNum;
	}

}
