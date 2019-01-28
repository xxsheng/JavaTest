package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.UserCard;
import lottery.domains.content.vo.user.UserCardVO;
import lottery.domains.content.vo.user.UserSecurityVO;
import lottery.web.WebJSON;

public interface UserCardService {
	
	boolean add(int userId, int bankId, String bankBranch, String cardName, String cardId, int isDefault);
	
	List<UserCardVO> listByUserId(int userId);

	UserCardVO getRandomByUserId(int userId);

	boolean validateCard(WebJSON json, int id, int userId, String cardName);

	UserCard getById(int id, int userId);

	UserCard getByCardId(String cardId);

	boolean delete(int id, int userId);

	boolean setDefault(int id, int userId);
}