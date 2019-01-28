package lottery.domains.content.biz.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityPacketService;
import lottery.domains.content.dao.ActivityPacketBillDao;
import lottery.domains.content.dao.ActivityPacketInfoDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.ActivityPacketBill;
import lottery.domains.content.entity.ActivityPacketInfo;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.activity.ActivityPacketVO;
import lottery.domains.pool.LotteryDataFactory;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityPacketServiceImpl implements ActivityPacketService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ActivityPacketBillDao activityPacketBillDao;
	
	@Autowired
	private ActivityPacketInfoDao activityPacketInfoDao;
	
//	@Autowired
//	private UserLotteryReportDao uLotteryReportDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public PageList searchBill(String username, String date, int start, int limit) {
		if(start < 0) start = 0;
		if(limit < 0) limit = 10;
		if(limit > 100) limit = 100;
		List<Criterion> criterions = new ArrayList<Criterion>();
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("id"));
		criterions.add(Restrictions.eq("status", 1));
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User uBean = userDao.getByUsername(username);
			if(uBean != null) {
				criterions.add(Restrictions.eq("userId", uBean.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		if(isSearch) {
			PageList pList = activityPacketBillDao.find(criterions, orders, start, limit);
			List<ActivityPacketVO> list = new ArrayList<>();
			for (Object o : pList.getList()) {
				list.add(new ActivityPacketVO((ActivityPacketBill) o, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public PageList searchPacketInfo(String username, String date, String type, int start, int limit) {
		if(start < 0) start = 0;
		if(limit < 0) limit = 10;
		if(limit > 100) limit = 100;
		List<Criterion> criterions = new ArrayList<Criterion>();
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("id"));
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User uBean = userDao.getByUsername(username);
			if(uBean != null) {
				criterions.add(Restrictions.eq("userId", uBean.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		if(StringUtil.isNotNull(type)) {
			criterions.add(Restrictions.eq("type", Integer.parseInt(type)));
		}
		if(isSearch) {
			PageList pList = activityPacketInfoDao.find(criterions, orders, start, limit);
			List<ActivityPacketVO> list = new ArrayList<>();
			for (Object o : pList.getList()) {
				list.add(new ActivityPacketVO((ActivityPacketInfo) o, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public boolean generatePackets(int count, double amount) {
		ActivityPacketInfo packetInfo = generatePacketInfo(count, amount);
		int packetInfoId = packetInfo.getId();
		
		List<ActivityPacketBill> packets = new ArrayList<ActivityPacketBill>();
		splitPacket(count, amount, packetInfoId, packets);
		for (ActivityPacketBill activityPacketBill : packets) {
			activityPacketBillDao.save(activityPacketBill);
		}
		
		return true;
	}

	private ActivityPacketInfo generatePacketInfo(int count, double amount) {
		ActivityPacketInfo packetInfo = new ActivityPacketInfo();
		packetInfo.setAmount(amount);
		packetInfo.setCount(count);
		packetInfo.setStatus(ActivityPacketInfo.PacketStatus.AVALIABLE.get());
		packetInfo.setTime(DateUtil.getCurrentTime());
		packetInfo.setUserId(-1);
		packetInfo.setType(ActivityPacketInfo.PacketType.SYSTEM_PACKET.get());
		activityPacketInfoDao.save(packetInfo);
		return packetInfo;
	}

	private void splitPacket(int count, double amount, int packetInfoId, List<ActivityPacketBill> packets) {
		double seed = amount/count;
		double start = 0;
		DecimalFormat df = new DecimalFormat("0.00");
		Random random = new Random();
		for (int i = 0; i < count; i++) {
			 double d = random.nextDouble();
			 if(d == 0.0){
				 d = 0.1;
			 }
			 d = d * seed;
			 double money = Double.parseDouble(df.format(d));
			 start += money;
			 
			 ActivityPacketBill packet = new ActivityPacketBill();
			 packet.setAmount(money);
			 packet.setPacketId(packetInfoId);
			 packet.setStatus(ActivityPacketInfo.PacketStatus.AVALIABLE.get());
			 packets.add(packet);
		}
		double surplus = Double.parseDouble(df.format((amount - start)/count));//还剩余多少钱
		
		start = 0;
		for (ActivityPacketBill activityPacketBill : packets) {
			double newAmount = activityPacketBill.getAmount() + surplus;
			activityPacketBill.setAmount(newAmount);
			start += newAmount;
		}
		
		ActivityPacketBill firstBill = packets.get(0);
		if(amount - start != 0){
			firstBill.setAmount(firstBill.getAmount() + (amount - start));
		}
	}

	@Override
	public List<Map<Integer, Double>> statTotal() {
		return activityPacketInfoDao.statTotal();
	}

}