package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserLotteryDetailsReportDao;
import lottery.domains.content.entity.UserLotteryDetailsReport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserLotteryDetailsReportDaoImpl implements UserLotteryDetailsReportDao {
	
	private final String tab = UserLotteryDetailsReport.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserLotteryDetailsReport> superDao;

	@Override
	public boolean add(UserLotteryDetailsReport entity) {
		return superDao.save(entity);
	}

	@Override
	public UserLotteryDetailsReport get(int userId, int lotteryId, int ruleId, String time) {
		String hql = "from " + tab + " where userId = ?0 and lotteryId = ?1 and ruleId = ?2 and time = ?3";
		Object[] values = {userId, lotteryId, ruleId, time};
		return (UserLotteryDetailsReport) superDao.unique(hql, values);
	}

	@Override
	public boolean update(UserLotteryDetailsReport entity) {
		String hql = "update " + tab + " set spend = spend + ?1, prize = prize + ?2, spendReturn = spendReturn + ?3, proxyReturn = proxyReturn + ?4, cancelOrder = cancelOrder + ?5, billingOrder = billingOrder + ?6 where id = ?0";
		Object[] values = {entity.getId(), entity.getSpend(), entity.getPrize(), entity.getSpendReturn(), entity.getProxyReturn(), entity.getCancelOrder(), entity.getBillingOrder()};
		return superDao.update(hql, values);
	}

	@Override
	public void addDetailsReports(List<UserLotteryDetailsReport> detailsReports) {
		// 批量查找哪些是存在的
		List<UserLotteryDetailsReport> oldDatas = getOldDatas(detailsReports);

		// 区分需要添加和修改的
		Object[] compared = compare(detailsReports, oldDatas);

		// 批量添加
		List<UserLotteryDetailsReport> adds = (List<UserLotteryDetailsReport>)compared[0];
		batchAddDetailsReports(adds);

		// 批量修改
		List<UserLotteryDetailsReport> updates = (List<UserLotteryDetailsReport>)compared[1];
		batchUpdateDetailsReports(updates);
	}

	/**
	 * 对比数据，第1位是需要添加的，第2位是需要修改的
	 */
	private Object[] compare(List<UserLotteryDetailsReport> detailsReports,
							 List<UserLotteryDetailsReport> oldDatas) {
		// 对比需要添加的
		List<UserLotteryDetailsReport> adds = new ArrayList<>();
		for (UserLotteryDetailsReport detailsReport : detailsReports) {
			boolean exist = false;
			for (UserLotteryDetailsReport oldData : oldDatas) {
				boolean equalsOld = equalsOld(detailsReport, oldData);
				if (equalsOld) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				adds.add(detailsReport);
			}
		}

		// 需要修改的
		List<UserLotteryDetailsReport> updates = new ArrayList<>();
		for (UserLotteryDetailsReport oldData : oldDatas) {
			for (UserLotteryDetailsReport detailsReport : detailsReports) {
				boolean equalsOld = equalsOld(detailsReport, oldData);
				if (equalsOld) {
					detailsReport.setId(oldData.getId());
					updates.add(detailsReport);
					break;
				}
			}
		}

		return new Object[]{adds, updates};
	}

	private boolean equalsOld(UserLotteryDetailsReport detailsReport, UserLotteryDetailsReport oldData) {
		return detailsReport.getUserId() == oldData.getUserId()
				&& detailsReport.getLotteryId() == oldData.getLotteryId()
				&& detailsReport.getRuleId() == oldData.getRuleId()
				&& detailsReport.getTime().equalsIgnoreCase(oldData.getTime());
	}

	/**
	 * 查找存在的报表详情
	 */
	private List<UserLotteryDetailsReport> getOldDatas(List<UserLotteryDetailsReport> detailsReports) {

		// 参数列表
		Set<Integer> userIds = new HashSet<>();
		Set<Integer> lotteryIds = new HashSet<>();
		Set<Integer> ruleIds = new HashSet<>();
		Set<String> times = new HashSet<>();
		for (UserLotteryDetailsReport detailsReport : detailsReports) {
			userIds.add(detailsReport.getUserId());
			lotteryIds.add(detailsReport.getLotteryId());
			ruleIds.add(detailsReport.getRuleId());
			times.add(detailsReport.getTime());
		}

		// 查询数据
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.in("userId", userIds));
		criterions.add(Restrictions.in("lotteryId", lotteryIds));
		criterions.add(Restrictions.in("ruleId", ruleIds));
		criterions.add(Restrictions.in("time", times));

		List<UserLotteryDetailsReport> result = superDao.findByCriteria(UserLotteryDetailsReport.class, criterions, null);

		return result;
	}

	private void batchAddDetailsReports(List<UserLotteryDetailsReport> adds) {
		if (CollectionUtils.isEmpty(adds)) {
			return;
		}

		String sql = "insert into `user_lottery_details_report`(`user_id`, `lottery_id`, `rule_id`, " +
				"`spend`, `prize`, `spend_return`, `proxy_return`, `cancel_order`, `billing_order`, `time`) " +
				"values(?,?,?,?,?,?,?,?,?,?)";
		List<Object[]> params = new ArrayList<>();
		for (UserLotteryDetailsReport tmp : adds) {
			Object[] param = {tmp.getUserId(), tmp.getLotteryId(), tmp.getRuleId(),
					tmp.getSpend(), tmp.getPrize(), tmp.getSpendReturn(),
					tmp.getProxyReturn(), tmp.getCancelOrder(), tmp.getBillingOrder(), tmp.getTime()};
			params.add(param);
		}
		superDao.doWork(sql, params);
	}

	private void batchUpdateDetailsReports(List<UserLotteryDetailsReport> updates) {
		if (CollectionUtils.isEmpty(updates)) {
			return;
		}

		String sql = "update `user_lottery_details_report` set " +
				"`prize` = `prize` + ?, " +
				"`spend_return` = `spend_return` + ?, " +
				"`proxy_return` = `proxy_return` + ?, " +
				"`billing_order` = `billing_order` + ? " +
				"where `id` = ?";

		List<Object[]> params = new ArrayList<>();
		for (UserLotteryDetailsReport tmp : updates) {
			Object[] param = {tmp.getPrize(), tmp.getSpendReturn(),
					tmp.getProxyReturn(), tmp.getBillingOrder(), tmp.getId()};
			params.add(param);
		}

		superDao.doWork(sql, params);
	}
}