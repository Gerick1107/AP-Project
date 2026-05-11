package edu.univ1.data;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDAO{
    private final DataSource dataSource;
    public CourseDAO(){
        this.dataSource=ERPDataSource.getDataSource();
    }
    public long insertCourse(Course course){
        String sql="INSERT INTO courses(code, title, credits) VALUES (?, ?, ?)";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1,course.getCode());
            stmt.setString(2,course.getTitle());
            stmt.setInt(3,course.getCredits());
            int rows=stmt.executeUpdate();
            if(rows>0){
                ResultSet keys=stmt.getGeneratedKeys();
                if(keys.next()){
                    return keys.getLong(1);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    public Course getById(long courseId){
        String sql="SELECT course_id, code, title, credits FROM courses WHERE course_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,courseId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return new Course(
                        rs.getLong("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Map<Long,Course>getCoursesByIds(Collection<Long>courseIds){
        if(courseIds==null||courseIds.isEmpty()){
            return Collections.emptyMap();
        }
        String placeholders=String.join(",",Collections.nCopies(courseIds.size(),"?"));
        String sql="SELECT course_id, code, title, credits FROM courses WHERE course_id IN (" + placeholders + ")";
        Map<Long,Course>result=new HashMap<>();
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            int idx=1;
            for(Long id:courseIds){
                stmt.setLong(idx++,id);
            }
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                Course c=new Course(
                        rs.getLong("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                );
                result.put(c.getCourseId(), c);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public List<Course>getAllCourses(){
        List<Course>list=new ArrayList<>();
        String sql="SELECT course_id, code, title, credits FROM courses";
        try(Connection conn=dataSource.getConnection();
             PreparedStatement stmt=conn.prepareStatement(sql);
             ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                Course c=new Course(
                        rs.getLong("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                );
                list.add(c);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public boolean updateCourse(Course course){
        String sql="UPDATE courses SET code = ?, title = ?, credits = ? WHERE course_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,course.getCode());
            stmt.setString(2,course.getTitle());
            stmt.setInt(3, course.getCredits());
            stmt.setLong(4,course.getCourseId());
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteCourse(long courseId){
        String sql="DELETE FROM courses WHERE course_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,courseId);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}