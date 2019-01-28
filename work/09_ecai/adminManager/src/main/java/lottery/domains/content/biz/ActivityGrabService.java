package lottery.domains.content.biz;

import java.util.Map;

import javautils.jdbc.PageList;

public interface ActivityGrabService {
	
	PageList searchBill(String username, String date, int start, int limit);
	
	/**
	 * 获取已发放总额和今天发放总额
	 * @return
	 */
	Map<String, Double> getOutTotalInfo();
	
}