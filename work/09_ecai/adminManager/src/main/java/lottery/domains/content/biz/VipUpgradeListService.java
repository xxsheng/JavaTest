package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface VipUpgradeListService {
	
	PageList search(String username, String month, int start, int limit);
	
	boolean add(int userId, int beforeLevel, int afterLevel, double recharge, double cost, String month);
	
}