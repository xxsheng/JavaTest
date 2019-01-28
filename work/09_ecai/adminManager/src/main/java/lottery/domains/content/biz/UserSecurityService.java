package lottery.domains.content.biz;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.vo.user.UserSecurityVO;

public interface UserSecurityService {
	
	PageList search(String username, String key, int start, int limit);
	
	List<UserSecurityVO> getByUserId(int id);
	
	boolean reset(String username);

}