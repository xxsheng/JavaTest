package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserCardService;
import lottery.domains.content.dao.UserCardDao;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.vo.user.UserCardVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserCardServiceImpl implements UserCardService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserCardDao uCardDao;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	@Override
	public boolean add(int userId, int bankId, String bankBranch, String cardName, String cardId, int isDefault) {
		int status = 0;
		String time = new Moment().toSimpleTime();
		UserCard entity = new UserCard(userId, bankId, cardName, cardId, status, time, isDefault);
		entity.setBankBranch(bankBranch);
		return uCardDao.add(entity);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserCardVO> listByUserId(int userId) {
		List<UserCardVO> list = new ArrayList<>();
		List<UserCard> clist = uCardDao.listByUserId(userId);
		for (UserCard tmpBean : clist) {
			list.add(new UserCardVO(tmpBean, dataFactory));
		}
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public UserCardVO getRandomByUserId(int userId) {
		List<UserCardVO> list = listByUserId(userId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		List<UserCardVO> validateCards = new ArrayList<>();
		for (UserCardVO userCardVO : list) {
			if (userCardVO.getStatus() == 0) {
				validateCards.add(userCardVO);
			}
		}

		if(validateCards.size() > 0) {
			Random random = new Random();
			int index = random.nextInt(validateCards.size());
			return validateCards.get(index);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean validateCard(WebJSON json, int id, int userId, String cardName) {
		if (StringUtils.isEmpty(cardName)) {
			json.set(2, "2-1075");
			return false;
		}

		UserCard card = uCardDao.getById(id, userId);
		if (card == null && card.getStatus() != 0) {
			json.set(2, "2-1026");
			return false;
		}

		if (!card.getCardName().equals(cardName)) {
			json.set(2, "2-1076");
			return false;
		}

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public UserCard getById(int id, int userId) {
		return uCardDao.getById(id, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public UserCard getByCardId(String cardId) {
		return uCardDao.getByCardId(cardId);
	}

	@Override
	public boolean delete(int id, int userId) {
		return uCardDao.delete(id, userId);
	}

	@Override
	public boolean setDefault(int id, int userId) {
		return uCardDao.setDefault(id, userId);
	}
}