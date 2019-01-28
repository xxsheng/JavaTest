package lottery.domains.content.biz;

import lottery.domains.content.vo.InstantStatVO;

public interface LotteryInstantStatService {

	InstantStatVO getInstantStat(String sTime, String eTime);
	
}