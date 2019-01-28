package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserBankcardUnbindService;
import lottery.domains.content.dao.UserBankcardUnbindDao;
import lottery.domains.content.entity.UserBankcardUnbindRecord;
import lottery.domains.content.vo.user.UserBankcardUnbindVO;
import lottery.domains.pool.DataFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBankcardUnbindServiceImpl implements UserBankcardUnbindService{
	/**
	 * DAO
	 */
	@Autowired
	private UserBankcardUnbindDao userBankcardUnbindDao;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	@Override
	public boolean add(UserBankcardUnbindRecord entity) {
		return userBankcardUnbindDao.add(entity);
	}

	@Override
	public boolean update(UserBankcardUnbindRecord entity) {
		return userBankcardUnbindDao.update(entity);
	}

	@Override
	public boolean updateByParam(String userIds,String cardId, int unbindNum, String unbindTime) {
		return userBankcardUnbindDao.updateByParam(userIds,cardId, unbindNum, unbindTime);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBankcardUnbindVO getUnbindInfoById(int id) {
		UserBankcardUnbindRecord entity = userBankcardUnbindDao.getUnbindInfoById(id);
		if(entity != null){
			return new UserBankcardUnbindVO(entity,dataFactory);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public UserBankcardUnbindVO getUnbindInfoBycardId(String cardId) {
		UserBankcardUnbindRecord entity = userBankcardUnbindDao.getUnbindInfoBycardId(cardId);
		if(entity != null){
			return new UserBankcardUnbindVO(entity,dataFactory);
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserBankcardUnbindVO> listAll() {
		List<UserBankcardUnbindVO> list = new ArrayList<UserBankcardUnbindVO>();
		List<UserBankcardUnbindRecord> clist = userBankcardUnbindDao.listAll();
		for (UserBankcardUnbindRecord tmpBean : clist) {
			list.add(new UserBankcardUnbindVO(tmpBean, dataFactory));
		}
		return list;
	}

}
