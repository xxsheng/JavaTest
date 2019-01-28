package lottery.domains.content.dao.read;

import lottery.domains.content.entity.UserBetsHitRanking;

import java.util.List;

public interface UserBetsHitRankingReadDao {
	List<UserBetsHitRanking> listAll(int count);
}