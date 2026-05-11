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

public class GradeDAO{
    private final DataSource dataSource;
    public GradeDAO(){
        this.dataSource=ERPDataSource.getDataSource();
    }
    public long insertGrade(Grade grade){
        String sql="INSERT INTO grades(enrollment_id, component, score, max_score, weight, final_grade) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            stmt.setLong(1,grade.getEnrollmentId());
            stmt.setString(2,grade.getComponent());
            stmt.setDouble(3,grade.getScore());
            stmt.setDouble(4,grade.getMaxScore());
            stmt.setDouble(5,grade.getWeight());
            stmt.setString(6,grade.getFinalGrade());
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
    public boolean updateGrade(Grade grade){
        String sql="UPDATE grades SET component=?, score=?, max_score=?, weight=?, final_grade=? WHERE grade_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setString(1,grade.getComponent());
            stmt.setDouble(2,grade.getScore());
            stmt.setDouble(3,grade.getMaxScore());
            stmt.setDouble(4,grade.getWeight());
            stmt.setString(5,grade.getFinalGrade());
            stmt.setLong(6,grade.getGradeId());
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public List<Grade>getGradesByEnrollment(long enrollmentId){
        List<Grade>list=new ArrayList<>();
        String sql="SELECT * FROM grades WHERE enrollment_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,enrollmentId);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public List<Grade>getGradesBySection(long sectionId){
        List<Grade> list=new ArrayList<>();
        String sql=
                "SELECT g.* FROM grades g "+
                        "JOIN enrollments e ON g.enrollment_id = e.enrollment_id "+
                        "WHERE e.section_id = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,sectionId);
            ResultSet rs=stmt.executeQuery();
            while (rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public boolean deleteGrade(long gradeId){
        String sql="DELETE FROM grades WHERE grade_id=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,gradeId);
            return stmt.executeUpdate()>0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public List<Grade> getGradesByComponentAndSection(long sectionId,String componentName){
        List<Grade> list=new ArrayList<>();
        String sql="SELECT g.* FROM grades g JOIN enrollments e ON g.enrollment_id = e.enrollment_id WHERE e.section_id = ? AND g.component = ?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,sectionId);
            stmt.setString(2,componentName);
            ResultSet rs=stmt.executeQuery();
            while(rs.next()){
                list.add(mapRow(rs));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public boolean updateGradeWeightAndMaxScore(long sectionId,String componentName,double maxScore,double weight){
        String sql="UPDATE grades g JOIN enrollments e ON g.enrollment_id = e.enrollment_id SET g.max_score=?, g.weight=? WHERE e.section_id=? AND g.component=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setDouble(1,maxScore);
            stmt.setDouble(2,weight);
            stmt.setLong(3,sectionId);
            stmt.setString(4,componentName);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteGradesByComponentAndSection(long sectionId,String componentName){
        String sql="DELETE g FROM grades g JOIN enrollments e ON g.enrollment_id = e.enrollment_id WHERE e.section_id=? AND g.component=?";
        try(Connection conn=dataSource.getConnection();
            PreparedStatement stmt=conn.prepareStatement(sql)){
            stmt.setLong(1,sectionId);
            stmt.setString(2,componentName);
            return stmt.executeUpdate()>0;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private Grade mapRow(ResultSet rs) throws SQLException{
        return new Grade(
                rs.getLong("grade_id"),
                rs.getLong("enrollment_id"),
                rs.getString("component"),
                rs.getDouble("score"),
                rs.getDouble("max_score"),
                rs.getDouble("weight"),
                rs.getString("final_grade")
        );
    }
}