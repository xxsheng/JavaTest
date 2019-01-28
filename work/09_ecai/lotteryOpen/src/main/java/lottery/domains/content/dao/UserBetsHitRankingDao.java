package lottery.domains.content.dao;

import lottery.domains.content.entity.UserBetsHitRanking;

import java.util.List;

public interface UserBetsHitRankingDao {
	long getTotalSize(int platform);

	UserBetsHitRanking getMinRanking(int platform, String startTime, String endTime);

	boolean add(UserBetsHitRanking ranking);

	List<Integer> getIds(int platform, String startTime, String endTime);

	List<Integer> getTotalIds(int count, int platform, String startTime, String endTime);

	List<Integer> getIdsByTimeDesc(int count, int platform);

	int delNotInIds(List<Integer> ids, int platform);

	boolean delNotInIds(List<Integer> ids, int platform, String startTime, String endTime);

	boolean delByTime(int platform, String endTime);
}