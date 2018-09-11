package com.hhly.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.PaperDispose;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.util.PrintUtil;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.util.SportPrintable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
@Transactional
public class PaperTests {

    
	@Autowired
	PaperDispose dispose;
	
	@Test
	public void three() {
		Ticket ticket = new Ticket();
		ticket.setContent("1807227001(30@50.00,31@28.00,32@28.00,03@12.00,13@10.50)|1807227002(31@12.00,40@26.00,99@300.00,12@11.00,03@50.00)^2_1");
		ticket.setChildCode(30004);
		ticket.setLotteryCode(300);
		ticket.setChildType("2_1");
		ticket.setMultiple(68);
		String string = dispose.coordinate(ticket);
		System.out.println("---------"+string);
		PrintUtil.print(new SportPrintable(string));
	}
	
	@Test
	public void six() {
		Ticket ticket = new Ticket();
		ticket.setContent("1807017001_Z(1@15.50,0@70.00,7@28.00)|1807017002_S(1@15.50)|1807017003_R(1@15.50)|1807017004_Z(1@15.50,5@12)|1807017005_S(1@15.50,0@12)|1807017151_S(3@2.58,1@3.20,0@2.18)^6_1");
		ticket.setChildCode(30001);
		ticket.setLotteryCode(300);
		ticket.setChildType("6_1");
		ticket.setMultiple(39);
		try {
			String string = dispose.coordinate(ticket);
			System.out.println(string);
			PrintUtil.print(new SportPrintable(string));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void eight() {
		Ticket ticket = new Ticket();
		ticket.setContent("1807017001_S(3@1.2,1@15.50)|1807017002_R(3@12,1@15.50)|1807017003_R(1@15.50)|1807017004_R(1@15.50)|1807017005_R(1@15.50)|1807017006_R(1@15.50)|1807017007_R(1@15.50)|1807017151_R(3@2.58,1@3.20,0@2.18)^8_1");
		ticket.setChildCode(30001);
		ticket.setLotteryCode(300);
		ticket.setChildType("8_1");
		ticket.setMultiple(39);
		try {
			String string = dispose.coordinate(ticket);
			System.out.println(string);
			//PrintUtil.print(new SportPrintable(string));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void dlt() {
		Ticket ticket = new Ticket();
		ticket.setContent("01,02,03,04,05,06+02,03");
		ticket.setChildCode(10201);
		ticket.setLotteryCode(102);
		ticket.setMultiple(99);
		ticket.setLottoAdd(0);
		ticket.setContentType("2");
		try {
			String string = dispose.coordinate(ticket);
			System.out.println(string);
			PrintUtil.print(new SportPrintable(string));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void dtdlt() {
		Ticket ticket = new Ticket();
		ticket.setContent("27,28,29,30,31+12#01,02,03,04,05,06,07,08,09,10,11");
		ticket.setChildCode(10202);
		ticket.setLotteryCode(102);
		ticket.setMultiple(85);
		ticket.setLottoAdd(0);
		ticket.setContentType("3");
		try {
			String string = dispose.coordinate(ticket);
			System.out.println(string);
			PrintUtil.print(new SportPrintable(string));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
