/**
 * 
 */
package mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import mybatis.pojo.Communicator;

/**
 * @author xxq_1
 *
 */
public interface ICommunicatorDao {

	@Select("SELECT * FROM communicator WHERE communicator_id = #{id}")
	public Communicator getById(@Param(value = "id") int id);
	
	@Select("SELECT * FROM communicator ORDER BY communicator_name")
	public List<Communicator> getAll();
}
