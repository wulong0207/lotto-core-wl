package com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.limitvalidator.module;

public interface LimitTranslator {
	//T translate(T arg1, T arg2);
	//T translate(T arg1, T arg2);
	String translate(String originalLimitContent, Integer lotteryChildCode);
	boolean whetherTraslate(Integer lotteryChildCode);
}