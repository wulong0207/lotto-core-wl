package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.util;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

public class PrintUtil {
	public static void print(Printable able) {
		// 通俗理解就是书、文档
		Book book = new Book();
		// 设置成竖打
		PageFormat pf = new PageFormat();
		pf.setOrientation(PageFormat.PORTRAIT);
		// 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
		Paper p = new Paper();
		p.setSize(215, 1000);// 纸张大小
		p.setImageableArea(0, 0, 215, 1000);// A4(595 X
											// 842)设置打印区域，其实0，0应该是72，72，因为A4纸的默认X,Y边距是72
		pf.setPaper(p);
		// 把 PageFormat 和 Printable 添加到书中，组成一个页面
		book.append(able, pf);

		// 获取打印服务对象
		PrinterJob job = PrinterJob.getPrinterJob();
		// 设置打印类
		job.setPageable(book);
		try {
			// 可以用printDialog显示打印对话框，在用户确认后打印；也可以直接打印
			// boolean a=job.printDialog();
			// if(a)
			// {
			job.print();
			// }
		} catch (PrinterException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 格式转换
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月14日 下午5:34:50
	 * @param content
	 * @return
	 */
	public static boolean[][] getCoordinate(String content) {
		List<Coordinate> list = new ArrayList<>();
		String[] str;
		int maxCol = 0;
		int maxRow = 0;
		for (String coo : content.split(",")) {
			str = coo.split("_");
			int row = Integer.parseInt(str[0]);
			int col = Integer.parseInt(str[1]);
			list.add(new Coordinate(row, col));
			if (maxCol < col) {
				maxCol = col;
			}
			if (maxRow < row) {
				maxRow = row;
			}
		}
		boolean[][] value = new boolean[maxRow + 1][maxCol + 1];
		for (Coordinate coordinate : list) {
			value[coordinate.getRow()][coordinate.getCol()] = true;
		}
		return value;
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 33; i++) {
			if (i % 2 == 0) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(i + "_0");
			}
		}
		sb.append(",1_1,1_2,1_3,1_7,1_12,33_13");
		System.err.println(sb.toString());
	}
}
