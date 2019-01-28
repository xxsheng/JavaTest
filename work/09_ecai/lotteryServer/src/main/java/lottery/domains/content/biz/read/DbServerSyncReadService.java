package lottery.domains.content.biz.read;

import lottery.domains.content.entity.DbServerSync;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
public interface DbServerSyncReadService {
    List<DbServerSync> listAll();
}
