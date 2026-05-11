package edu.univ1.data;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class AuthDataSource{
    private static HikariDataSource dataSource;
    static{
        HikariConfig config=new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.AUTH_DB_URL);
        config.setUsername(DatabaseConfig.AUTH_DB_USER);
        config.setPassword(DatabaseConfig.AUTH_DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        dataSource=new HikariDataSource(config);
    }
    public static DataSource getDataSource(){
        return dataSource;
    }
}