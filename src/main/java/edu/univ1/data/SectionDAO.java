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
import java.util.List;

public class SectionDAO{
    private final DataSource dataSource;
    public SectionDAO(){
        this.dataSource=ERPDataSource.getDataSource();
    }
    public long insertSection(Section section){
        String sql="INSERT INTO sections(course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            stmt.setLong(1,section.getCourseId());
            stmt.setLong(2,section.getInstructorId());
            stmt.setString(3,section.getDayTime());
            stmt.setString(4,section.getRoom());
            stmt.setInt(5,section.getCapacity());
            stmt.setString(6,section.getSemester());
            stmt.setInt(7,section.getYear());
            int rows=stmt.executeUpdate();
            if (rows>0){
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
    public Section getById(long sectionId){
        String sql="SELECT * FROM sections WHERE section_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,sectionId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return mapRow(rs);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public List<Section>getByCourse(long courseId){
        List<Section> list=new ArrayList<>();
        String sql="SELECT * FROM sections WHERE course_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,courseId);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public List<Section>getByInstructor(long instructorId){
        List<Section> list=new ArrayList<>();
        String sql="SELECT * FROM sections WHERE instructor_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,instructorId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public List<Section>getAll(){
        List<Section> list=new ArrayList<>();
        String sql="SELECT * FROM sections";
        try(Connection conn=dataSource.getConnection();
             PreparedStatement stmt=conn.prepareStatement(sql);
             ResultSet rs=stmt.executeQuery()) {
            while(rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public boolean updateSection(Section section){
        String sql="UPDATE sections SET course_id=?, instructor_id=?, day_time=?, room=?, capacity=?, semester=?, year=? " +
                "WHERE section_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,section.getCourseId());
            stmt.setLong(2,section.getInstructorId());
            stmt.setString(3,section.getDayTime());
            stmt.setString(4,section.getRoom());
            stmt.setInt(5,section.getCapacity());
            stmt.setString(6,section.getSemester());
            stmt.setInt(7,section.getYear());
            stmt.setLong(8,section.getSectionId());
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteSection(long sectionId){
        String sql="DELETE FROM sections WHERE section_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,sectionId);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private Section mapRow(ResultSet rs) throws SQLException{
        return new Section(
                rs.getLong("section_id"),
                rs.getLong("course_id"),
                rs.getLong("instructor_id"),
                rs.getString("day_time"),
                rs.getString("room"),
                rs.getInt("capacity"),
                rs.getString("semester"),
                rs.getInt("year")
        );
    }
}