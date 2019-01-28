package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface ActivitySalaryService {
	
	PageList search(String username, String date, Integer type, int start, int limit);

}