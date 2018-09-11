package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.util;

/**
 * @desc 坐标
 * @author jiangwei
 * @date 2018年7月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public class Coordinate {
	
    private int row;
    
    private int col;
    
	public Coordinate(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
    
    
}
