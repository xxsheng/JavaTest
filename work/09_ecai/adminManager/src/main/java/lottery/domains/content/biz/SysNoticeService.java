package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface SysNoticeService {

	PageList search(Integer status, int start, int limit);
	
	boolean add(String title, String content, String simpleContent, int sort, int status, String date);

	boolean edit(int id, String title, String content, String simpleContent, int sort, int status, String date);

	boolean updateStatus(int id, int status);
	
	boolean updateSort(int id, int sort);

	boolean delete(int id);
	
}