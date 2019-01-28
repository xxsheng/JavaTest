package lottery.domains.content.biz;

/**
 * Created by Nick on 2016/12/28.
 */
public interface UserGameReportService {
    boolean update(int userId, int platformId, int type, double amount, String time);
}
