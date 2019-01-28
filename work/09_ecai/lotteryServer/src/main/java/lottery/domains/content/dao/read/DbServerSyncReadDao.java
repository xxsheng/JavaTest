package lottery.domains.content.dao.read;

import java.util.List;

import lottery.domains.content.entity.DbServerSync;

public interface DbServerSyncReadDao {

	List<DbServerSync> listAll();
	
}