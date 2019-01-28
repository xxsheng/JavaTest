package lottery.domains.content.dao.read.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserGameDividendBillReadDao;
import lottery.domains.content.entity.UserGameDividendBill;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class UserGameDividendBillReadDaoImpl implements UserGameDividendBillReadDao {
	private final String tab = UserGameDividendBill.class.getSimpleName();

	@Autowired
	private HibernateSuperReadDao<UserGameDividendBill> superDao;

	@Override
	public PageList searchByZhuGuans(List<Integer> userIds, String sTime, String eTime, int start, int limit) {
		StringBuffer countHql = new StringBuffer("select count(ugdb.id) from " + tab + " ugdb,User u where ugdb.userId = u.id and (");
		StringBuffer queryHql = new StringBuffer("select ugdb from " + tab + " ugdb,User u where ugdb.userId = u.id and (");

		List<Object> params = new LinkedList<>();

		int paramIndex = 0;
		for (int i = 0; i < userIds.size(); i++) {
			countHql.append(" u.upids like ?").append(paramIndex);
			queryHql.append(" u.upids like ?").append(paramIndex++);

			if (i < userIds.size() - 1) {
				countHql.append(" or");
				queryHql.append(" or");
			}

			params.add("%[" + userIds.get(i) + "]%");
		}
		countHql.append(") and u.id > 0 and ugdb.id > 0");
		queryHql.append(") and u.id > 0 and ugdb.id > 0");


		if (StringUtils.isNotEmpty(sTime)) {
			countHql.append(" and ugdb.indicateStartDate>=?").append(paramIndex);
			queryHql.append(" and ugdb.indicateStartDate>=?").append(paramIndex++);
			params.add(sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countHql.append(" and ugdb.indicateEndDate<?").append(paramIndex);
			queryHql.append(" and ugdb.indicateEndDate<?").append(paramIndex++);
			params.add(eTime);
		}

		queryHql.append(" order by ugdb.settleTime desc,ugdb.id desc");

		Object[] values = params.toArray();
		return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
	}

	@Override
	public PageList searchByLowers(int upUserId, String sTime, String eTime, int start, int limit) {
		String countHql = "select count(ugdb.id) from " + tab + " ugdb,User u where ugdb.userId = u.id and (ugdb.userId = ?0 or u.upids like ?1) and u.id > 0 and ugdb.id > 0";
		String queryHql = "select ugdb from " + tab + " ugdb,User u where ugdb.userId = u.id and (ugdb.userId = ?0 or u.upids like ?1) and u.id > 0 and ugdb.id > 0";

		List<Object> params = new LinkedList<>();
		params.add(upUserId);
		params.add("%["+upUserId+"]%");

		int paramIndex = 2;
		if (StringUtils.isNotEmpty(sTime)) {
			countHql += " and ugdb.indicateStartDate>=?" + paramIndex;
			queryHql += " and ugdb.indicateStartDate>=?" + paramIndex++;
			params.add(sTime);
		}
		if (StringUtils.isNotEmpty(eTime)) {
			countHql += " and ugdb.indicateEndDate<?" + paramIndex;
			queryHql += " and ugdb.indicateEndDate<?" + paramIndex++;
			params.add(eTime);
		}

		queryHql += " order by ugdb.settleTime desc,ugdb.id desc";

		Object[] values = params.toArray();
		return superDao.findPageList(queryHql, countHql, values, start, limit);
	}
}