package edu.univ1.data;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.util.List;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.sql.*;

public class InstructorDAO{
    private final DataSource dataSource;
    public InstructorDAO(){
        this.dataSource=ERPDataSource.getDataSource();
    }
    public boolean insertInstructor(Instructor instructor){
        String sql="INSERT INTO instructors(user_id, department) VALUES (?, ?)";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,instructor.getUserId());
            stmt.setString(2,instructor.getDepartment());
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public Instructor getByUserId(long userId){
        String sql="SELECT * FROM instructors WHERE user_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,userId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return mapRow(rs);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean updateInstructor(Instructor instructor){
        String sql="UPDATE instructors SET department=? WHERE user_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,instructor.getDepartment());
            stmt.setLong(2,instructor.getUserId());
            return stmt.executeUpdate()>0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private Instructor mapRow(ResultSet rs) throws SQLException{
        return new Instructor(
                rs.getLong("user_id"),
                rs.getString("department")
        );
    }
    public List<Instructor>getAllInstructors(){
        List<Instructor> list=new ArrayList<>();
        String sql="SELECT * FROM instructors";
        try(Connection conn=dataSource.getConnection();
             PreparedStatement stmt=conn.prepareStatement(sql);
             ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                list.add(new Instructor(
                        rs.getLong("user_id"),
                        rs.getString("department")
                ));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public boolean deleteInstructor(long userId){
        String sql="DELETE FROM instructors WHERE user_id=?";
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