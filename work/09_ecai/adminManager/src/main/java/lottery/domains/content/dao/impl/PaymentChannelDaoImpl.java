package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.PaymentChannelDao;
import lottery.domains.content.entity.PaymentChannel;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentChannelDaoImpl implements PaymentChannelDao {
	
	private final String tab = PaymentChannel.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<PaymentChannel> superDao;

	@Override
	public List<PaymentChannel> listAll() {
		String hql = "from " + tab + " order by sequence";
		return superDao.list(hql);
	}

	@Override
	public List<PaymentChannel> listAll(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(PaymentChannel.class, criterions, orders);
	}
	
	@Override
	public PaymentChannel getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (PaymentChannel) superDao.unique(hql, values);
	}
	
	@Override
	public int getOverload() {
		String hql = "select count(id) from " + tab + " where usedCredits >= totalCredits";
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public boolean add(PaymentChannel entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(PaymentChannel entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}

	@Override
	public boolean modSequence(int id, int sequence) {
		String hql = "update " + tab + " set sequence = sequence + ?1 where id = ?0";
		Object[] values = {id, sequence};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateSequence(int id, int sort) {
		String hql = "update " + tab + " set  sequence= ?1 where id = ?0";
		Object[] values = {id, sort};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean batchModSequence(int sequence) {
		String hql = "update " + tab + " set sequence = sequence - 1 where sequence > ?0";
		Object[] values = {sequence};
		return superDao.update(hql, values);
	}

	@Override
	public PaymentChannel getBySequence(int sequence) {
		String hql = "from " + tab + " where sequence = ?0";
		Object[] values = {sequence};
		return (PaymentChannel) superDao.unique(hql, values);
	}

	@Override
	public int getTotal() {
		String hql = "select count(id) from " + tab;
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public List<PaymentChannel> getBySequenceUp(int sequence) {
		String hql = "from " + tab + " where sequence < ?0 order by sequence desc";
		Object[] values = {sequence};
		return superDao.list(hql, values);
	}
	
	@Override
	public List<PaymentChannel> getBySequenceDown(int sequence) {
		String hql = "from " + tab + " where sequence > ?0 order by sequence asc";
		Object[] values = {sequence};
		return superDao.list(hql, values);
	}
	
	@Override
	public int getMaxSequence() {
		String hql = "select max(sequence) from " + tab;
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public boolean addUsedCredits(int id, double credit) {
		String hql = "update " + tab + " set usedCredits = usedCredits + ?0 where id = ?1";
		Object[] values = {credit, id};
		return superDao.update(hql, values);
	}
}