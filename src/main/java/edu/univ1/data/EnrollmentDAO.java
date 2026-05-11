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

public class EnrollmentDAO{
    private final DataSource dataSource;
    public EnrollmentDAO(){
        this.dataSource=ERPDataSource.getDataSource();
    }
    public long enroll(long studentId,long sectionId){
        String sql="INSERT INTO enrollments(student_id, section_id, status, final_score, final_letter, grade_point) VALUES (?, ?, 'ENROLLED', 0, NULL, 0)";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            stmt.setLong(1,studentId);
            stmt.setLong(2,sectionId);
            int rows=stmt.executeUpdate();
            if(rows>0){
                ResultSet keys=stmt.getGeneratedKeys();
                if(keys.next()){
                    return keys.getLong(1);
                }
            }
        }catch(SQLIntegrityConstraintViolationException e){
            System.out.println("Already enrolled or duplicate entry.");
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    public boolean drop(long studentId,long sectionId){
        String sql="UPDATE enrollments SET status='DROPPED' WHERE student_id=? AND section_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,studentId);
            stmt.setLong(2,sectionId);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean isEnrolled(long studentId,long sectionId){
        String sql="SELECT COUNT(*) FROM enrollments WHERE student_id=? AND section_id=? AND status='ENROLLED'";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,studentId);
            stmt.setLong(2,sectionId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1)>0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean isEnrolledInCourse(long studentId,long courseId){
        String sql="SELECT COUNT(*) "+
                "FROM enrollments e JOIN sections s ON e.section_id = s.section_id "+
                "WHERE e.student_id=? AND s.course_id=? AND e.status='ENROLLED'";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,studentId);
            stmt.setLong(2,courseId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1)>0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean sectionHasStudents(long sectionId){
        String sql="SELECT COUNT(*) FROM enrollments WHERE section_id=? AND status='ENROLLED'";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,sectionId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1)>0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateFinalGrade(long enrollmentId,double finalScore,String letter,double gradePoint){
        String sql="UPDATE enrollments SET final_score=?, final_letter=?, grade_point=? WHERE enrollment_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setDouble(1,finalScore);
            stmt.setString(2,letter);
            stmt.setDouble(3,gradePoint);
            stmt.setLong(4,enrollmentId);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public List<Enrollment>getByStudent(long studentId){
        List<Enrollment> list=new ArrayList<>();
        String sql="SELECT * FROM enrollments WHERE student_id=? AND status='ENROLLED'";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,studentId);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public List<Enrollment>getBySection(long sectionId){
        List<Enrollment> list=new ArrayList<>();
        String sql="SELECT * FROM enrollments WHERE section_id=? AND status='ENROLLED'";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,sectionId);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    private Enrollment mapRow(ResultSet rs) throws SQLException{
        Enrollment enrollment=new Enrollment(
                rs.getLong("enrollment_id"),
                rs.getLong("student_id"),
                rs.getLong("section_id"),
                rs.getString("status")
        );
        double finalScore=rs.getDouble("final_score");
        if(!rs.wasNull()){
            enrollment.setFinalScore(finalScore);
        }
        enrollment.setFinalLetter(rs.getString("final_letter"));
        double gradePoint=rs.getDouble("grade_point");
        if(!rs.wasNull()){
            enrollment.setGradePoint(gradePoint);
        }
        return enrollment;
    }
}