package lottery.domains.content.dao;

import lottery.domains.content.entity.LotteryPlayRulesGroup;

import java.util.List;

public interface LotteryPlayRulesGroupDao {
	
	List<LotteryPlayRulesGroup> listAll();

	List<LotteryPlayRulesGroup> listByType(int typeId);

	LotteryPlayRulesGroup getById(int id);
	
	boolean update(LotteryPlayRulesGroup entity);
	
	boolean updateStatus(int id, int status);
}
