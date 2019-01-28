package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBetsLimit;
import lottery.domains.content.vo.user.UserBetsLimitVO;

public interface UserBetsLimitService {

	boolean addUserBetsLimit(String username, int lotteryId, double maxBet, String source, double maxPrize);
	
	PageList search(String username, int start, int limit, boolean queryGobalSetting);
	
	boolean updateUserBetsLimit(UserBetsLimit ubLimit);
	
	boolean deleteUserBetsLimit(int id);
	
	boolean addOrUpdate(Integer id, String username, int lotteryId, double maxBet, String source, double maxPrize);
	
	UserBetsLimitVO getById(int id);
}
