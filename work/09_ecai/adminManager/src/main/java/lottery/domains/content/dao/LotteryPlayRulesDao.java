package lottery.domains.content.dao;

import lottery.domains.content.entity.LotteryPlayRules;

import java.util.List;

public interface LotteryPlayRulesDao {
	List<LotteryPlayRules> listAll();

	List<LotteryPlayRules> listByType(int typeId);

	List<LotteryPlayRules> listByTypeAndGroup(int typeId, int groupId);

	LotteryPlayRules getById(int id);

	boolean update(LotteryPlayRules entity);

	boolean updateStatus(int id, int status);

	boolean update(int id, String minNum, String maxNum);
}
