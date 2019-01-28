package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesGroupDao;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesGroupDaoImpl implements LotteryPlayRulesGroupDao {

	private final String tab = LotteryPlayRulesGroup.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRulesGroup> superDao;
	
	@Override
	public List<LotteryPlayRulesGroup> listAll() {
		String hql = "from " + tab + " order by typeId,sort";
		return superDao.list(hql);
	}
}