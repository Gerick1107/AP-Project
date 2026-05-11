package edu.univ1.data;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import javax.sql.DataSource;
import java.sql.*;

public class StudentDAO{
    private final DataSource dataSource;
    public StudentDAO(){
        this.dataSource=ERPDataSource.getDataSource();
    }
    public boolean insertStudent(Student student){
        String sql="INSERT INTO students(user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,student.getUserId());
            stmt.setString(2,student.getRollNo());
            stmt.setString(3,student.getProgram());
            stmt.setInt(4,student.getYear());
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public Student getByUserId(long userId){
        String sql="SELECT * FROM students WHERE user_id = ?";
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
    public boolean updateStudent(Student student){
        String sql="UPDATE students SET roll_no=?, program=?, year=? WHERE user_id=?";

        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,student.getRollNo());
            stmt.setString(2,student.getProgram());
            stmt.setInt(3,student.getYear());
            stmt.setLong(4,student.getUserId());
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private Student mapRow(ResultSet rs) throws SQLException{
        return new Student(
                rs.getLong("user_id"),
                rs.getString("roll_no"),
                rs.getString("program"),
                rs.getInt("year")
        );
    }
    public boolean deleteStudent(long userId){
        String sql="DELETE FROM students WHERE user_id=?";
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