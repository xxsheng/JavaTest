package lottery.domains.content.biz;

import java.util.List;


import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBankcardUnbindRecord;
import lottery.domains.content.vo.user.UserBankcardUnbindVO;

public interface UserBankcardUnbindService {
	
	boolean add(UserBankcardUnbindRecord entity);
	
	boolean update(UserBankcardUnbindRecord entity);

	boolean delByCardId(String cardId);

	boolean updateByParam(String userIds,String cardId,int unbindNum,String unbindTime);
	
	UserBankcardUnbindVO getUnbindInfoById(int id);
	
	UserBankcardUnbindVO getUnbindInfoBycardId(String cardId);
	
	/**
	 * 分页查询
	 */
	PageList search(String userNames,String cardId,String unbindTime,
			int start, int limit);
	/**
	 * 获取所有
	 */
	List<UserBankcardUnbindVO> listAll();
}
