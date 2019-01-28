package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesGroupConfigDao;
import lottery.domains.content.entity.LotteryPlayRulesGroupConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesGroupConfigDaoImpl implements LotteryPlayRulesGroupConfigDao {

	private final String tab = LotteryPlayRulesGroupConfig.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRulesGroupConfig> superDao;
	
	@Override
	public List<LotteryPlayRulesGroupConfig> listAll() {
		String hql = "from " + tab + " order by id";
		return superDao.list(hql);
	}
}