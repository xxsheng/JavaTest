package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.vo.lottery.LotteryVO;

public interface LotteryService {

	List<LotteryVO> list(String type);
	
	boolean updateStatus(int id, int status);

	boolean updateTimes(int id, int times);

	Lottery getByShortName(String shortName);

}