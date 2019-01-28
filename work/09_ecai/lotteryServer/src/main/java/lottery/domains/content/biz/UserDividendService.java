package lottery.domains.content.biz;

import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividend;
import lottery.web.WebJSON;

/**
 * 契约分红service
 */
public interface UserDividendService {
	UserDividend getByUserId(int userId);

	UserDividend getById(int id);

	/**
	 * 发起契约
	 */
	boolean request(WebJSON json, int requestUserId, int acceptUserId, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String userLevel);

	/**
	 * 接受契约
	 */
	boolean agree(WebJSON json, int userId, int id);

	/**
	 * 拒绝契约
	 */
	boolean deny(WebJSON json, int userId, int id);

	/**
	 * 删除团队分红
	 */
	boolean deleteByTeam(int userId);

	/**
	 * 检查分红设置，自动设置好并修复整个团队配置
	 */
	void checkDividend(int userId);
	
	/**
	 * 获取比率区间
	 * @param acceptUser
	 * @return
	 */
	double[] getMinMaxScale(User acceptUser);
	
	/**
	 * 获取销量区间
	 * @param acceptUser
	 * @return
	 */
	double[] getMinMaxSales(User acceptUser);
	
	/**
	 * 获取亏损区间
	 * @param acceptUser
	 * @return
	 */
	double[] getMinMaxLoss(User acceptUser);
	
	/**
	 * 获取人数区间
	 * @param acceptUser
	 * @return
	 */
	int[] getMinMaxUser(User acceptUser);
}