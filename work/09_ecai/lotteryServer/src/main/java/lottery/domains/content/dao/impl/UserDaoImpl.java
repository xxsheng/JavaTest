package lottery.domains.content.dao.impl;

import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
	
	public static final String tab = User.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<User> superDao;
	
	@Override
	public boolean add(User entity) {
		return superDao.save(entity);
	}

	@Override
	public User getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (User) superDao.unique(hql, values);
	}

	@Override
	public User getByUsername(String username) {
		String hql = "from " + tab + " where username = ?0";
		Object[] values = {username};
		return (User) superDao.unique(hql, values);
	}

	@Override
	public boolean updateLoginTime(int id, String time) {
		String hql = "update " + tab + " set loginTime = ?1 where id = ?0";
		Object[] values = {id, time};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateWithdrawName(int id, String withdrawName) {
		String hql = "update " + tab + " set withdrawName = ?1 where id = ?0";
		Object[] values = {id, withdrawName};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateWithdrawPassword(int id, String md5Pwd) {
		String hql = "update " + tab + " set withdrawPassword = ?1 where id = ?0";
		Object[] values = {id, md5Pwd};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLoginPwd(int id, String md5Pwd) {
		String hql = "update " + tab + " set password = ?1 where id = ?0";
		Object[] values = {id, md5Pwd};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateImgPwd(int id, String md5Pwd) {
		String hql = "update " + tab + " set imgPassword = ?1 where id = ?0";
		Object[] values = {id, md5Pwd};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateTotalMoney(int id, double amount) {
		String hql = "update " + tab + " set totalMoney = totalMoney + ?1 where id = ?0";
		if(amount < 0) {
			hql += " and totalMoney >= " + (-amount);
		}
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLotteryMoney(int id, double amount) {
		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1 where id = ?0";
		if(amount < 0) {
			hql += " and lotteryMoney >= " + (-amount);
		}
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLotteryMoney(int id, double lotteryAmount, double freezeAmount) {
		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1, freezeMoney = freezeMoney + ?2 where id = ?0";
		if(lotteryAmount < 0) {
			hql += " and lotteryMoney >= " + (-lotteryAmount);
		}
		Object[] values = {id, lotteryAmount, freezeAmount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateBaccaratMoney(int id, double amount) {
		String hql = "update " + tab + " set baccaratMoney = baccaratMoney + ?1 where id = ?0";
		if(amount < 0) {
			hql += " and baccaratMoney >= " + (-amount);
		}
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateFreezeMoney(int id, double amount) {
		String hql = "update " + tab + " set freezeMoney = freezeMoney + ?1 where id = ?0";
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateDividendMoney(int id, double amount) {
		String hql = "update " + tab + " set dividendMoney = dividendMoney + ?1 where id = ?0";
		if(amount < 0) {
			hql += " and dividendMoney >= " + (-amount);
		}
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateMoney(int id, double totalAmount, double lotteryAmount,
			double baccaratAmount, double freezeAmount, double dividendAmount) {
		String hql = "update " + tab + " set totalMoney = totalMoney + ?1, lotteryMoney = lotteryMoney + ?2, baccaratMoney = baccaratMoney + ?3, freezeMoney = freezeMoney + ?4, dividendMoney = dividendMoney + ?5 where id = ?0";
		if(totalAmount < 0) {
			hql += " and totalMoney >= " + (-totalAmount);
		}
		if(lotteryAmount < 0) {
			hql += " and lotteryMoney >= " + (-lotteryAmount);
		}
		if(baccaratAmount < 0) {
			hql += " and baccaratMoney >= " + (-baccaratAmount);
		}
		if(dividendAmount < 0) {
			hql += " and dividendMoney >= " + (-dividendAmount);
		}
		Object[] values = {id, totalAmount, lotteryAmount, baccaratAmount, freezeAmount, dividendAmount};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateLotteryPoint(int id, int code, double lp, double nlp, int ct, int BStatus) {
		String hql = "update " + tab + " set code = ?1, locatePoint = ?2, notLocatePoint = ?3, codeType = ?4, BStatus=?5 where id = ?0";
		Object[] values = {id, code, lp, nlp, ct, BStatus};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateAStatus(int id, int status, String message) {
		String hql = "update " + tab + " set AStatus = ?1, message = ?2 where id = ?0";
		Object[] values = {id, status, message};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateBStatus(int id, int status, String message) {
		String hql = "update " + tab + " set BStatus = ?1, message = ?2 where id = ?0";
		Object[] values = {id, status, message};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateBindStatus(int id, int status) {
		String hql = "update " + tab + " set bindStatus = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateNickname(int id, String nickname) {
		String hql = "update " + tab + " set nickname = ?1 where id = ?0";
		Object[] values = {id, nickname};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean setOnline(String username, String sessionId) {
		String time = new Moment().toSimpleTime();
		String hql = "update " + tab + " set sessionId = ?1, onlineStatus = 1, loginTime= ?2 where username = ?0";
		Object[] values = {username, sessionId, time};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean setOffline(String sessionId) {
		String hql = "from " + tab + " where sessionId = ?0";
		Object[] queryvalues = {sessionId};
		User user =  (User) superDao.unique(hql, queryvalues);
		if(user != null){
			Object[] values = {user.getId()};
			return superDao.update("update " + tab + " set sessionId = null, onlineStatus = 0 where id = ?0", values);
		}
		return true;
	
	}
	
	@Override
	public boolean updateIntegral(int id, double amount) {
		String hql = "update " + tab + " set integral = integral + ?1 where id = ?0";
		if(amount < 0) {
			hql += " and integral >= " + (-amount);
		}
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateFreezeMoney(List<Object[]> list) {
		String sql = "update `user` set `freeze_money` = `freeze_money` + ? where `id` = ?";
		List<Object[]> params = new ArrayList<>();
		for (Object[] o : list) {
			int id = (int) o[0];
			double amount = (double) o[1];
			Object[] param = {amount, id};
			params.add(param);
		}
		return superDao.doWork(sql, params);
	}

	// @Override
	// public boolean updateBstatusAndAllowEqualCode(Integer bstatus, int allowEqualCode,int UserId) {
	// 	String hql = "update " + tab + " set BStatus = ?1, allowEqualCode = ?2 where id = ?0";
	// 	Object[] values = {UserId, bstatus, allowEqualCode};
	// 	return superDao.update(hql, values);
	// }

	@Override
	public boolean updateSecretKey(int id, String secretKey) {
		String hql = "update " + tab + " set secretKey = ?1 where id = ?0";
		Object[] values = {id, secretKey};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateIsBindGoogle(int id, int isBindGoogle) {
		String hql = "update " + tab + " set isBindGoogle = ?1 where id = ?0";
		Object[] values = {id, isBindGoogle};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateLoginValidate(int uerId, int loginValidate) {
		String hql = "update " + tab + " set loginValidate = ?1 where id = ?0";
		Object[] values = {uerId, loginValidate};
		return superDao.update(hql, values);
	}

	@Override
	public List<User> getDemoListByonlineStatus(int onlineStatus) {
		String hql = "from " + tab + " where type = ?0 and nickname = ?1 and onlineStatus = ?2";
		Object[] values = {2,"试玩用户",onlineStatus};
		return superDao.list(hql, values);
	}

	@Override
	public boolean updateDemoUserLotteryMoney(int id, double amount) {
		String hql = "update " + tab + " set  lottery_money =  ?1 where id = ?0 and nickname = ?2";
		if(amount < 0) {
			hql += " and lottery_money >= " + (-amount);
		}
		Object[] values = {id, amount,"试玩用户"};
		return superDao.update(hql, values);
	}
}