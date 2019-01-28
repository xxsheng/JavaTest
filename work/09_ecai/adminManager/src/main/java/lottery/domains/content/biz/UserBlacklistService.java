package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface UserBlacklistService {
	
	PageList search(String keyword, int start, int limit);
	
	boolean add(String username, String cardName, String cardId, Integer bankId, String ip, String operatorUser, String operatorTime, String remarks);
	
	boolean delete(int id);
	
}