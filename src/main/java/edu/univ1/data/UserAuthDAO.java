package edu.univ1.data;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAuthDAO{
    private final DataSource dataSource;
    public UserAuthDAO(){
        this.dataSource=AuthDataSource.getDataSource();
    }
    public User findByUsername(String username){
        String sql="SELECT user_id, username, role, status FROM users_auth WHERE username = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,username);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                User u=new User();
                u.setUserId(rs.getLong("user_id"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setActive("ACTIVE".equals(rs.getString("status")));
                return u;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String getPasswordHash(long userId){
        String sql="SELECT password_hash FROM users_auth WHERE user_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,userId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getString("password_hash");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public long insertUser(String username,String role,String passwordHash){
        String sql="INSERT INTO users_auth(username, role, password_hash, status) VALUES (?, ?, ?, 'ACTIVE')";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1,username);
            stmt.setString(2,role);
            stmt.setString(3,passwordHash);
            int rows=stmt.executeUpdate();
            if(rows>0){
                ResultSet keys=stmt.getGeneratedKeys();
                if(keys.next()){
                    return keys.getLong(1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    public void updateLastLogin(long userId){
        String sql="UPDATE users_auth SET last_login = NOW() WHERE user_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,userId);
            stmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public boolean updatePassword(long userId,String newHash){
        String sql="UPDATE users_auth SET password_hash=? WHERE user_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,newHash);
            stmt.setLong(2,userId);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public String getUsernameById(long userId){
        String sql="SELECT username FROM users_auth WHERE user_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,userId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getString("username");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Map<Long,String>getUsernamesByIds(List<Long>userIds){
        if (userIds==null||userIds.isEmpty()){
            return Collections.emptyMap();
        }
        String placeholders=String.join(",",Collections.nCopies(userIds.size(),"?"));
        String sql="SELECT user_id, username FROM users_auth WHERE user_id IN (" + placeholders + ")";
        Map<Long,String> result=new HashMap<>();
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            for(int i=0;i<userIds.size();i++){
                stmt.setLong(i+1,userIds.get(i));
            }
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                result.put(rs.getLong("user_id"),rs.getString("username"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public String getRoleByUserId(long userId){
        String sql="SELECT role FROM users_auth WHERE user_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,userId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getString("role");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean deleteUser(long userId){
        String sql="DELETE FROM users_auth WHERE user_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,userId);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}