package lottery.domains.content.biz;

public interface UserInfoService {
	
	boolean resetEmail(String username);
	
	boolean modifyEmail(String username, String email);
	
}