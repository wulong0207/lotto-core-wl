package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print;

import java.util.Map;

/**
 * @desc 计算票二维坐标
 * @author jiangwei
 * @date 2018年7月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface IPaper {
	/**
	 * 检查是否能转换成打印纸
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月18日 上午10:36:25
	 * @param ticket
	 * @return
	 */
	boolean check(Ticket ticket);
	/**
	 * 获取玩法固定头部
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月17日 下午2:30:35
	 * @return
	 */
    String header(Ticket ticket);
    /**
     * 玩法
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月17日 下午2:30:32
     * @param childCode 子玩法
     * @param contentType 1.单式，2复试
     * @param childType 玩法类型：1_1,2_1
     * @return
     */
    String play(Ticket ticket);
    /**
     * 票内容解析
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月17日 下午2:35:02
     * @param content 内容
     * @param childCode 子玩法
     * @return
     */
    String content(Ticket ticket);
    /**
     * 倍数
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月17日 下午2:37:11
     * @param multiple
     * @return
     */
    String multiple(Ticket ticket);
    
    /**
     * 拆分数字
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月24日 下午4:33:22
     * @param coordinate 坐标
     * @param no 第几次
     * @param matchNo 需要拆分数
     * @param num 拆分系数
     * @param header 坐标头
     * @return
     */
	default StringBuilder getNumber(Map<String, String> coordinate, int no, int matchNo, int[] num, String header) {
		StringBuilder sb = new StringBuilder();
		for (int i = 100; i > 0; i /= 10) {
			int number = matchNo / i;
			matchNo = matchNo % i;
			for (int j = 0; number > 0; j++) {
				int b = number / num[j];
				number = number % num[j];
				if (b == 1) {
					sb.append(",");
					String key = header + "_" + num[j] * i;
					if (no > 0) {
						key = key + "_" + no;
					}
					sb.append(coordinate.get(key));
				}
			}
		}
		return sb;
	}
    
}
