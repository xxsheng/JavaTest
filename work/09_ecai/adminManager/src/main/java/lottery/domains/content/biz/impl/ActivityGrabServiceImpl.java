package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityGrabService;
import lottery.domains.content.dao.ActivityGrabBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.ActivityGrabBill;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.activity.ActivityGrabBillVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class ActivityGrabServiceImpl implements ActivityGrabService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ActivityGrabBillDao grabBillDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	private static final String ALL_AMOUNT = "allAmount";
	
	private static final String TODAY_AMOUNT = "todayAmount";
	
	@Override
	public PageList searchBill(String username, String date, int start,
			int limit) {
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
		if(isSearch) {
			PageList pList = grabBillDao.find(criterions, orders, start, limit);
			List<ActivityGrabBillVO> list = new ArrayList<>();
			for (Object o : pList.getList()) {
				list.add(new ActivityGrabBillVO((ActivityGrabBill) o, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public Map<String, Double> getOutTotalInfo() {
		Double allAmount = grabBillDao.getOutAmount(null);
		Double todayAmount = grabBillDao.getOutAmount(DateUtil.getCurrentTime());
		
		Map<String, Double> totalInfo = new HashMap<String, Double>();
		totalInfo.put(ALL_AMOUNT, allAmount);
		totalInfo.put(TODAY_AMOUNT, todayAmount);
		return totalInfo;
	}

}
