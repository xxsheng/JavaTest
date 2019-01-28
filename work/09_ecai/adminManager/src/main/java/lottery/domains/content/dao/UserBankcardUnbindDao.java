package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBankcardUnbindRecord;

public interface UserBankcardUnbindDao {
	
	boolean add(UserBankcardUnbindRecord entity);
	
	boolean update(UserBankcardUnbindRecord entity);

	boolean delByCardId(String cardId);
	
	boolean updateByParam(String userIds,String cardId,int unbindNum,String unbindTime);
	
	UserBankcardUnbindRecord getUnbindInfoById(int id);
	
	UserBankcardUnbindRecord getUnbindInfoBycardId(String cardId);
	/**
	 * 分页查询
	 */
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	/**
	 * 获取所有
	 */
	List<UserBankcardUnbindRecord> listAll();
}
