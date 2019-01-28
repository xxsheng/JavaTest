package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.user.UserTeamStatisticsVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class UserReadDaoImpl implements UserReadDao {
	
	public static final String tab = User.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<User> superDao;

	@Override
	public List<User> listAllFromRead() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

	@Override
	public User getByIdFromRead(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (User) superDao.unique(hql, values);
	}

	@Override
	public User getByUsernameFromRead(String username) {
		String hql = "from " + tab + " where username = ?0";
		Object[] values = {username};
		return (User) superDao.unique(hql, values);
	}

	@Override
	public List<User> getUserDirectLowerFromRead(int id) {
		String hql = "from " + tab + " where upid = ?0 and id > 0";
		Object[] values = {id};
		return superDao.list(hql, values);
	}

	@Override
	public List<User> getUserDirectLowerFromRead(int id, int code) {
		String hql = "from " + tab + " where upid = ?0 and code = ?1 and id > 0";
		Object[] values = {id, code};
		return superDao.list(hql, values);
	}

	@Override
	public List<User> getUserDirectLowerFromRead(int id, int[] codes) {
		String hql = "from " + tab + " where upid = ?0 and code in ("+ ArrayUtils.transInIds(codes)+") and id > 0";
		Object[] values = {id};
		return superDao.list(hql, values);
	}

	@Override
	public List<User> getUserLowerFromRead(int id) {
		String hql = "from " + tab + " where upids like ?0";
		Object[] values = {"%[" + id + "]%"};
		return superDao.list(hql, values);
	}

	@Override
	public List<User> getUserLowerFromRead(List<Integer> userIds) {
		StringBuffer hql = new StringBuffer("from ").append(tab).append(" where");

		List<String> params = new LinkedList<>();
		for (int i = 0; i < userIds.size(); i++) {
			if (i > 0) {
				hql.append(" or");
			}
			hql.append(" upids like ?").append(i);
			params.add("%[" + userIds.get(i) + "]%");
		}
		return superDao.list(hql.toString(), params.toArray());
	}

	@Override
	public List<User> listFromRead(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(User.class, criterions, orders);
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(User.class, criterions, orders, start, limit);
	}

	@Override
	public UserTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
		StringBuffer hql = new StringBuffer("select count(id), sum(case when onlineStatus=1 and sessionId is not null then 1 else 0 end), sum(case when registTime >= ?0 and registTime < ?1 then 1 else 0 end),sum(totalMoney),sum(lotteryMoney) from ").append(tab).append(" where id = ?2 or upids like ?3");

		List<Object> params = new LinkedList<>();
		params.add(sDate);
		params.add(eDate);
		params.add(userId);
		params.add("%[" + userId + "]%");

		Object[] values = params.toArray();
		Object unique = superDao.unique(hql.toString(), values);
		if (unique == null) {
			return new UserTeamStatisticsVO();
		}

		Object[] results = (Object[]) unique;
		int index = 0;
		int totalUser = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		int onlineUser = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		int totalRegister = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		double totalMoney = results[index] == null ? 0 : ((Double) results[index]).doubleValue(); index++;
		double lotteryMoney = results[index] == null ? 0 : ((Double) results[index]).doubleValue(); index++;

		UserTeamStatisticsVO uTeamStatisticsVO = new UserTeamStatisticsVO();
		uTeamStatisticsVO.setTotalUser(totalUser);
		uTeamStatisticsVO.setOnlineUser(onlineUser);
		uTeamStatisticsVO.setTotalRegister(totalRegister);
		uTeamStatisticsVO.setTotalBalance(totalMoney);
		uTeamStatisticsVO.setLotteryBalance(lotteryMoney);
		return uTeamStatisticsVO;
	}

	@Override
	public UserTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
		StringBuffer sql = new StringBuffer("select count(id), sum(case when online_status=1 and session_id is not null then 1 else 0 end), sum(case when regist_time >= :sDate and regist_time < :eDate then 1 else 0 end),sum(total_money),sum(lottery_money) from user where (id in ("+ArrayUtils.transInIds(userIds)+")) or (");

		Map<String, Object> params = new HashMap<>();
		params.put("sDate", sDate);
		params.put("eDate", eDate);

		for (int i = 0; i < userIds.length; i++) {
			sql.append("upids like :upid").append(i);
			params.put("upid" + i, "%["+userIds[i]+"]%");

			if (i < userIds.length - 1) {
				sql.append(" or ");
			}
		}
		sql.append(")");

		Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
		if (unique == null) {
			return new UserTeamStatisticsVO();
		}

		Object[] results = (Object[]) unique;
		int index = 0;
		int totalUser = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		int onlineUser = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		int totalRegister = results[index] == null ? 0 : Integer.valueOf(results[index].toString()); index++;
		double totalMoney = results[index] == null ? 0 : ((BigDecimal) results[index]).doubleValue(); index++;
		double lotteryMoney = results[index] == null ? 0 : ((BigDecimal) results[index]).doubleValue(); index++;

		UserTeamStatisticsVO uTeamStatisticsVO = new UserTeamStatisticsVO();
		uTeamStatisticsVO.setTotalUser(totalUser);
		uTeamStatisticsVO.setOnlineUser(onlineUser);
		uTeamStatisticsVO.setTotalRegister(totalRegister);
		uTeamStatisticsVO.setTotalBalance(totalMoney);
		uTeamStatisticsVO.setLotteryBalance(lotteryMoney);
		return uTeamStatisticsVO;
	}

	@Override
	public UserTeamStatisticsVO statisticsAll(String sDate, String eDate) {
		StringBuffer hql = new StringBuffer("select count(id), sum(case when onlineStatus=1 and sessionId is not null then 1 else 0 end), sum(case when registTime >= ?0 and registTime < ?1 then 1 else 0 end),sum(totalMoney),sum(lotteryMoney) from ").append(tab).append("   where upid != 0 ");

		List<Object> params = new LinkedList<>();
		params.add(sDate);
		params.add(eDate);

		Object[] values = params.toArray();
		Object unique = superDao.unique(hql.toString(), values);
		if (unique == null) {
			return new UserTeamStatisticsVO();
		}

		Object[] results = (Object[]) unique;
		int index = 0;
		int totalUser = results[index] == null ? 0 : Integer.valueOf(results[index].toString());index++;
		int onlineUser = results[index] == null ? 0 : Integer.valueOf(results[index].toString());index++;
		int totalRegister = results[index] == null ? 0 : Integer.valueOf(results[index].toString());index++;
		double totalMoney = results[index] == null ? 0 : ((Double) results[index]).doubleValue();index++;
		double lotteryMoney = results[index] == null ? 0 : ((Double) results[index]).doubleValue();index++;

		UserTeamStatisticsVO uTeamStatisticsVO = new UserTeamStatisticsVO();
		uTeamStatisticsVO.setTotalUser(totalUser);
		uTeamStatisticsVO.setOnlineUser(onlineUser);
		uTeamStatisticsVO.setTotalRegister(totalRegister);
		uTeamStatisticsVO.setTotalBalance(totalMoney);
		uTeamStatisticsVO.setLotteryBalance(lotteryMoney);
		return uTeamStatisticsVO;
	}

	@Override
	public List<?> getDayRegistAll(String sTime, String eTime) {
		String sql = "select substring(regist_time, 1, 10), count(id) from user where regist_time >= :sTime  and upid != 0 and regist_time < :eTime group by substring(regist_time, 1, 10)";

		Map<String, Object> params = new HashMap<>();
		params.put("sTime", sTime);
		params.put("eTime", eTime);

		return superDao.listBySql(sql, params);
	}

	@Override
	public List<?> getDayRegistByTeam(int userId, String sTime, String eTime) {
		return getDayRegistByTeam(new int[]{userId}, sTime, eTime);
	}

	@Override
	public List<?> getDayRegistByTeam(int[] userIds, String sTime, String eTime) {
		StringBuffer sql = new StringBuffer("select substring(regist_time, 1, 10), count(id) from user where (id in(:userIds)");

		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < userIds.length; i++) {
			sql.append(" or upids like :upid").append(i);
			params.put("upid" + i, "%[" + userIds[i] + "]%");
		}

		sql.append(")and regist_time >= :sTime and regist_time < :eTime group by substring(regist_time, 1, 10)");

		params.put("sTime", sTime);
		params.put("eTime", eTime);
		params.put("userIds", ArrayUtils.transInIds(userIds));

		return superDao.listBySql(sql.toString(), params);
	}
}