package lottery.domains.content.biz;

import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserInfo;
import lottery.domains.content.vo.user.UserInfoVO;

public interface UserInfoService {

	UserInfoVO get(User uBean);

	UserInfo get(int userId);

	boolean add(UserInfo entity);

	boolean update(UserInfo entity);

}