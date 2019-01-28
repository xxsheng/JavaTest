package lottery.domains.content.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javautils.ObjectUtil;
import javautils.StringUtil;
import javautils.date.Moment;
import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.global.Database;
import lottery.domains.pool.LotteryDataFactory;

/*
 * 每天早上凌晨5点30开始备份与删除数据
 */
@Component
public class DataBaseBackupJob {

	private final Logger logger = LoggerFactory.getLogger(DataBaseBackupJob.class);

	boolean isRunning = false;

	@Scheduled(cron = "0 30 5 * * *")
	// @PostConstruct
	public void start() {
		if (!isRunning) {
			isRunning = true;
			try {
				// 备份彩票投注记录保留1个月,备份3个月
				backupUserBets();

				// 删除试玩用户原始投注订单,保留一天
				deleteDemoUserBetsOriginal();

				// 备份原始投注订单,保留31天
				deleteUserBetsOriginal();

				// 备份用户第三方游戏投注记录保留1个月, 备份3个月
				backupGameBets();

				// 备份注单记录保留1个月,备份3个月
				backupUserBill();

				// 删除系统消息,保留7天
				deleteSysMessage();

				// 备份用户消息保留3个月, 备份12个月
				backupUserMessage();

				// 删除开奖号码
				deleteOpenCode();

				// 备份后台操作日志保留6个月, 备份12个月
				backupAdminUserLog();

				// 备份后台详细日志保留6个月, 备份12个月
				backupAdminActionLog();

				// 备份前台用户登录日志保留12个月, 备份36个月
				backupUserLoginLog();

				// 备份前台用户操作日志保留12个月, 备份36个月
				backupUserActionLog();

				// 备份未支付充值记录保留7天, 备份1个月
				backupUserRechargeUnPaid();

				// 备份充值记录保留3个月, 备份36个月
				backupUserRecharge();

				// 备份取现记录保留3个月, 备份36个月
				backupUserWithdraw();

				// 备份转账记录保留1个月, 备份12个月
				backupUserTransfer();

				// 备份红包雨账单保留1个月, 备份6个月
				backupRedPacketRainBill();

				// 备份红包雨时间保留1个月, 备份6个月
				backupRedPacketRainTime();

				// 删除大额中奖记录，保留一个月
				deleteUserHighPrize();
				
				// 删除大额中奖记录，保留一天
				deleteDemoUserHighPrize();

				// 备份日结账单保留2个月,备份6个月
				backupDailySettleBill();

				// 备份分红账单保留2个月, 备份6个月
				backupDividendBill();

				// 备份老虎机真人体育返水保留2个月, 备份6个月
				backupGameWaterBill();

				// 备份老虎机真人体育分红保留2个月, 备份6个月
				backupGameDividendBill();

				// 备份主账户报表保留3个月,备份6个月
				backupUserMainReport();

				// 备份彩票报表保留2个月,备份6个月
				backupUserLotteryReport();

				// 备份彩票详情报表保留2个月,备份6个月
				backupUserLotteryDetailsReport();

				// 备份游戏报表保留2个月,备份6个月
				backupUserGameReport();
			} catch (Exception e) {
				logger.error("备份数据出错", e);
			} finally {
				isRunning = false;
			}
		}
	}

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private LotteryDao lotteryDao;

	@Autowired
	private LotteryDataFactory dataFactory;

	private final int LIMIT_UNIT_COUNT = 500; // 单次转移记录数量
	private final int USER_BETS_REMAIN_MONTH = 1; // 投注数据保留多少个月
	// private final int USER_BETS_BACKUP_REMAIN_MONTH = 3; // 投注备份数据保留多少个月
	private final int USER_RECHARGE_UNPAID_REMAIN_DAYS = 2; // 保留多少天用户未充值记录
	private final int USER_RECHARGE_REMAIN_MONTH = 3; // 保留多少月用户充值记录
	private final int USER_HIGH_PRIZE_REMAIN_MONTH = 1; // 保留多少月大额中奖记录
	private final int USER_BILL_REMAIN_MONTH = 1; // 账单数据保留多少个月
	private final int USER_BILL_BACKUP_REMAIN_MONTH = 6; // 账单备份数据保留多少个月
	private final int OUT_LOTTERY_REMAIN_COUNT = 200; // 外部彩种每个彩种保留数量
	private final int SELF_LOTTERY_REMAIN_COUNT = 3000; // 自主彩种每个彩种保留数量
	private final int JSMMC_LOTTERY_REMAIN_COUNT = 100; // 急速秒秒彩每个用户保留
	private final int GAME_BETS_REMAIN_MONTH = 1; // 第三方游戏投注数据保留多少个月


	/**
	 * 备份彩票投注记录保留1个月,备份3个月..
	 */
	private void backupUserBets() {

		String realRemainTime = new Moment().subtract(1, "months").toSimpleTime();
		String backupRemainTime = new Moment().subtract(3, "months").toSimpleTime();
		String realWhere = "b.time < '" + realRemainTime + "' AND b.status NOT IN (0)";
		String backupWhere = "b.time < '" + backupRemainTime + "'";

		// 删除试玩用户保留1天，不备份
		String demRealRemainTime = new Moment().subtract(1, "days").toSimpleTime();
		String demorealWhere = "b.time < '" + demRealRemainTime + "' AND b.status NOT IN (0)";
		delFictitiousUserBackupBase("虚拟用户彩票投注记录", realWhere, realWhere, "user_bets");
		delDemoUserBackupBase("试玩用户彩票投注记录", demorealWhere, demorealWhere, "user_bets");
		// 备份彩票投注记录保留1个月,备份3个月..
		commonUserBackupBase("彩票投注记录", realWhere, backupWhere, "user_bets");

	}

	/**
	 * 备份账单记录保留1个月,备份3个月
	 */
	private void backupUserBill() {
		commonNoDemoBackupByMonth("账单记录", 1, 3, "user_bill");
	}

	/**
	 * 删除试玩用户原始投注订单,保留1天
	 */
	private void deleteDemoUserBetsOriginal() {
		logger.info("删除试玩用户原始投注订单...");
		String time = new Moment().subtract(1, "days").toSimpleTime(); // 保留1天原始投注

		// 删除备份数据
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			String deleteSql = "DELETE FROM `" + Database.name
					+ "`.`user_bets_original`  WHERE user_id in (select id from `" + Database.name
					+ "`.`user` where nickname = '试玩用户' ) and  `time` < '" + time + "'";

			SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
			int deleteCount = deleteQuery.executeUpdate();
			if (deleteCount > 0) {
				logger.info("删除原始投注数据完成，共删除" + deleteCount + "条数据。");
				session.getTransaction().commit();
			}
		} catch (Exception e) {
			logger.error("删除原始投注数据出错...回滚");
			session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 删除原始投注订单,保留31天
	 */
	private void deleteUserBetsOriginal() {
		logger.info("开始删除原始投注数据...");
		String time = new Moment().subtract(31, "days").toSimpleTime(); // 保留31天原始投注
		String oldDbTab = "`" + Database.name + "`.`user_bets_original`"; // 旧的数据库与表名

		// 删除备份数据
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			String deleteSql = "DELETE FROM `" + Database.name + "`.`user_bets_original` WHERE `time` < '" + time + "'";

			SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
			int deleteCount = deleteQuery.executeUpdate();
			if (deleteCount > 0) {
				logger.info("删除原始投注数据完成，共删除" + deleteCount + "条数据。");
				session.getTransaction().commit();
			}
		} catch (Exception e) {
			logger.error("删除原始投注数据出错...回滚");
			session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 删除系统消息,保留7天
	 */
	private void deleteSysMessage() {
		logger.info("开始删除无用通知数据...");
		String time = new Moment().subtract(7, "days").toSimpleTime();
		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			String deleteSql = "DELETE FROM `" + Database.name + "`.`user_sys_message` WHERE `time` < '" + time + "'";
			SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
			int deleteCount = deleteQuery.executeUpdate();
			if (deleteCount > 0) {
				logger.info("共删除" + deleteCount + "条无用系统通知数据。");
				session.getTransaction().commit();
			} else {
				logger.info("无可用通知数据删除");
			}
		} catch (Exception e) {
			logger.info("删除系统通知出错...回滚");
			session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 备份用户消息保留3个月, 备份12个月
	 */
	private void backupUserMessage() {
		commonBackupByMonth("用户消息", 3, 12, "user_message");
	}

	private void deleteOpenCode() {
		logger.info("开始删除开奖号码数据...");
		List<Lottery> lotteries = lotteryDao.listAll();
		for (Lottery lottery : lotteries) {
			if (lottery.getId() == 117) {
				delOpenCodeByJSMMC(lottery);
			} else {
				int remainCount = lottery.getSelf() == 1 ? SELF_LOTTERY_REMAIN_COUNT : OUT_LOTTERY_REMAIN_COUNT;
				if (lottery.getId() == 127) {
					remainCount = 400;
				}
				delOpenCodeByLottery(lottery, null, remainCount);
			}
		}
	}

	/**
	 * 删除开奖号码数据
	 */
	private void delOpenCodeByLottery(Lottery lottery, Integer userId, int remainCount) {
		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			String oldDbTab = "`" + Database.name + "`.`lottery_open_code`"; // 旧的数据库与表名

			String countSql = "SELECT COUNT(`id`) FROM " + oldDbTab + " WHERE `lottery` = '" + lottery.getShortName()
					+ "'";
			if (userId != null) {
				countSql += " AND `user_id` = " + userId;
			}
			SQLQuery countQuery = session.createSQLQuery(countSql);
			Object countResult = countQuery.uniqueResult();
			Integer count = ObjectUtil.toInt(countResult);
			if (count == null || count <= remainCount) {
				return;
			}

			String selectSql = "SELECT MIN(t1.`id`) FROM (SELECT `id` FROM " + oldDbTab + " WHERE `lottery` = '"
					+ lottery.getShortName() + "'";
			if (userId != null) {
				selectSql += " AND `user_id` = " + userId;
			}
			selectSql += " ORDER BY `id` DESC LIMIT 0, " + remainCount + ") t1";

			SQLQuery selectQuery = session.createSQLQuery(selectSql);
			Object selectResult = selectQuery.uniqueResult();
			Integer targetId = ObjectUtil.toInt(selectResult);
			if (targetId != null && targetId > 0) {
				String deleteSql = "DELETE FROM " + oldDbTab + " WHERE `id` < " + targetId + " AND `lottery` = '"
						+ lottery.getShortName() + "'";
				if (userId != null) {
					deleteSql += " AND `user_id` = " + userId;
				}
				SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
				int deleteCount = deleteQuery.executeUpdate();
				if (deleteCount > 0) {
					logger.info("删除{}开奖号码数据完成，共删除" + deleteCount + "条数据。", lottery.getShortName());
					session.getTransaction().commit();
				} else {
					logger.info("{}无可用数据删除", lottery.getShortName());
				}
			}
		} catch (Exception e) {
			logger.error("删除{}开奖号码数据完成...回滚", lottery.getShortName());
			session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 删除急速秒秒彩开奖号码数据，并且只保留一个月的数据
	 */
	private void delOpenCodeByJSMMC(Lottery lottery) {
		if (lottery.getId() != 117) {
			return;
		}

		// 查询购买过的用户ID
		List<Integer> userIds = getJSMMCUserIds();
		if (CollectionUtils.isEmpty(userIds)) {
			return;
		}
		for (Integer userId : userIds) {
			delOpenCodeByLottery(lottery, userId, JSMMC_LOTTERY_REMAIN_COUNT);
		}

		// 删除一个月前的数据
		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			String oldDbTab = "`" + Database.name + "`.`lottery_open_code`"; // 旧的数据库与表名

			String deleteSql = "DELETE FROM " + oldDbTab + " WHERE `lottery` = 'jsmmc' AND `time` < '"
					+ new Moment().subtract(30, "days").toSimpleTime() + "'";
			SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
			int deleteCount = deleteQuery.executeUpdate();
			if (deleteCount > 0) {
				logger.info("删除急速秒秒彩一个月前数据完成，共删除" + deleteCount + "条数据。");
				session.getTransaction().commit();
			} else {
				logger.info("{}无可用数据删除", lottery.getShortName());
			}
		} catch (Exception e) {
			logger.error("删除急速秒秒彩一个月前数据完成...回滚");
			session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	private List<Integer> getJSMMCUserIds() {
		List<Integer> userIds = new ArrayList<>();
		Session session = sessionFactory.openSession();
		try {
			SQLQuery selectQuery = session.createSQLQuery(
					"SELECT DISTINCT user_id FROM lottery_open_code WHERE user_id IS NOT NULL AND lottery = 'jsmmc'");
			List list = selectQuery.list();
			for (Object object : list) {
				userIds.add(Integer.valueOf(object.toString()));
			}
		} catch (Exception e) {
			logger.error("转移开奖号码出错", e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return userIds;
	}

	/**
	 * 备份后台操作日志保留6个月, 备份12个月
	 */
	private void backupAdminUserLog() {
		commonBackupByMonth("后台操作日志", 6, 12, "admin_user_log");
	}

	/**
	 * 备份后台详细日志保留6个月, 备份12个月
	 */
	private void backupAdminActionLog() {
		commonBackupByMonth("后台详细日志", 6, 12, "admin_user_action_log");
	}

	/**
	 * 备份前台用户登录日志保留12个月, 备份26个月
	 */
	private void backupUserLoginLog() {
		commonBackupByMonth("前台用户登录日志", 12, 36, "user_login_log");
	}

	/**
	 * 备份前台用户操作日志保留12个月, 备份36个月
	 */
	private void backupUserActionLog() {
		commonBackupByMonth("前台用户操作日志", 12, 36, "user_action_log");
	}

	/**
	 * 备份用户第三方游戏投注记录保留1个月, 备份3个月
	 */
	private void backupGameBets() {
		commonBackupByMonth("用户第三方游戏投注记录", 3, 3, "game_bets");
	}

	/**
	 * 删除大额中奖记录，保留一个月
	 */
	private void deleteUserHighPrize() {
		String name = "大额中奖记录";

		String time = new Moment().subtract(1, "months").toSimpleTime();
		logger.info("开始删除{}...", name);

		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();

			String deleteSql = "DELETE FROM `" + Database.name + "`.`user_high_prize` WHERE `time` < '" + time + "'";
			SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
			int deleteCount = deleteQuery.executeUpdate();
			if (deleteCount > 0) {
				logger.info("删除{}，共删除{}条数据。", name, deleteCount);
				session.getTransaction().commit();
			} else {
				logger.info("无可用删除", name);
			}
		} catch (Exception e) {
			logger.error("删除" + name + "记录出错...回滚");
			session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
	/**
	 * 删除大额中奖记录，保留1天
	 */
	private void deleteDemoUserHighPrize() {
		String name = "大额中奖记录";

		String time = new Moment().subtract(1, "days").toSimpleTime();
		logger.info("开始删除{}...", name);

		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			String deleteSql = "DELETE FROM `" + Database.name + "`.`user_high_prize` WHERE user_id in (select id from `" + Database.name + "`.`user` where nickname = '试玩用户') and  `time` < '" + time + "'";
			SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
			int deleteCount = deleteQuery.executeUpdate();
			if (deleteCount > 0) {
				logger.info("删除{}，共删除{}条数据。", name, deleteCount);
				session.getTransaction().commit();
			} else {
				logger.info("无可用删除", name);
			}
		} catch (Exception e) {
			logger.error("删除" + name + "记录出错...回滚");
			session.getTransaction().rollback();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
	

	/**
	 * 备份未支付的充值记录保留7天
	 */
	private void backupUserRechargeUnPaid() {
		String realRemainTime = new Moment().subtract(7, "days").toSimpleTime();
		String realWhere = "`time` < '" + realRemainTime + "' AND `status`=0";
		String backupWhere = null;

		commonBackupBase("用户未支付充值记录", realWhere, backupWhere, "user_recharge");
	}

	/**
	 * 备份充值记录保留3个月, 备份12个月
	 */
	private void backupUserRecharge() {
		commonBackupByMonth("用户充值记录", 3, 12, "user_recharge");
	}

	/**
	 * 备份取现记录保留3个月, 备份12个月
	 */
	private void backupUserWithdraw() {
		commonBackupByMonth("用户取现记录", 3, 12, "user_withdraw");
	}

	/**
	 * 备份转账记录保留1个月, 备份12个月
	 */
	private void backupUserTransfer() {
		commonBackupByMonth("用户转账记录", 1, 12, "user_transfers");
	}

	/**
	 * 备份红包雨账单保留1个月, 备份12个月
	 */
	private void backupRedPacketRainBill() {
		commonBackupByMonth("红包雨账单", 1, 12, "activity_red_packet_rain_bill");
	}

	/**
	 * 备份红包雨时间保留1个月, 备份12个月
	 */
	private void backupRedPacketRainTime() {
		commonBackupByMonth("红包雨时间配置", 1, 12, "activity_red_packet_rain_time", "end_time");
	}

	/**
	 * 备份日结账单保留2个月,备份12个月
	 */
	private void backupDailySettleBill() {
		commonBackupByMonth("彩票日结账单", 2, 12, "user_daily_settle_bill", "settle_time");
	}

	/**
	 * 备份分红账单保留2个月, 备份12个月
	 */
	private void backupDividendBill() {
		commonBackupByMonth("彩票分红账单", 2, 12, "user_dividend_bill", "settle_time");
	}

	/**
	 * 备份老虎机真人体育返水保留2个月, 备份12个月
	 */
	private void backupGameWaterBill() {
		commonBackupByMonth("老虎机真人体育返水账单", 2, 12, "user_game_water_bill", "settle_time");
	}

	/**
	 * 备份老虎机真人体育分红保留2个月, 备份12个月
	 */
	private void backupGameDividendBill() {
		commonBackupByMonth("老虎机真人体育分红账单", 2, 12, "user_game_dividend_bill", "settle_time");
	}

	/**
	 * 备份主账户报表保留3个月,备份12个月
	 */
	private void backupUserMainReport() {
		commonBackupByMonth("主账户报表", 2, 12, "user_main_report");
	}

	/**
	 * 备份彩票报表保留2个月,备份12个月
	 */
	private void backupUserLotteryReport() {
		commonBackupByMonth("彩票报表", 2, 12, "user_lottery_report");
	}

	/**
	 * 备份彩票详情报表保留2个月,备份12个月
	 */
	private void backupUserLotteryDetailsReport() {
		commonBackupByMonth("彩票详情报表", 2, 12, "user_lottery_details_report");
	}

	/**
	 * 备份游戏报表保留2个月,备份12个月
	 */
	private void backupUserGameReport() {
		commonBackupByMonth("老虎机真人体育报表", 2, 12, "user_game_report");
	}

	private void commonNoDemoBackupByMonth(String name, int realRemainMonth, int backupRemainMonth, String table) {
		String realRemainTime = new Moment().subtract(realRemainMonth, "months").toSimpleDate();
		String backupRemainTime = new Moment().subtract(backupRemainMonth, "months").toSimpleDate();
		// 试玩保留1天，不备份
		String demRealRemainTime = new Moment().subtract(1, "days").toSimpleTime();
		String demorealWhere = "b.time < '" + demRealRemainTime + "' ";
		delDemoUserBackupBase(name, demorealWhere, null, table);
		delFictitiousUserBackupBase(name, realRemainTime, null, table);
		// 备份账单记录保留1个月,备份3个月
		commonNoDemoBackupByTime(name, realRemainTime, backupRemainTime, table, "b.time");

	}

	private void commonBackupByMonth(String name, int realRemainMonth, int backupRemainMonth, String table) {
		String realRemainTime = new Moment().subtract(realRemainMonth, "months").toSimpleDate();
		String backupRemainTime = new Moment().subtract(backupRemainMonth, "months").toSimpleDate();

		commonBackupByTime(name, realRemainTime, backupRemainTime, table, "time");
	}

	private void commonBackupByMonth(String name, int realRemainMonth, int backupRemainMonth, String table,
			String timeField) {
		String realRemainTime = new Moment().subtract(realRemainMonth, "months").toSimpleDate();
		String backupRemainTime = new Moment().subtract(backupRemainMonth, "months").toSimpleDate();

		commonBackupByTime(name, realRemainTime, backupRemainTime, table, timeField);
	}

	private void commonNoDemoBackupByTime(String name, String realRemainTime, String backupRemainTime, String table,
			String timeField) {
		String realWhere = "" + timeField + " < '" + realRemainTime + "'";
		String backupWhere = "" + timeField + " < '" + backupRemainTime + "'";
		commonUserBackupBase(name, realWhere, backupWhere, table);
	}

	private void commonBackupByTime(String name, String realRemainTime, String backupRemainTime, String table,
			String timeField) {
		String realWhere = "`" + timeField + "` < '" + realRemainTime + "'";
		String backupWhere = "`" + timeField + "` < '" + backupRemainTime + "'";

		commonBackupBase(name, realWhere, backupWhere, table);
	}

	private void commonBackupBase(String name, String realWhere, String backupWhere, String table) {
		boolean migrateDone = false;
		String oldDbTab = "`" + Database.name + "`.`" + table + "`"; // 旧的数据库与表名
		String newDbTab = "`" + Database.backup + "`.`" + table + "`"; // 新的数据库与表名

		// 备份账单数据
		logger.info("开始迁移{}前的{}数据", realWhere, name);
		do {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			try {
				String idSql = "SELECT `id` FROM " + oldDbTab + " WHERE " + realWhere + " ORDER BY `id` DESC LIMIT 0,"
						+ LIMIT_UNIT_COUNT;
				SQLQuery idQuery = session.createSQLQuery(idSql);
				Object idResult = idQuery.list();
				if (idResult == null) {
					migrateDone = true;
					logger.info("没有可以迁移的{}数据。", name);
					return;
				}

				ArrayList<Integer> ids = (ArrayList<Integer>) idResult;
				if (ids == null || ids.size() <= 0) {
					migrateDone = true;
					logger.info("没有可以迁移的数据。", name);
					return;
				}

				String insertSql = "INSERT INTO " + newDbTab + " (SELECT * FROM " + oldDbTab + " WHERE `id` IN ("
						+ javautils.array.ArrayUtils.toString(ids) + "))";
				String deleteSql = "DELETE FROM " + oldDbTab + " WHERE `id` IN ("
						+ javautils.array.ArrayUtils.toString(ids) + ")";

				SQLQuery insertQuery = session.createSQLQuery(insertSql);
				int insertCount = insertQuery.executeUpdate();
				if (insertCount > 0) {
					logger.info("迁移{}数据{}条数据。", name, insertCount);
					SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
					int deleteCount = deleteQuery.executeUpdate();
					if (deleteCount > 0) {
						session.getTransaction().commit();
					}
				} else {
					migrateDone = true;
					logger.info("没有可以迁移的{}数据。", name);
				}
			} catch (Exception e) {
				logger.error("迁移" + name + "数据出错...回滚");
				session.getTransaction().rollback();
				migrateDone = true;
			} finally {
				if (session != null && session.isOpen()) {
					session.close();
				}
			}
		} while (!migrateDone);

		// 删除备份数据
		if (StringUtil.isNotNull(backupWhere)) {
			logger.info("开始删除{}前的{}备份数据", backupWhere, name);
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			try {
				String deleteSql = "DELETE FROM " + newDbTab + " WHERE " + backupWhere;

				SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
				int deleteCount = deleteQuery.executeUpdate();
				if (deleteCount > 0) {
					logger.info("删除{}备份数据完成，共删除{}条数据。", name, deleteCount);
					session.getTransaction().commit();
				} else {
					logger.info("没有可用的{}备份数据删除，本次未删除任何备份数据。", name, deleteCount);
				}
			} catch (Exception e) {
				logger.error("删除" + name + "备份数据出错...回滚");
				session.getTransaction().rollback();
			} finally {
				if (session != null && session.isOpen()) {
					session.close();
				}
			}
		}
	}

	
	private void delFictitiousUserBackupBase(String name, String realWhere, String backupWhere, String table) {
		boolean migrateDone = false;
		String oldDbTab = "`" + Database.name + "`.`" + table + "`"; // 旧的数据库与表名
		String userTab = "user";
		String userDbTab = "`" + Database.name + "`.`" + userTab + "`"; // 旧的数据库与表名

		// 备份账单数据
		logger.info("开始删除虚拟用户{}前的{}数据", realWhere, name);
		do {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			try {
				String idSql = "SELECT b.id FROM " + oldDbTab + " b,  " + userDbTab
						+ " u WHERE b.user_id = u.id  and u.type = 4  and " + realWhere
						+ " ORDER BY b.id DESC LIMIT 0," + LIMIT_UNIT_COUNT;
				SQLQuery idQuery = session.createSQLQuery(idSql);
				Object idResult = idQuery.list();
				if (idResult == null) {
					migrateDone = true;
					logger.info("没有可以删除的{}数据。", name);
					return;
				}

				ArrayList<Integer> ids = (ArrayList<Integer>) idResult;
				if (ids == null || ids.size() <= 0) {
					migrateDone = true;
					logger.info("没有可以删除的数据。", name);
					return;
				}

				String deleteSql = "DELETE FROM " + oldDbTab + " WHERE `id` IN ("
						+ javautils.array.ArrayUtils.toString(ids) + ")";

				SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
				int deleteCount = deleteQuery.executeUpdate();
				if (deleteCount > 0) {
					session.getTransaction().commit();
				}
			} catch (Exception e) {
				logger.error("删除试玩用户" + name + "数据出错...回滚");
				session.getTransaction().rollback();
				migrateDone = true;
			} finally {
				if (session != null && session.isOpen()) {
					session.close();
				}
			}
		} while (!migrateDone);
	}
	
	private void delDemoUserBackupBase(String name, String realWhere, String backupWhere, String table) {
		boolean migrateDone = false;
		String oldDbTab = "`" + Database.name + "`.`" + table + "`"; // 旧的数据库与表名
		String userTab = "user";
		String userDbTab = "`" + Database.name + "`.`" + userTab + "`"; // 旧的数据库与表名

		// 备份账单数据
		logger.info("开始删除试玩用户{}前的{}数据", realWhere, name);
		do {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			try {
				String idSql = "SELECT b.id FROM " + oldDbTab + " b,  " + userDbTab
						+ " u WHERE b.user_id = u.id  and u.nickname  = '试玩用户' and " + realWhere
						+ " ORDER BY b.id DESC LIMIT 0," + LIMIT_UNIT_COUNT;
				SQLQuery idQuery = session.createSQLQuery(idSql);
				Object idResult = idQuery.list();
				if (idResult == null) {
					migrateDone = true;
					logger.info("没有可以删除的{}数据。", name);
					return;
				}

				ArrayList<Integer> ids = (ArrayList<Integer>) idResult;
				if (ids == null || ids.size() <= 0) {
					migrateDone = true;
					logger.info("没有可以删除的数据。", name);
					return;
				}

				String deleteSql = "DELETE FROM " + oldDbTab + " WHERE `id` IN ("
						+ javautils.array.ArrayUtils.toString(ids) + ")";

				SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
				int deleteCount = deleteQuery.executeUpdate();
				if (deleteCount > 0) {
					session.getTransaction().commit();
				}
			} catch (Exception e) {
				logger.error("删除试玩用户" + name + "数据出错...回滚");
				session.getTransaction().rollback();
				migrateDone = true;
			} finally {
				if (session != null && session.isOpen()) {
					session.close();
				}
			}
		} while (!migrateDone);
	}

	private void commonUserBackupBase(String name, String realWhere, String backupWhere, String table) {
		boolean migrateDone = false;
		String oldDbTab = "`" + Database.name + "`.`" + table + "`"; // 旧的数据库与表名
		String userTab = "user";
		String userDbTab = "`" + Database.name + "`.`" + userTab + "`"; // 旧的数据库与表名

		String newDbTab = "`" + Database.backup + "`.`" + table + "`"; // 新的数据库与表名

		// 备份账单数据
		logger.info("开始迁移{}前的{}数据", realWhere, name);
		do {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			try {
				String idSql = "SELECT b.id FROM " + oldDbTab + " b,  " + userDbTab
						+ " u WHERE b.user_id = u.id  and u.upid != 0  and " + realWhere
						+ " ORDER BY b.id DESC LIMIT 0," + LIMIT_UNIT_COUNT;
				SQLQuery idQuery = session.createSQLQuery(idSql);
				Object idResult = idQuery.list();
				if (idResult == null) {
					migrateDone = true;
					logger.info("没有可以迁移的{}数据。", name);
					return;
				}

				ArrayList<Integer> ids = (ArrayList<Integer>) idResult;
				if (ids == null || ids.size() <= 0) {
					migrateDone = true;
					logger.info("没有可以迁移的数据。", name);
					return;
				}

				String insertSql = "INSERT INTO " + newDbTab + " (SELECT * FROM " + oldDbTab + " WHERE `id` IN ("
						+ javautils.array.ArrayUtils.toString(ids) + "))";

				String deleteSql = "DELETE FROM " + oldDbTab + " WHERE `id` IN ("
						+ javautils.array.ArrayUtils.toString(ids) + ")";

				SQLQuery insertQuery = session.createSQLQuery(insertSql);
				int insertCount = insertQuery.executeUpdate();
				if (insertCount > 0) {
					logger.info("迁移{}数据{}条数据。", name, insertCount);
					SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
					int deleteCount = deleteQuery.executeUpdate();
					if (deleteCount > 0) {
						session.getTransaction().commit();
					}
				} else {
					migrateDone = true;
					logger.info("没有可以迁移的{}数据。", name);
				}
			} catch (Exception e) {
				logger.error("迁移" + name + "数据出错...回滚");
				session.getTransaction().rollback();
				migrateDone = true;
			} finally {
				if (session != null && session.isOpen()) {
					session.close();
				}
			}
		} while (!migrateDone);

		// 删除备份数据
		if (StringUtil.isNotNull(backupWhere)) {
			logger.info("开始删除{}前的{}备份数据", backupWhere, name);
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			try {
				String deleteSql = "DELETE FROM " + newDbTab + " WHERE " + backupWhere;

				SQLQuery deleteQuery = session.createSQLQuery(deleteSql);
				int deleteCount = deleteQuery.executeUpdate();
				if (deleteCount > 0) {
					logger.info("删除{}备份数据完成，共删除{}条数据。", name, deleteCount);
					session.getTransaction().commit();
				} else {
					logger.info("没有可用的{}备份数据删除，本次未删除任何备份数据。", name, deleteCount);
				}
			} catch (Exception e) {
				logger.error("删除" + name + "备份数据出错...回滚");
				session.getTransaction().rollback();
			} finally {
				if (session != null && session.isOpen()) {
					session.close();
				}
			}
		}
	}
}