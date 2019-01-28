package lottery.domains.content.biz;

import admin.web.WebJSONObject;
import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.entity.UserDividend;

import java.util.List;

/**
 * 契约日结Service
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleService {
    /**
     * 查找契约分页数据
     */
    PageList search(List<Integer> userIds, String sTime, String eTime, Double minScale, Double maxScale,
                    Integer minValidUser, Integer maxValidUser, Integer status, int start, int limit);

    UserDailySettle getByUserId(int userId);

    UserDailySettle getById(int id);

    /**
     * 删除团队日结
     */
    boolean deleteByTeam(String username);

    /**
     * 转换为超级招商或招商
     */
    boolean changeZhaoShang(User user, boolean changeToCJZhaoShang);

    /**
     * 检查日结设置，自动设置好并修复整个团队配置
     */
    void checkDailySettle(String username);

    boolean update(WebJSONObject json, int id, String scaleLevel, String salasLevel, String lossLevel, int minValidUser,String usersLevel);

    boolean add(WebJSONObject json, String username, String scaleLevel, String salesLevel, String lossLevel, int minValidUser, int status,String usersLevel);

    boolean checkCanEdit(WebJSONObject json, User user);

    boolean checkCanDel(WebJSONObject json, User acceptUser);

    double[] getMinMaxScale(User acceptUser);
    
    double[] getMinMaxSales(User acceptUser);
    
    double[] getMinMaxLoss(User acceptUser);
    
    int[] getMinMaxUsers(User acceptUser);
    
    boolean checkValidLevel(String scaleLevel, String salesLevel, String lossLevel, UserDailySettle upDailySettle,String usersLevel);
}
