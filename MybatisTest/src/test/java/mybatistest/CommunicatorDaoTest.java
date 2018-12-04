/**
 * 
 */
package mybatistest;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mybatis.dao.ICommunicatorDao;
import mybatis.pojo.Communicator;

/**
 * @author xxq_1
 *
 */
public class CommunicatorDaoTest {

	private Reader reader;
	private SqlSessionFactory sqlSessionFactory;

	@Before
	public void setUp() {
		try {
			reader = Resources.getResourceAsReader("config/mybatis/mybatisConfig.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}
	
	@After
	public void tearDown() {
		
	}
	
	/**
	 * 测试通过id查询
	 * 
	 */
	@Test
	public void testGetById() {
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ICommunicatorDao dao = session.getMapper(ICommunicatorDao.class);
			Communicator cc = dao.getById(1);
			if(null == cc) {
				System.out.println("the result is null.");
			}else {
				System.out.println(cc);
			}
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
	
	@Test
	public void testGetAll() {
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ICommunicatorDao cc = session.getMapper(ICommunicatorDao.class);
			List<Communicator> list = cc.getAll();
			for (Communicator communicator : list) {
				System.out.println(communicator.toString());
				System.out.println(communicator);
			}
			
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
}
