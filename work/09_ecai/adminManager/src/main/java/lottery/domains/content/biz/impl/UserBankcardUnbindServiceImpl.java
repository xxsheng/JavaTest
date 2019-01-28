package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBankcardUnbindService;
import lottery.domains.content.dao.UserBankcardUnbindDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBankcardUnbindRecord;
import lottery.domains.content.vo.user.UserBankcardUnbindVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class UserBankcardUnbindServiceImpl implements UserBankcardUnbindService{
	/**
	 * DAO
	 */
	@Autowired
	private UserBankcardUnbindDao userBankcardUnbindDao;
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	@Override
	public boolean add(UserBankcardUnbindRecord entity) {
		return userBankcardUnbindDao.add(entity);
	}

	@Override
	public boolean update(UserBankcardUnbindRecord entity) {
		return userBankcardUnbindDao.update(entity);
	}

	@Override
	public boolean delByCardId(String cardId) {
		return userBankcardUnbindDao.delByCardId(cardId);
	}

	@Override
	public boolean updateByParam(String userIds,String cardId, int unbindNum, String unbindTime) {
		return userBankcardUnbindDao.updateByParam(userIds,cardId, unbindNum, unbindTime);
	}

	@Override
	public UserBankcardUnbindVO getUnbindInfoById(int id) {
		UserBankcardUnbindRecord entity = userBankcardUnbindDao.getUnbindInfoById(id);
		if(entity != null){
			return new UserBankcardUnbindVO(entity,lotteryDataFactory);
		}
		return null;
	}

	@Override
	public UserBankcardUnbindVO getUnbindInfoBycardId(String cardId) {
		UserBankcardUnbindRecord entity = userBankcardUnbindDao.getUnbindInfoBycardId(cardId);
		if(entity != null){
			return new UserBankcardUnbindVO(entity,lotteryDataFactory);
		}
		return null;
	}

	@Override
	public PageList search(String userNames,String cardId,String unbindTime,
			int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		if(StringUtil.isNotNull(userNames)) {
			User userEntity = uDao.getByUsername(userNames);
			if(userEntity != null){
				criterions.add(Restrictions.like("userIds", "#"+userEntity.getId()+"#",MatchMode.ANYWHERE));
			}else{
				criterions.add(Restrictions.like("userIds", String.valueOf("0000"),MatchMode.ANYWHERE));
			}
		}

		if(StringUtil.isNotNull(cardId)) {
			criterions.add(Restrictions.like("cardId", cardId,MatchMode.ANYWHERE));
		}

		if(StringUtil.isNotNull(unbindTime)) {
			criterions.add(Restrictions.like("unbindTime", unbindTime, MatchMode.ANYWHERE));
		}

		orders.add(Order.desc("unbindTime"));
		orders.add(Order.desc("id"));

		PageList pList = userBankcardUnbindDao.search(criterions, orders, start, limit);
		List<UserBankcardUnbindVO> list = new ArrayList<UserBankcardUnbindVO>();
		for (Object o : pList.getList()) {
			list.add(new UserBankcardUnbindVO((UserBankcardUnbindRecord) o,lotteryDataFactory));
		}
		pList.setList(list);
		return pList;
		
	}

	@Override
	public List<UserBankcardUnbindVO> listAll() {
		List<UserBankcardUnbindVO> list = new ArrayList<UserBankcardUnbindVO>();
		List<UserBankcardUnbindRecord> clist = userBankcardUnbindDao.listAll();
		for (UserBankcardUnbindRecord tmpBean : clist) {
			list.add(new UserBankcardUnbindVO(tmpBean,lotteryDataFactory));
		}
		return list;
	}

}
