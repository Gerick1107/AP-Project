package edu.univ1.data;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;

public class SettingsDAO{
    private final DataSource dataSource;
    public SettingsDAO(){
        this.dataSource=ERPDataSource.getDataSource();
    }
    public String getValue(String key){
        String sql="SELECT settings_value FROM settings WHERE settings_key=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,key);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getString("settings_value");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean setValue(String key,String value){
        String sql=
                "INSERT INTO settings(settings_key, settings_value) VALUES (?, ?) "+
                        "ON DUPLICATE KEY UPDATE settings_value=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,key);
            stmt.setString(2,value);
            stmt.setString(3,value);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteValue(String key){
        String sql="DELETE FROM settings WHERE settings_key=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,key);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public LocalDate getDateValue(String key){
        String value=getValue(key);
        if(value==null||value.isBlank()){
            return null;
        }
        return LocalDate.parse(value);
    }
    public boolean setDateValue(String key,LocalDate date){
        if(date==null){
            return deleteValue(key);
        }
        return setValue(key,date.toString());
    }
    public boolean getMaintenanceMode(){
        String value=getValue("maintenance");
        return "true".equalsIgnoreCase(value);
    }
    public boolean setMaintenanceMode(boolean enabled){
        return setValue("maintenance", enabled ? "true" : "false");
    }
}