package zl.example;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
@EnableTransactionManagement(proxyTargetClass=true) // 作用同于<tx:annotation-driven>
public class HibernateSessionFactory {
	
	//@Bean(name="hikariDataSource",destroyMethod="close")
	public DataSource hikariDataSource() {
	    HikariDataSource dataSource = new HikariDataSource();
	    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	  // dataSource.setDataSourceProperties( hikariCPConfig.getHikariDatasource() );
	    dataSource.setUsername("root");
	    dataSource.setPassword("1234");
	    dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=true&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true");
	    dataSource.addDataSourceProperty("dataSource.cachePrepStmts", "true");
	    dataSource.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
	    dataSource.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
	    dataSource.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
	    dataSource.setMaximumPoolSize(80);
	    dataSource.setConnectionTimeout(2000);
	    dataSource.setMinimumIdle(30);
	    //dataSource.setMetricRegistry(metricRegistry);
	    dataSource.setConnectionTestQuery("SELECT 1;");
	    SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
	    loggingListener.setQueryLogEntryCreator(new DefaultQueryLogEntryCreator());
	    return ProxyDataSourceBuilder
	    .create(dataSource)
	    .name("slawek")
	    .listener(loggingListener)
	    .build();
	}
	
/*	
	public DataSource dataSource(DataSource dataSource) {
	    SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
	    loggingListener.setQueryLogEntryCreator(new DefaultQueryLogEntryCreator());
	    return ProxyDataSourceBuilder
	        .create(dataSource)
	        .name("zl_datasource")
	        .listener(loggingListener)
	        .build();
	}*/
	@Bean(name = "sessionFactory")
	public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan("zl.example");
		Properties hibernateProperties =  new Properties();
		hibernateProperties.setProperty("hibernate.jdbc.batch_size", "5000");
		hibernateProperties.setProperty("hibernate.jdbc.fetch_size", "3");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create");
		hibernateProperties.setProperty("hibernate.show_sql", "false");
		hibernateProperties.setProperty("hibernate.format_sql", "false");
		hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "true");
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		
		//hibernateProperties.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		//hibernateProperties.setProperty("hibernate.connection.url", "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=true&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true");
//		hibernateProperties.setProperty("hibernate.connection.url", "jdbc:mysql://10.9.108.125:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=true&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true");
		//hibernateProperties.setProperty("hibernate.connection.username", "root");
		//hibernateProperties.setProperty("hibernate.connection.password", "SendCloudappl1!@#");
	//	hibernateProperties.setProperty("hibernate.connection.password", "1234");
		sessionFactory.setHibernateProperties(hibernateProperties);
		sessionFactory.setImplicitNamingStrategy(new SpringImplicitNamingStrategy());
		sessionFactory.setPhysicalNamingStrategy(new SpringPhysicalNamingStrategy());
		return sessionFactory;
	}
}