package lottery.web.content.validate;

import javautils.regex.RegexUtil;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.config.CodeConfig;
import lottery.domains.content.vo.user.SysCodeRangeVO;
import lottery.domains.pool.LotteryDataFactory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import admin.web.WebJSONObject;

@Component
public class UserValidate {

	@Autowired
	private UserDao uDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	public boolean testUserPoint(WebJSONObject json, User uBean, double locatePoint) {
		// 必须不能小于
		if(locatePoint <= uBean.getLocatePoint()) {
			json.set(2, "2-2014");
			return false;
		}
		// 判断最高返点
		CodeConfig config = lotteryDataFactory.getCodeConfig();
		if(locatePoint > config.getSysLp()) {
			json.set(2, "2-9");
			return false;
		}
		// 判断上级返点
		if(uBean.getUpid() != 0) {
			User upperUser = uDao.getById(uBean.getUpid());
			if(upperUser != null) {
				boolean trueCode = locatePoint < upperUser.getLocatePoint();
				if(upperUser.getAllowEqualCode() == 1) {
					trueCode = locatePoint <= upperUser.getLocatePoint();
				}
				if(!trueCode) {
					json.set(2, "2-2015");
					return false;
				}
			}
		}
		return true;
	}

	public boolean testUsername(WebJSONObject json, String username) {
		String patrn = "^[a-zA-Z]{1}([a-zA-Z0-9]){5,11}$";
		if(!RegexUtil.isMatcher(username, patrn)) {
			json.set(2, "2-2029");
			return false;
		}
		return true;
	}

	public boolean testNewUserPoint(WebJSONObject json, double locatePoint) {
		// 判断最高返点
		CodeConfig config = lotteryDataFactory.getCodeConfig();
		if(locatePoint < 0 || locatePoint > config.getSysLp()) {
			json.set(2, "2-9");
			return false;
		}
		return true;
	}

	public boolean testNewUserPoint(WebJSONObject json, User upperUser, double locatePoint) {
		// 判断最高返点
		CodeConfig config = lotteryDataFactory.getCodeConfig();
		if(locatePoint < 0 || locatePoint > config.getSysLp()) {
			json.set(2, "2-9");
			return false;
		}
		if (locatePoint > config.getSysLp() || locatePoint < 0) {
			json.set(2, "2-2026"); // 不允许创建该账号等级！
			return false;
		}
		boolean trueCode = locatePoint < upperUser.getLocatePoint();
		if(upperUser.getAllowEqualCode() == 1) {
			trueCode = locatePoint <= upperUser.getLocatePoint();
		}
		if(!trueCode) {
			json.set(2, "2-2015");
			return false;
		}
		return true;
	}
	
	public SysCodeRangeVO loadEditPoint(User uBean) {
		CodeConfig config = lotteryDataFactory.getCodeConfig();
		double testMinPoint = 0;
		double testMaxPoint = config.getSysLp();
		// 验证最高返点
		if(uBean.getUpid() != 0) {
			User upperUser = uDao.getById(uBean.getUpid());
			if(upperUser != null) {
				testMaxPoint = upperUser.getLocatePoint();
				if(upperUser.getAllowEqualCode() != 1) {
					testMaxPoint = upperUser.getLocatePoint() - 0.1;
				}
			}
		}
		// 验证最低返点
		List<User> uDirectList = uDao.getUserDirectLower(uBean.getId());
		for (User tmpBean : uDirectList) {
			if(tmpBean.getLocatePoint() > testMinPoint) {
				testMinPoint = tmpBean.getLocatePoint();
				if(uBean.getAllowEqualCode() != 1) {
					testMinPoint += 0.1;
				}
			}
		}
		SysCodeRangeVO result = new SysCodeRangeVO();
		result.setMinPoint(testMinPoint);
		result.setMaxPoint(testMaxPoint);
		return result;
	}
	
}