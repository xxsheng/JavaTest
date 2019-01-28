package lottery.domains.content.biz;

import lottery.domains.content.entity.SysNotice;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
public interface SysNoticeService {
    SysNotice getById(int id);

    List<SysNotice> get(int count);

    List<SysNotice> getNoticeTitle(int count);

    List<SysNotice> getNoticeTitleLastNew(int count);
}
