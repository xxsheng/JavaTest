// package activity.domains.content.biz.impl;
//
// import java.text.DecimalFormat;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;
//
// import javautils.StringUtil;
// import javautils.date.DateUtil;
// import javautils.date.Moment;
// import lottery.domains.content.biz.UserBillService;
// import lottery.domains.content.dao.ActivityPacketBillWriteDao;
// import lottery.domains.content.dao.ActivityPacketInfoDao;
// import lottery.domains.content.dao.UserDao;
// import lottery.domains.content.dao.UserLotteryReportDao;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.entity.UserLotteryReport;
// import lottery.domains.content.global.Global;
// import net.sf.json.JSONArray;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;
//
// import activity.domains.content.biz.ActivityPacketService;
// import activity.domains.content.dao.write.ActivityRebateWriteDao;
// import activity.domains.content.entity.ActivityPacketBill;
// import activity.domains.content.entity.ActivityPacketInfo;
// import activity.domains.content.entity.ActivityRebate;
// import activity.domains.content.entity.activity.RebateRulesPacket;
//
// @Service
// public class ActivityPacketServiceImpl implements ActivityPacketService {
//
// 	@Autowired
// 	private UserDao userDao;
//
// 	@Autowired
// 	private ActivityPacketBillWriteDao activityPacketBillDao;
//
// 	@Autowired
// 	private ActivityPacketInfoDao activityPacketInfoDao;
//
// 	@Autowired
// 	private UserLotteryReportDao uLotteryReportDao;
//
// 	@Autowired
// 	private ActivityRebateWriteDao aRebateDao;
//
// 	@Autowired
// 	private UserBillService uBillService;
//
// 	@Autowired
//     private RedisTemplate<String, Object> redisTemplate;
//
// 	private static Logger logger = LoggerFactory.getLogger(ActivityPacketServiceImpl.class);
//
// 	@SuppressWarnings("unchecked")
// 	List<RebateRulesPacket> listRules(String rules) {
// 		return (List<RebateRulesPacket>) JSONArray.toCollection(
// 				JSONArray.fromObject(rules), RebateRulesPacket.class);
// 	}
//
// 	double getTotalCost(String startTime, String endTime, int userId) {
// 		String sDate = new Moment().fromTime(startTime).toSimpleDate();
// 		String eDate = new Moment().fromTime(endTime).add(1, "days").toSimpleDate();
// 		int[] ids = { userId };
// 		List<UserLotteryReport> lotteryDayList = uLotteryReportDao.getDayList(ids, sDate, eDate);
// 		double totalCost = 0;
// 		for (UserLotteryReport tmpBean : lotteryDayList) {
// 			totalCost += tmpBean.getBillingOrder();
// 		}
// 		return totalCost;
// 	}
//
// 	private ActivityPacketInfo generatePacketInfo(int count, double amount, int userId) {
// 		ActivityPacketInfo packetInfo = new ActivityPacketInfo();
// 		packetInfo.setAmount(amount);
// 		packetInfo.setCount(count);
// 		packetInfo.setStatus(ActivityPacketInfo.PacketStatus.AVALIABLE.get());
// 		packetInfo.setTime(DateUtil.getCurrentTime());
// 		packetInfo.setUserId(userId);
// 		packetInfo.setType(ActivityPacketInfo.PacketType.USER_PACKET.get());
// 		activityPacketInfoDao.save(packetInfo);
// 		return packetInfo;
// 	}
//
// 	private void splitPacket(int count, double amount, int packetInfoId, List<ActivityPacketBill> packets) {
// 		double seed = amount/count;
// 		double start = 0;
// 		DecimalFormat df = new DecimalFormat("0.00");
// 		Random random = new Random();
// 		for (int i = 0; i < count; i++) {
// 			 double d = random.nextDouble();
// 			 if(d == 0.0){
// 				 d = 0.1;
// 			 }
// 			 d = d * seed;
// 			 double money = Double.parseDouble(df.format(d));
// 			 start += money;
//
// 			 ActivityPacketBill packet = new ActivityPacketBill();
// 			 packet.setAmount(money);
// 			 packet.setPacketId(packetInfoId);
// 			 packet.setStatus(ActivityPacketInfo.PacketStatus.AVALIABLE.get());
// 			 packets.add(packet);
// 		}
// 		double surplus = Double.parseDouble(df.format((amount - start)/count));//还剩余多少钱
//
// 		start = 0;
// 		for (ActivityPacketBill activityPacketBill : packets) {
// 			double newAmount = activityPacketBill.getAmount() + surplus;
// 			activityPacketBill.setAmount(newAmount);
// 			start += newAmount;
// 		}
//
// 		ActivityPacketBill firstBill = packets.get(0);
// 		if(amount - start != 0){
// 			firstBill.setAmount(firstBill.getAmount() + (amount - start));
// 		}
// 	}
//
// 	@Override
// 	public List<ActivityPacketInfo> getAvaliablePackets(int userId, String ip) {
// 		List<ActivityPacketInfo> list = activityPacketInfoDao.find(ActivityPacketInfo.PacketStatus.AVALIABLE.get(), userId, ip);
// 		return list;
// 	}
//
//
// 	/**
// 	 * <p>1.为解决并发问题 每次获取红包前先判断状态是否可用</p>
// 	 * <p>2.获取红包后更新PacketInfo状态</p>
// 	 * <p>3.返回null提示用户已经抢光并且更新数据 否则返回正常红包</p>
// 	 * <p>4.更新用户余额，添加用户账单</p>
// 	 
// 	@Override
// 	public ActivityPacketBill doGrabPacket(ActivityPacketInfo packetInfo, int userId, String ip) {
// 		if(packetInfo.getStatus() == ActivityPacketInfo.PacketStatus.FINISH.get()){
// 			return null;
// 		}
// 		logger.info("获得红包:{}", packetInfo.getId());
//
// 		List<ActivityPacketBill> packets = packetInfo.getPackets(activityPacketBillDao, redisTemplate, true);
// 		ActivityPacketBill getPacket = null;
//
// 		for (ActivityPacketBill activityPacketBill : packets) {
// 			if(activityPacketBill.getStatus() == ActivityPacketInfo.PacketStatus.AVALIABLE.get()){
// 				getPacket = activityPacketBill;
// 				activityPacketBill.setStatus(ActivityPacketInfo.PacketStatus.FINISH.get());
// 				break;
// 			}
// 		}
//
// 		if(getPacket != null){
// 			logger.info("抢红包成功，获得红包:{} 下一份:{} 金额:{}", packetInfo.getId(), getPacket.getId(), getPacket.getAmount());
// 			//1. 更新被抢红包状态
// 			updatePacketBill(userId, getTodayCost(userId), ip, getPacket);
// 			//2. 更新用余额，新增用户账单
// 			updateBalanceAndBill(userId, getPacket, packetInfo.getType());
// 			//3. 更新红包缓存
// 			packetInfo.refreshCache(redisTemplate, packets);
// 			//4. 检查红包是否被抢光，如果被抢完则删除缓存 更新DB 状态
// 			checkPacketInfoStatus(packetInfo, packets, userId);
// 		}
//
// 		return getPacket;
// 	}
//
// 	private void checkPacketInfoStatus(ActivityPacketInfo packetInfo, List<ActivityPacketBill> packets, int userId) {
// 		boolean finish = true;
// 		for (ActivityPacketBill activityPacketBill : packets) {
// 			if(activityPacketBill.getStatus() == ActivityPacketInfo.PacketStatus.AVALIABLE.get()){
// 				finish = false;
// 			}
// 		}
// 		if(finish){
// 			packetInfo.deleteCache(redisTemplate);
// 			packetInfo.setStatus(ActivityPacketInfo.PacketStatus.FINISH.get());
// 			activityPacketInfoDao.update(packetInfo);
// 			logger.info("红包:{} 已经被抢完，删除缓存，更新DB...", packetInfo.getId());
// 		}
// 	}
//
//
// 	//计入用户流水 更新用户金额
// 	private void updateBalanceAndBill(int userId, ActivityPacketBill activityPacketBill, int packetType) {
// 		User uBean = userDao.getById(userId);
// 		boolean uFlag = userDao.updateLotteryMoney(userId, activityPacketBill.getAmount());
// 		String remarks = "抢红包";
// 		Integer refType = 1; // 其他活动类型
// 		if(uFlag) {
// 			uBillService.addPacketBill(uBean, Global.BILL_ACCOUNT_LOTTERY, activityPacketBill.getAmount(), refType, remarks);
// 		}
// 		if(packetType == ActivityPacketInfo.PacketType.SYSTEM_PACKET.get()){//如果是系统红包 则计入优惠活动
// 			uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, activityPacketBill.getAmount(), refType, remarks);
// 		}
// 	}
//
// 	private void updatePacketBill(int userId, double cost, String ip, ActivityPacketBill activityPacketBill) {
// 		activityPacketBill.setStatus(ActivityPacketInfo.PacketStatus.FINISH.get());
// 		activityPacketBill.setUserId(userId);
// 		activityPacketBill.setTime(DateUtil.getCurrentTime());
// 		activityPacketBill.setCost(cost);
// 		activityPacketBill.setIp(ip);
// 		activityPacketBillDao.update(activityPacketBill);
// 	}
//
// 	@Override
// 	public boolean doSendPacket(int count, double amount, int userId) {
// 		ActivityPacketInfo packetInfo = generatePacketInfo(count, amount, userId);
// 		int packetInfoId = packetInfo.getId();
//
// 		List<ActivityPacketBill> packets = new ArrayList<ActivityPacketBill>();
// 		splitPacket(count, amount, packetInfoId, packets);
// 		for (ActivityPacketBill activityPacketBill : packets) {
// 			activityPacketBillDao.save(activityPacketBill);
// 		}
//
// 		//计入用户流水 更新用户金额
// 		User uBean = userDao.getById(userId);
// 		boolean uFlag = userDao.updateLotteryMoney(userId, -amount);
// 		if(uFlag) {
// 			String remarks = "派发红包";
// 			Integer refType = 1; // 其他活动类型
// 			return uBillService.addPacketBill(uBean, Global.BILL_ACCOUNT_LOTTERY, -amount, refType, remarks);
// 		}
// 		return true;
// 	}
//
// 	@Override
// 	public ActivityPacketInfo get(int id) {
// 		return activityPacketInfoDao.get(id);
// 	}
//
// 	@Override
// 	public ActivityPacketInfo validateIp(int packetId, String ip, List<ActivityPacketInfo> list) {
// 		ActivityPacketInfo packetInfo = null;
// 		for (ActivityPacketInfo activityPacketInfo : list) {
// 			if(packetId == activityPacketInfo.getId()){
// 				packetInfo = activityPacketInfo;
// 				break;
// 			}
// 		}
// 		boolean exist = activityPacketBillDao.isExist(ip, packetId);
// 		if(exist){
// 			logger.info("Packetid:{} with ip :{} weather exist:{} !", packetId, ip , exist);
// 			packetInfo = null;
// 		}
// 		return packetInfo;
// 	}
//
// 	public double validateCost(int userId) {
// 		ActivityRebate activity = aRebateDao.getByType(Global.ACTIVITY_REBATE_PACKET);
// 		double minCost = 0;
// 		if(StringUtil.isNotNull(activity.getRules())){
// 			minCost = listRules(activity.getRules()).get(0).getCost();//最低消费额度
// 		}
// 		double todayCost = getTodayCost(userId);
// 		return todayCost >= minCost ? -1 : minCost;
// 	}
//
// 	private double getTodayCost(int userId) {
// 		double todayCost = 0;
// 		String today = new Moment().toSimpleDate();
// 		UserLotteryReport todayReport = uLotteryReportDao.get(userId, today);
// 		if(todayReport != null){
// 			todayCost = todayReport.getBillingOrder();
// 		}
// 		return todayCost;
// 	}
//
// }