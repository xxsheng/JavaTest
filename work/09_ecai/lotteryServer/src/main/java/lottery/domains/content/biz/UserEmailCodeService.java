package lottery.domains.content.biz;

import lottery.domains.content.entity.UserEmailCode;

public interface UserEmailCodeService {
	
	UserEmailCode generate(int type, String username, String email);
	
	UserEmailCode get(int type, String username, String code);
	
	boolean used(int id);
	
}