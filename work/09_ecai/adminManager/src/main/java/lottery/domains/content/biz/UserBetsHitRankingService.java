package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBetsHitRanking;

public interface UserBetsHitRankingService {

	PageList search(int start, int limit);
	
	boolean add(String name, String username, int prizeMoney, String time, String code, String type, int platform);

	boolean edit(int id, String name, String username, int prizeMoney, String time, String code, String type, int platform);

	boolean delete(int id);

	UserBetsHitRanking getById(int id);
}