package lottery.domains.content.biz;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.vo.activity.ActivityRewardBillVO;

public interface ActivityRewardService {
	
	PageList search(String username, String date, Integer type, Integer status, int start, int limit);
	
	boolean add(int toUser, int fromUser, int type, double totalMoney, double money, String date);
	
	List<ActivityRewardBillVO> getLatest(int toUser, int status, int count);
	
	boolean agreeAll(String date);
	
	boolean calculate(int type, String date);

}