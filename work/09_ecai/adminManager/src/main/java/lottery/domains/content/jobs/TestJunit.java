package lottery.domains.content.jobs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javautils.date.DateUtil;
import lottery.domains.content.biz.LotteryOpenTimeService;
import lottery.domains.content.dao.LotteryOpenTimeDao;
import lottery.domains.content.entity.LotteryOpenTime;

@Component
public class TestJunit {

	@Autowired
	LotteryOpenTimeService service;
	
	@Autowired
	LotteryOpenTimeDao dao;

//	@Scheduled(cron = "0 19 * * * *")
	public void TestOpenTime() {
		String lottery = "gxk3";
		int expect = 1;
		int id = 44115;
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");// 24小时制  
		Calendar cal = Calendar.getInstance();
		Date startTime = DateUtil.parseDate("09:26:00", "HH:mm:ss");
		Date stopTime = DateUtil.parseDate("09:36:00", "HH:mm:ss");
		Date openTime = DateUtil.parseDate("09:38:00", "HH:mm:ss");
		
		for (int i = expect; i <= 78; i++) {
	
			id++;
			
			LotteryOpenTime jsk3 = new LotteryOpenTime();
			jsk3.setId(id);
			jsk3.setLottery(lottery);
			jsk3.setExpect(String.format("%03d", i));
			
			if(i > 1){
				cal.setTime(startTime);
				cal.add(Calendar.MINUTE, 10);
				startTime = cal.getTime();   
			}
			String strStart = format.format(startTime);
			
			jsk3.setStartTime(strStart);
			
			if(i > 1){
				cal.setTime(stopTime);
				cal.add(Calendar.MINUTE, 10);
				stopTime = cal.getTime();   
			}
			String strStop = format.format(stopTime);
			
			jsk3.setStopTime(strStop);
			
			if(i > 1){
				cal.setTime(openTime);
				cal.add(Calendar.MINUTE, 10);
				openTime = cal.getTime();
			}
			String strOpen = format.format(openTime);
			
			jsk3.setOpenTime(strOpen);
			jsk3.setIsTodayExpect(true);
			jsk3.setPlay(440);
			
			dao.save(jsk3);
		}
		
		
		lottery = "bjk3";
		startTime = DateUtil.parseDate("08:58:00", "HH:mm:ss");
		stopTime = DateUtil.parseDate("09:08:00", "HH:mm:ss");
		openTime = DateUtil.parseDate("09:10:00", "HH:mm:ss");
		
		for (int i = expect; i <= 89; i++) {
	
			id++;
			
			LotteryOpenTime jsk3 = new LotteryOpenTime();
			jsk3.setId(id);
			jsk3.setLottery(lottery);
			jsk3.setExpect(String.format("%03d", i));
			
			if(i > 1){
				cal.setTime(startTime);
				cal.add(Calendar.MINUTE, 10);
				startTime = cal.getTime();   
			}
			String strStart = format.format(startTime);
			
			jsk3.setStartTime(strStart);
			
			if(i > 1){
				cal.setTime(stopTime);
				cal.add(Calendar.MINUTE, 10);
				stopTime = cal.getTime();   
			}
			String strStop = format.format(stopTime);
			
			jsk3.setStopTime(strStop);
			
			if(i > 1){
				cal.setTime(openTime);
				cal.add(Calendar.MINUTE, 10);
				openTime = cal.getTime();
			}
			String strOpen = format.format(openTime);
			
			jsk3.setOpenTime(strOpen);
			jsk3.setIsTodayExpect(true);
			jsk3.setPlay(440);
			
			dao.save(jsk3);
		}
		
		lottery = "hebk3";
		startTime = DateUtil.parseDate("08:28:00", "HH:mm:ss");
		stopTime = DateUtil.parseDate("08:38:00", "HH:mm:ss");
		openTime = DateUtil.parseDate("08:40:00", "HH:mm:ss");
		
		for (int i = expect; i <= 81; i++) {
	
			id++;
			
			LotteryOpenTime jsk3 = new LotteryOpenTime();
			jsk3.setId(id);
			jsk3.setLottery(lottery);
			jsk3.setExpect(String.format("%03d", i));
			
			if(i > 1){
				cal.setTime(startTime);
				cal.add(Calendar.MINUTE, 10);
				startTime = cal.getTime();   
			}
			String strStart = format.format(startTime);
			
			jsk3.setStartTime(strStart);
			
			if(i > 1){
				cal.setTime(stopTime);
				cal.add(Calendar.MINUTE, 10);
				stopTime = cal.getTime();   
			}
			String strStop = format.format(stopTime);
			
			jsk3.setStopTime(strStop);
			
			if(i > 1){
				cal.setTime(openTime);
				cal.add(Calendar.MINUTE, 10);
				openTime = cal.getTime();
			}
			String strOpen = format.format(openTime);
			
			jsk3.setOpenTime(strOpen);
			jsk3.setIsTodayExpect(true);
			jsk3.setPlay(440);
			
			dao.save(jsk3);
		}
		
		
		lottery = "gsk3";
		startTime = DateUtil.parseDate("09:58:00", "HH:mm:ss");
		stopTime = DateUtil.parseDate("10:08:00", "HH:mm:ss");
		openTime = DateUtil.parseDate("10:10:00", "HH:mm:ss");
		
		for (int i = expect; i <= 72; i++) {
	
			id++;
			
			LotteryOpenTime jsk3 = new LotteryOpenTime();
			jsk3.setId(id);
			jsk3.setLottery(lottery);
			jsk3.setExpect(String.format("%03d", i));
			
			if(i > 1){
				cal.setTime(startTime);
				cal.add(Calendar.MINUTE, 10);
				startTime = cal.getTime();   
			}
			String strStart = format.format(startTime);
			
			jsk3.setStartTime(strStart);
			
			if(i > 1){
				cal.setTime(stopTime);
				cal.add(Calendar.MINUTE, 10);
				stopTime = cal.getTime();   
			}
			String strStop = format.format(stopTime);
			
			jsk3.setStopTime(strStop);
			
			if(i > 1){
				cal.setTime(openTime);
				cal.add(Calendar.MINUTE, 10);
				openTime = cal.getTime();
			}
			String strOpen = format.format(openTime);
			
			jsk3.setOpenTime(strOpen);
			jsk3.setIsTodayExpect(true);
			jsk3.setPlay(440);
			
			dao.save(jsk3);
		}
		
		lottery = "jxk3";
		startTime = DateUtil.parseDate("08:53:00", "HH:mm:ss");
		stopTime = DateUtil.parseDate("09:03:00", "HH:mm:ss");
		openTime = DateUtil.parseDate("09:05:00", "HH:mm:ss");
		
		for (int i = expect; i <= 84; i++) {
	
			id++;
			
			LotteryOpenTime jsk3 = new LotteryOpenTime();
			jsk3.setId(id);
			jsk3.setLottery(lottery);
			jsk3.setExpect(String.format("%03d", i));
			
			if(i > 1){
				cal.setTime(startTime);
				cal.add(Calendar.MINUTE, 10);
				startTime = cal.getTime();   
			}
			String strStart = format.format(startTime);
			
			jsk3.setStartTime(strStart);
			
			if(i > 1){
				cal.setTime(stopTime);
				cal.add(Calendar.MINUTE, 10);
				stopTime = cal.getTime();   
			}
			String strStop = format.format(stopTime);
			
			jsk3.setStopTime(strStop);
			
			if(i > 1){
				cal.setTime(openTime);
				cal.add(Calendar.MINUTE, 10);
				openTime = cal.getTime();
			}
			String strOpen = format.format(openTime);
			
			jsk3.setOpenTime(strOpen);
			jsk3.setIsTodayExpect(true);
			jsk3.setPlay(440);
			
			dao.save(jsk3);
		}
	}
	
	public static void main(String[] args) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");// 24小时制  
		Date date = DateUtil.parseDate("08:29:00", "HH:mm:ss");
		
		Calendar cal = Calendar.getInstance();
		for (int i = 1; i <= 82; i++) {
			cal.setTime(date);
			cal.add(Calendar.MINUTE, 10);
			date = cal.getTime();   
			System.out.println("第:"+i +":" + format.format(date));  //显示更新后的日期
		}
		
		System.out.println(String.format("%03d", 52));
	}

}
