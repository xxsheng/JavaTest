package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.LotteryType;

public interface LotteryTypeService {

	List<LotteryType> listAll();
	
	boolean updateStatus(int id, int status);

}