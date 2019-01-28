package lottery.domains.content.biz;

import lottery.domains.content.entity.UserWhitelist;

import java.util.List;

public interface UserWhitelistService {
    List<UserWhitelist> getByUsername(String username);
}