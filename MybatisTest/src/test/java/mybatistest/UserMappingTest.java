package mybatistest;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.portable.Delegate;

import mybatis.pojo.User;

/**
 * @author xxq_1
 *
 */
public class UserMappingTest {

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
	 * 通过Id查找user
	 */
	@Test
	public void testGetById() {
		SqlSession session = sqlSessionFactory.openSession();
		try {

			User user = session.selectOne("mybatis.mapping.User.getByID",1);

			if (null == user) {
				System.out.println("the result is null.");
			} else {
				System.out.println(user.getUserName());
				System.out.println(user.getNickName());
				System.out.println(user);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	/**
	 * 测试写入数据
	 */
	@Test
	public void testInsertUser() {
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			
			User user = new User();
			user.setEmail("1558281773@qq.com");
			user.setNickName("xueyouzhang");
			user.setUserName("xxq2");
			user.setIsValid(1);
			user.setUserPassword("5f4dcc3b5aa765d61d8327deb882cf99");
			
			session.insert("mybatis.mapping.User.insertUser", user);
			System.out.println("New Id is " + user.getUserId() );
			session.commit();
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
	
	/**
	 * 测试模糊查询
	 */
	@Test
	public void testQueryByName() {
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			User user = new User();
			user.setUserName("xq");
			
			List<User> list = session.selectList("mybatis.mapping.User.queryByName", user);
			System.out.println(list.size());
			
			for(User u : list) {
				System.out.println(u);
			}
			
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
	
	/**
	 * 测试更新数据
	 */
	@Test
	public void testUpdateUser() {
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			User user = session.selectOne("mybatis.mapping.User.getByID",1);
			user.setEmail("123456@qq.com");
			user.setUserName("zq");
			user.setIsValid(0);
			user.setUserTypeId(1);
			
			int result = session.update("mybatis.mapping.User.updateUser", user);
			session.commit();
			
			System.out.println(result);
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
	
	/**
	 * 测试通过id删除 
	 */
	@Test
	public void testUserDelete() {
		SqlSession session = sqlSessionFactory.openSession();

		try {
			int result = session.delete("mybatis.mapping.User.deleteById", 1);
			System.out.println(result);
			session.commit();
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
	
	@Test
	public void testDeleteByIdList() {
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			List<Integer> idList = new ArrayList<Integer>();
			idList.add(2);
			idList.add(4);
			idList.add(5);
			
			int result = session.delete("mybatis.mapping.User.deleteBatch", idList);
			System.out.println(result);
			session.commit();
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
}
