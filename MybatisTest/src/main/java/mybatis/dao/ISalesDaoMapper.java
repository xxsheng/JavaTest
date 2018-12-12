package mybatis.dao;

import mybatis.pojo.Sales;

public interface ISalesDaoMapper {

	public Sales getSalesmanById(int id);
	public Sales getById(int id);
}
