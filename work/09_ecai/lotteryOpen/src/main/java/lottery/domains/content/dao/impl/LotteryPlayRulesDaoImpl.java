package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesDao;
import lottery.domains.content.entity.LotteryPlayRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesDaoImpl implements LotteryPlayRulesDao {

	private final String tab = LotteryPlayRules.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRules> superDao;
	
	@Override
	public List<LotteryPlayRules> listAll() {
		String hql = "from " + tab + " order by typeId, groupId";
		return superDao.list(hql);
	}
}