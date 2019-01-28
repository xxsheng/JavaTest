package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.SysNotice;

public interface SysNoticeDao {
	
	SysNotice getById(int id);

	List<SysNotice> get(int count);
	
	List<SysNotice> getNoticeTitle(int count);
	
	List<SysNotice> getNoticeTitleLastNew(int count);
	
}