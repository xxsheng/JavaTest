package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserWhitelist;

public interface UserWhitelistDao {
	
	List<UserWhitelist> getByUsername(String username);

}