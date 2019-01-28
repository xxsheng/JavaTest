package lottery.domains.content.dao;

import lottery.domains.content.entity.DbServerSync;
import lottery.domains.content.global.DbServerSyncEnum;

import java.util.List;

public interface DbServerSyncDao {

	List<DbServerSync> listAll();

	boolean update(DbServerSyncEnum type);
	
}