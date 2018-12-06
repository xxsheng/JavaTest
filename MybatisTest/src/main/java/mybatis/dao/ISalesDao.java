package mybatis.dao;

import java.util.List;

import mybatis.pojo.Sales;

public interface ISalesDao {

	public Sales getById(int id);
	public List<Sales> getAll();
}
