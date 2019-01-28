package lottery.domains.content.biz;

import admin.web.WebJSONObject;
import javautils.jdbc.PageList;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;

import java.util.List;

public interface LotteryOpenCodeService {

	PageList search(String lottery, String expect, int start, int limit);
	
	LotteryOpenCodeVO get(String lottery, String expect);
	
	boolean add(WebJSONObject json, String lottery, String expect, String code, String opername);
	
	boolean delete(LotteryOpenCode bean);

	int countByInterfaceTime(String lottery, String startTime, String endTime);

	LotteryOpenCode getFirstExpectByInterfaceTime(String lottery, String startTime, String endTime);

	List<LotteryOpenCode> getLatest(String lottery, int count);

}