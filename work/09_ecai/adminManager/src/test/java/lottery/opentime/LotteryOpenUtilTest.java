package lottery.opentime;

import lottery.JunitEnvironment;
import lottery.domains.content.biz.LotteryOpenTimeService;
import lottery.domains.content.entity.LotteryOpenTime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;

import java.util.List;

/**
* Created by Nick on 2016/10/1.
*/
public class LotteryOpenUtilTest extends JunitEnvironment {
   @Autowired
   LotteryOpenTimeService service;
   
   @Test
   public void getCurrOpenTime() throws Exception {
	   String lottery = "shk3";
		String expect = "";
		int start = 0;
		int limit = 100;
		PageList pList = service.search(lottery, expect, start, limit);
		List list = pList.getList();
		for (Object object : list) {
			LotteryOpenTime ot = (LotteryOpenTime) object;
			System.out.println(ot.getId() + "   " + ot.getLottery());
		}
   }

}