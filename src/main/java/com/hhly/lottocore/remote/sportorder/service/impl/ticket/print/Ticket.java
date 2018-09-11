package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print;

public class Ticket {
	//类容
	private String content;
    //彩种
	private int lotteryCode;
	//子玩法
	private int childCode;
	//子玩法类型
	private String childType;
	//内容类型
	private String contentType;
	//倍数
	private int multiple;
	
	private String machineKey;
	//大乐透追加
	private int lottoAdd;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLotteryCode() {
		return lotteryCode;
	}

	public void setLotteryCode(int lotteryCode) {
		this.lotteryCode = lotteryCode;
	}

	public int getChildCode() {
		return childCode;
	}

	public void setChildCode(int childCode) {
		this.childCode = childCode;
	}

	public String getChildType() {
		return childType;
	}

	public void setChildType(String childType) {
		this.childType = childType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the multiple
	 */
	public int getMultiple() {
		return multiple;
	}

	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	/**
	 * @return the machineKey
	 */
	public String getMachineKey() {
		return machineKey;
	}

	/**
	 * @param machineKey the machineKey to set
	 */
	public void setMachineKey(String machineKey) {
		this.machineKey = machineKey;
	}
	/**
	 * @return the lottoAdd
	 */
	public int getLottoAdd() {
		return lottoAdd;
	}

	/**
	 * @param lottoAdd the lottoAdd to set
	 */
	public void setLottoAdd(int lottoAdd) {
		this.lottoAdd = lottoAdd;
	}

}
