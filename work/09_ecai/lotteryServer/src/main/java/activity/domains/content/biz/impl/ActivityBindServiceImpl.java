package activity.domains.content.biz.impl;

import org.springframework.stereotype.Service;

import activity.domains.content.biz.ActivityBindService;

@Service
public class ActivityBindServiceImpl implements ActivityBindService {

//	@Autowired
//	private ActivityRebateWriteDao aRebateDao;
//	
//	@Autowired
//	private ActivityBindBillWriteDao aBindBillDao;
	
//	@Override
//	public boolean add(User uBean, String ip, String bindName, int bindBank, String bindCard) {
//		ActivityRebate activity = aRebateDao.getByType(Global.ACTIVITY_REBATE_BIND);
//		if(activity != null) {
//			int userId = uBean.getId();
//			String registTime = uBean.getRegistTime();
//			double money = Double.parseDouble(activity.getRules());
//			String time = new Moment().toSimpleTime();
//			int status = 0;
//			ActivityBindBill entity = new ActivityBindBill(userId, registTime, ip, bindName, bindBank, bindCard, money, time, status);
//			return aBindBillDao.add(entity);
//		}
//		return false;
//	}
	
}