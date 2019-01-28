package lottery.domains.content.biz;

public interface UserLoginLogService {
	boolean add(int userId, String ip, String userAgent, String time,String loginLine);
}