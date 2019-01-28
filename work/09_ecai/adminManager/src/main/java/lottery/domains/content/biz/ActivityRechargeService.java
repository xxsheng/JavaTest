package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface ActivityRechargeService {
	
	PageList search(String username, String date, String keyword, Integer status, int start, int limit);
	
	/**
	 * 发放
	 */
	boolean agree(int id);
	
	/**
	 * 拒绝
	 */
	boolean refuse(int id);

	/**
	 * 检查
	 */
	boolean check(int id);
	
}