package lottery.domains.content.biz;

import admin.web.WebJSONObject;
import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividend;

import java.util.List;

/**
 * 契约分红service
 */
public interface UserDividendService {
	/**
	 * 查找契约分页数据
	 */
	PageList search(List<Integer> userIds, String sTime, String eTime, Double minScale, Double maxScale,
					Integer minValidUser, Integer maxValidUser, Integer status, Integer fixed, int start, int limit);

	UserDividend getByUserId(int userId);

	UserDividend getById(int id);

	/**
	 * 删除团队分红
	 */
	boolean deleteByTeam(String username);

	/**
	 * 转换为超级招商或招商
	 */
	boolean changeZhaoShang(User user, boolean changeToCJZhaoShang);

	/**
	 * 检查分红设置，自动设置好并修复整个团队配置
	 */
	void checkDividend(String username);

	boolean update(WebJSONObject json, int id, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String userLevel);

	boolean add(WebJSONObject json, String username, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, int status, String userLevel);

	boolean checkCanEdit(WebJSONObject json, User user);

	boolean checkCanDel(WebJSONObject json, User user);

	double[] getMinMaxScale(User acceptUser);
	
	double[] getMinMaxSales(User acceptUser);
	
	double[] getMinMaxLoss(User acceptUser);
	
	int[] getMinMaxUser(User acceptUser);
	
	public boolean checkValidLevel(String scaleLevel, String salesLevel, String lossLevel, UserDividend upDividend, String userLevel);
}