package zl.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class HibernateApplicationTests {

	@Autowired
	LocalSessionFactoryBean factory;
	@Test
	public void contextLoads() throws Exception {
	    long start = System.currentTimeMillis();
	    /**hibernate.jdbc.batch_size不会设置很大，在30-50左右，但是大批量的插入，显然此值偏小。
	     * 大批量的插入也不推荐使用hibernate的批量插入（多余的封装处理环节，建议jdbc）
	     * */
	    factory.getHibernateProperties().setProperty("hibernate.jdbc.batch_size", "5000");
	    factory.afterPropertiesSet();
		Session session = factory.getObject().openSession();
		Transaction tx = session.beginTransaction();
		for  ( int i=1; i<=200000; i++ ) {
			T t = new T();
			//t.setId(i);
		    t.setName("name-"+i);
		    session.save(t);
		  //手动控制强刷，一般取值<=hibernate.jdbc.batch_size，大于没意义
		    if ( i % 5000 == 0 && i != 0) {
		    	session.flush();
		        session.clear();
		    }
		    if ( i % 50000 == 0 && i != 0) {
		    	session.flush();
		        session.clear();
		        tx.commit();
		        tx = session.beginTransaction();
		    }
		}
		
    	session.flush();
        session.clear();
        tx.commit();
		session.close();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
	}

	@Test
	public void testBatchJdbc() throws Exception {
		long start = System.currentTimeMillis();
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:3306/test?useSSL=true&rewriteBatchedStatements=true", "root", "1234");
		connection.setAutoCommit(false);
		PreparedStatement cmd = connection.prepareStatement("insert into `t` (`name`,`id`) values(?,?)");
		for (int i = 1; i <= 200000; i++) {
			cmd.setString(1, "name_" + i);
			cmd.setInt(2, i);
			cmd.addBatch();
			// 一次想insert 5000条
			if (i % 5000 == 0 && i != 0) {
				cmd.executeBatch();
				cmd.clearBatch();
			}
			//每20000条提交一次事物
			if (i % 20000 == 0 && i != 0) {
				cmd.executeBatch();
				cmd.clearBatch();
				connection.commit();
			}
		}

		cmd.executeBatch();
		cmd.clearBatch();
		connection.commit();
		cmd.close();
		connection.close();
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
