package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBetsOriginal;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserBetsOriginalDao {

	UserBetsOriginal getById(int id);

	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	PageList find(String sql , int start, int limit);

	/**
	 * 第一位是总投注(money)，第二位是总奖金(prizeMoney)
	 */
	double[] getTotalMoney(String keyword, Integer userId,Integer utype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
						   String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
						   Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status);
}