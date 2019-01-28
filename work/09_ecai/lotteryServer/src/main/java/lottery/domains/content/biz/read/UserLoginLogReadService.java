package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;
import lottery.domains.content.vo.user.UserLoginLogVO;

import java.util.List;

public interface UserLoginLogReadService {
	UserLoginLogVO getLastLogin(int userId, int start);

	List<UserLoginLogVO> getLoginLog(int userId, int count);

	PageList getLoginLogList(int userId, int start, int limit);
}