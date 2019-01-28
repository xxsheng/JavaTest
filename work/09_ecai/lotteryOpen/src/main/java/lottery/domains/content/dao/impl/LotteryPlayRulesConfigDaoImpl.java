package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesConfigDao;
import lottery.domains.content.entity.LotteryPlayRulesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesConfigDaoImpl implements LotteryPlayRulesConfigDao {

	private final String tab = LotteryPlayRulesConfig.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRulesConfig> superDao;
	
	@Override
	public List<LotteryPlayRulesConfig> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}
}