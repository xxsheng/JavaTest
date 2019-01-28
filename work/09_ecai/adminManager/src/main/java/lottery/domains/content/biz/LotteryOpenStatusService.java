package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.vo.lottery.LotteryOpenStatusVO;

public interface LotteryOpenStatusService {

	List<LotteryOpenStatusVO> search(String lotteryId, String date);
	
	boolean doManualControl(String lottery, String expect);
}
