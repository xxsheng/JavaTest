package lottery.domains.content.biz;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.vo.user.UserCardVO;

public interface UserCardService {
	
	PageList search(String username, String keyword, Integer status, int start, int limit);
	
	UserCardVO getById(int id);

	UserCard getByUserAndCardId(int userId, String cardId);

	List<UserCardVO> getByUserId(int id);
	
	boolean add(String username, int bankId, String bankBranch, String cardName, String cardId, int status);
	
	boolean edit(int id, int bankId, String bankBranch, String cardId);
	
	boolean updateStatus(int id, int status);
	
}