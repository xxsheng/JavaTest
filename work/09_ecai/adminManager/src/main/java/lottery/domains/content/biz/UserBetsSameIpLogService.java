package lottery.domains.content.biz;

import javautils.jdbc.PageList;

/**
 * Created by Nick on 2017/2/8.
 */
public interface UserBetsSameIpLogService {
    PageList search(String ip, String username, String sortColoum, String sortType, int start, int limit);
}
