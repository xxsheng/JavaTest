package mybatistest;

import static org.junit.Assert.assertNotNull;

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

import mybatis.dao.ISalesDao;
import mybatis.dao.ISalesDaoMapper;
import mybatis.pojo.Sales;

public class SalesmanDaoTest {

	private Reader reader;
	private SqlSessionFactory sqlSessionFactory;
	
	@Before
	public void setUp() {
		try {
			reader = Resources.getResourceAsReader("config/mybatis/mybatisConfig.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// TODO: handle finally clause
		}
		
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}
	
	@After
	public void after() {
		
	}
	
	@Test
	public void getById() {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			ISalesDao isImpl = session.getMapper(ISalesDao.class);
			Sales sales = isImpl.getById(1);
			assertNotNull(sales);
			System.out.println(sales);
		} finally {
			// TODO: handle finally clause
			
			session.close();
		}
	}
	
	@Test
	public void getAll() {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			ISalesDao isImpl = session.getMapper(ISalesDao.class);
			List<Sales> list = isImpl.getAll();
			assertNotNull(list);
			System.out.println(list);
		} finally {
			// TODO: handle finally clause
			
			session.close();
		}
	}
	
	@Test
	public void getSalesmanById() {
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ISalesDaoMapper isMapperImpl = session.getMapper(ISalesDaoMapper.class);
			Sales sales = isMapperImpl.getSalesmanById(1);
			System.out.println(sales.getUserinfo().getUserId());
			System.out.println("----------------------1111");
			System.out.println(sales.getUserinfo().getUserId());
			System.out.println("----------------------222");
			
			
		} finally {
			// TODO: handle finally clause
			session.close();
		}
	}
}

