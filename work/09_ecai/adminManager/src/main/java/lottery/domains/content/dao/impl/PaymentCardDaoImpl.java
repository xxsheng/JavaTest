package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.PaymentCardDao;
import lottery.domains.content.entity.PaymentCard;

@Repository
public class PaymentCardDaoImpl implements PaymentCardDao {
	
	private final String tab = PaymentCard.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<PaymentCard> superDao;

	@Override
	public List<PaymentCard> listAll() {
		String hql = "from " + tab + " order by bankId";
		return superDao.list(hql);
	}

	@Override
	public PaymentCard getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (PaymentCard) superDao.unique(hql, values);
	}
	
	@Override
	public int getOverload() {
		String hql = "select count(id) from " + tab + " where usedCredits >= totalCredits";
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public boolean add(PaymentCard entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(PaymentCard entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}

	@Override
	public boolean addUsedCredits(int cardId, double usedCredits) {
		String hql = "update " + tab + " set usedCredits = usedCredits + ?0 where id = ?1";
		Object[] values = {usedCredits, cardId};
		return superDao.update(hql, values);
	}
}