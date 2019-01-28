package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.VipUpgradeListService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.VipUpgradeListDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.VipUpgradeList;
import lottery.domains.content.vo.vip.VipUpgradeListVO;
import lottery.domains.pool.LotteryDataFactory;
//import org.springframework.transaction.annotation.Transactional;

@Service
//@Transactional
public class VipUpgradeListServiceImpl implements VipUpgradeListService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private VipUpgradeListDao vUpgradeListDao;
	
	/**
	 * LotteryDataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public PageList search(String username, String month, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(month)) {
			criterions.add(Restrictions.eq("month", month));
		}
 		orders.add(Order.desc("id"));
		if(isSearch) {
			List<VipUpgradeListVO> list = new ArrayList<>();
			PageList pList = vUpgradeListDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new VipUpgradeListVO((VipUpgradeList) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public boolean add(int userId, int beforeLevel, int afterLevel, double recharge, double cost, String month) {
		// 先判断有没有本次记录
		boolean hasRecord = vUpgradeListDao.hasRecord(userId, month);
		if(!hasRecord) {
			String thisTime = new Moment().toSimpleTime();
			VipUpgradeList entity = new VipUpgradeList(userId, beforeLevel, afterLevel, recharge, cost, month, thisTime);
			return vUpgradeListDao.add(entity);
		}
		return false;
	}
	
}