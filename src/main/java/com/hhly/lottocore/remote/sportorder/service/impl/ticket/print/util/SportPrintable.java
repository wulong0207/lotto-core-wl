package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.util;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import com.hhly.skeleton.base.util.DateUtil;

public class SportPrintable implements Printable {
	
	private String data;
	
	public SportPrintable(String  data){
		this.data = data;
	}

	@Override
	public int print(Graphics gra, PageFormat pf, int pageIndex) throws PrinterException {
		// 转换成Graphics2D
		Graphics2D g2 = (Graphics2D) gra;
		// 设置打印颜色为黑色
		g2.setColor(Color.black);
		// 打印起点坐标
		double x = pf.getImageableX();
		double y = pf.getImageableY();
		switch (pageIndex) {
		case 0:
			// 设置打印字体（字体名称、样式和点大小）（字体名称可以是物理或者逻辑名称）
			// Java平台所定义的五种字体系列：Serif、SansSerif、Monospaced、Dialog 和 DialogInput
			Font font = new Font("新宋体", Font.PLAIN, 10);
			g2.setFont(font);// 设置字体
			// BasicStroke bs_3=new BasicStroke(0.5f);
			float[] dash1 = { 2.0f };
			// 设置打印线的属性。
			// 1.线宽 2、3、不知道，4、空白的宽度，5、虚线的宽度，6、偏移量
			g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash1, 0.0f));
			// g2.setStroke(bs_3);//设置线宽*
			float heigth = font.getSize2D();// 字体高度
			// 用Graphics2D直接输出
			String[] datas = data.split(";");
			boolean[][] coordinate = PrintUtil.getCoordinate(datas[0]);
			int num = 1;
			for (int i = 1; i < datas.length; i++) {
				String string = datas[i];
				g2.drawString(string, (float) x, (float) y + num * heigth);
				num++;
			}
			g2.drawString(DateUtil.getNow(DateUtil.DATETIME_FORMAT_NO_SEC)+":请核对金额和选项", (float) x, (float) y + num * heigth);
			num++;
			for (int i = 0; i < coordinate.length; i++) {
				boolean[] row = coordinate[i];
				String value = heiBiao(row);
				if("".equals(value.trim())){
					continue;
				}
				g2.drawString(value, (float) x, (float) y + num * heigth);
				num++;
			}
			return PAGE_EXISTS;
		default:
			return NO_SUCH_PAGE;
		}

	}
    /**
     * 打印黑标
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月14日 下午5:44:49
     * @param row
     * @return
     */
	public static String heiBiao(boolean[] row) {
		StringBuilder sb = new StringBuilder();
		for (boolean b : row) {
			if(b){
				sb.append("▆");
				//sb.append("▃");
				sb.append(" ");
			}else{
				sb.append("   ");
			}
		}
		return sb.toString();
	}
}
