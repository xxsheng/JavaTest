package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.LotteryCrawlerStatus;

public interface LotteryCrawlerStatusDao {
	
	List<LotteryCrawlerStatus> listAll();
	
	LotteryCrawlerStatus get(String lottery);
	
	boolean update(LotteryCrawlerStatus entity);
	
}