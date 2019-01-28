package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.LotteryCrawlerStatus;
import lottery.domains.content.vo.lottery.LotteryCrawlerStatusVO;

public interface LotteryCrawlerStatusService {
	
	List<LotteryCrawlerStatusVO> listAll();
	
	LotteryCrawlerStatus getByLottery(String lottery);
	
	boolean update(String lottery, String lastExpect, String lastUpdate);

}