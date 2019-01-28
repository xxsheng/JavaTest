package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBalanceSnapshot;

/**
 * Created by Nick on 2017-07-03.
 */
public interface UserBalanceSnapshotService {
    PageList search(String sDate, String eDate, int start, int limit);

    boolean add(UserBalanceSnapshot entity);
}
