package lottery.domains.content.biz.impl;

import com.alibaba.fastjson.JSON;
import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserBetsLimitService;
import lottery.domains.content.dao.UserBetsLimitDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBetsLimit;
import lottery.domains.content.vo.user.UserBetsLimitVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserBetsLimitServiceImpl implements UserBetsLimitService {

	@Autowired
	private UserBetsLimitDao userBetsLimitDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private JedisTemplate jedisTemplate;

	private static final String USER_LIMIT_KEY = "USER:LIMIT:";
	
	@Override
	public boolean addUserBetsLimit(String username, int lotteryId, double maxBet, String source, double maxPrize) {
		User user = userDao.getByUsername(username);
		if(source.equals("user") && user == null){
			throw new IllegalArgumentException("用户名不存在!");
		}
		UserBetsLimit userBetsLimit = new UserBetsLimit();
		if(user != null){
			userBetsLimit.setUserId(user.getId());
		}
		else {
			userBetsLimit.setUserId(0);//系统配置
		}
		
		userBetsLimit.setLotteryId(lotteryId);
		userBetsLimit.setMaxBet(maxBet);
		userBetsLimit.setMaxPrize(maxPrize);



		boolean saved = userBetsLimitDao.save(userBetsLimit);
		if (saved) {
			// 存入redis
			List<UserBetsLimit> limits = new ArrayList<>();
			String oldValue = jedisTemplate.get(USER_LIMIT_KEY + userBetsLimit.getUserId());
			if (StringUtils.isNotEmpty(oldValue)) {
				limits = JSON.parseArray(oldValue, UserBetsLimit.class);
			}

			limits.add(userBetsLimit);
			jedisTemplate.set(USER_LIMIT_KEY + userBetsLimit.getUserId(), JSON.toJSONString(limits));
		}

		return userBetsLimitDao.save(userBetsLimit);
	}

	@Override
	public PageList search(String username, int start, int limit, boolean queryGobalSetting) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("id"));
		if(StringUtil.isNotNull(username)){
			User user = userDao.getByUsername(username);
			if(user != null){
				criterions.add(Restrictions.eq("userId", user.getId()));
			}else if( !queryGobalSetting && user == null){
				return null;
			}
		}
		//系统全局设置
		if(queryGobalSetting){
			criterions.add(Restrictions.eq("userId", 0));
		}else{
			criterions.add(Restrictions.ne("userId", 0));
		}
		
		PageList pageList = userBetsLimitDao.find(criterions, orders, start, limit);
		List<UserBetsLimitVO> data = new ArrayList<>();
		if(pageList != null){
			for (Object o : pageList.getList()) {
				data.add(new UserBetsLimitVO((UserBetsLimit)o, lotteryDataFactory));
			}
			pageList.setList(data);
		}
		return pageList;
	}

	@Override
	public boolean updateUserBetsLimit(UserBetsLimit ubLimit) {
		boolean result = userBetsLimitDao.update(ubLimit);
		if (result) {

			// 更新redis
			List<UserBetsLimit> limits = new ArrayList<>();
			String oldValue = jedisTemplate.get(USER_LIMIT_KEY + ubLimit.getUserId());
			if (StringUtils.isNotEmpty(oldValue)) {
				limits = JSON.parseArray(oldValue, UserBetsLimit.class);

				if (CollectionUtils.isEmpty(limits)) {
					limits = new ArrayList<>();
					limits.add(ubLimit);
				}
				else {
					for (UserBetsLimit limit : limits) {
						if (limit.getLotteryId() == ubLimit.getLotteryId()) {
							limit.setMaxBet(ubLimit.getMaxBet());
							limit.setMaxPrize(ubLimit.getMaxPrize());
							break;
						}
					}
				}
			}
			else {
				limits.add(ubLimit);
			}

			jedisTemplate.set(USER_LIMIT_KEY + ubLimit.getUserId(), JSON.toJSONString(limits));
		}
		return result;
	}

	@Override
	public UserBetsLimitVO getById(int id) {
		UserBetsLimit userBetsLimit = userBetsLimitDao.getById(id);
		if(userBetsLimit != null){
			return new UserBetsLimitVO(userBetsLimit, lotteryDataFactory);
		}
		return null;
	}

	@Override
	public boolean deleteUserBetsLimit(int id) {
		UserBetsLimit userBetsLimit = userBetsLimitDao.getById(id);
		jedisTemplate.del(USER_LIMIT_KEY + userBetsLimit.getUserId());
		return userBetsLimitDao.delete(id);
	}

	@Override
	public boolean addOrUpdate(Integer id, String username, int lotteryId, double maxBet, String source, double maxPrize) {
		if(id == null){//add
			return this.addUserBetsLimit(username, lotteryId, maxBet, source, maxPrize);
		}
		
		UserBetsLimit updateVO = new UserBetsLimit();
		updateVO.setId(id);
		
		User user = userDao.getByUsername(username);
		
		updateVO.setLotteryId(lotteryId);
		updateVO.setUserId(user == null ? 0 : user.getId());
		updateVO.setMaxBet(maxBet);
		updateVO.setMaxPrize(maxPrize);
		
		return this.updateUserBetsLimit(updateVO);
	}

}
