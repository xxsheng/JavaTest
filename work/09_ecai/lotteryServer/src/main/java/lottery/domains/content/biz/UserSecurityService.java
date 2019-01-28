package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.UserSecurity;
import lottery.domains.content.vo.user.UserSecurityVO;

public interface UserSecurityService {
	
	List<UserSecurity> listByUserId(int userId);
	
	boolean add(UserSecurity ... entity);
	
	boolean add(int userId, String question, String answer);
	
	UserSecurityVO getRandomByUserId(int userId);
	
	boolean validateSecurity(int id, int userId, String token, String answer);
	
}