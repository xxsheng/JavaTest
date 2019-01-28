package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface VipBirthdayGiftsService {
	
	PageList search(String username, Integer level, String date, Integer status, int start, int limit);
	
	boolean calculate(String birthday);
	
}