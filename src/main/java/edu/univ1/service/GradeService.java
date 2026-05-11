package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradeService{
    private final GradeDAO gradeDAO=new GradeDAO();
    private final EnrollmentDAO enrollmentDAO=new EnrollmentDAO();
    private final SettingsDAO settingsDAO=new SettingsDAO();
    public String addGrade(long enrollmentId,String component,double score,double maxScore,double weight){
        if(settingsDAO.getMaintenanceMode()){
            return "Grade entry disabled (Maintenance Mode).";
        }
        Grade g=new Grade();
        g.setEnrollmentId(enrollmentId);
        g.setComponent(component);
        g.setScore(score);
        g.setMaxScore(maxScore);
        g.setWeight(weight);
        g.setFinalGrade(null);
        long id=gradeDAO.insertGrade(g);
        if(id>0){
            recomputeFinalGrade(enrollmentId);
            return "Component saved and final score recalculated.";
        }
        return "Failed to add grade.";
    }
    public String updateGrade(long enrollmentId,long gradeId,String component,double score,double maxScore,double weight){
        if(settingsDAO.getMaintenanceMode()){
            return "Grade updates disabled (Maintenance Mode).";
        }
        Grade g=new Grade(gradeId,enrollmentId,component,score,maxScore,weight,null);
        boolean ok=gradeDAO.updateGrade(g);
        if(ok){
            recomputeFinalGrade(enrollmentId);
            return "Component updated and final score recalculated.";
        }
        return "Failed to update grade.";
    }
    public List<Grade> getGradesForEnrollment(long enrollmentId){
        return gradeDAO.getGradesByEnrollment(enrollmentId);
    }
    public List<Grade> getGradesForSection(long sectionId){
        return gradeDAO.getGradesBySection(sectionId);
    }
    public String deleteGrade(long enrollmentId,long gradeId){
        if(settingsDAO.getMaintenanceMode()){
            return "Grade deletion disabled (Maintenance Mode).";
        }
        boolean ok=gradeDAO.deleteGrade(gradeId);
        if(ok){
            recomputeFinalGrade(enrollmentId);
            return "Component deleted and final score recalculated.";
        }
        return "Failed to delete grade.";
    }
    public List<Grade> getGradesByComponentAndSection(long sectionId,String componentName){
        return gradeDAO.getGradesByComponentAndSection(sectionId,componentName);
    }
    public String updateComponentWeightAndMaxScoreForSection(long sectionId,String componentName,double maxScore,double weight){
        if(settingsDAO.getMaintenanceMode()){
            return "Grade updates disabled (Maintenance Mode).";
        }
        boolean ok=gradeDAO.updateGradeWeightAndMaxScore(sectionId,componentName,maxScore,weight);
        if(ok){
            List<Enrollment> enrollments=enrollmentDAO.getBySection(sectionId);
            for(Enrollment e:enrollments){
                recomputeFinalGrade(e.getEnrollmentId());
            }
            return "Component weight and max score updated for all students.";
        }
        return "Failed to update component.";
    }
    public String deleteComponentForSection(long sectionId,String componentName){
        if(settingsDAO.getMaintenanceMode()){
            return "Grade deletion disabled (Maintenance Mode).";
        }
        boolean ok=gradeDAO.deleteGradesByComponentAndSection(sectionId,componentName);
        if(ok){
            List<Enrollment> enrollments=enrollmentDAO.getBySection(sectionId);
            for(Enrollment e:enrollments){
                recomputeFinalGrade(e.getEnrollmentId());
            }
            return "Component deleted for all students.";
        }
        return "Failed to delete component.";
    }
    public GradeStats getSectionStats(long sectionId){
        List<Enrollment> enrollments=enrollmentDAO.getBySection(sectionId);
        List<Double> scores=new ArrayList<>();
        for(Enrollment e:enrollments){
            if(e.getFinalScore()>0){
                scores.add(e.getFinalScore());
            }
        }
        if(scores.isEmpty()){
            return null;
        }
        Collections.sort(scores);
        double sum=0;
        for(double s:scores) sum+=s;
        double avg=sum/scores.size();
        double median;
        int n=scores.size();
        if (n%2==0){
            median=(scores.get(n/2-1)+scores.get(n/2))/2.0;
        }else{
            median=scores.get(n/2);
        }
        double min=scores.get(0);
        double max=scores.get(n-1);
        return new GradeStats(avg,median,avg,min,max);
    }
    private void recomputeFinalGrade(long enrollmentId){
        List<Grade> components=gradeDAO.getGradesByEnrollment(enrollmentId);
        double total=0;
        double weightSum=0;
        for(Grade c:components){
            if(c.getMaxScore()<=0||c.getWeight()<=0){
                continue;
            }
            double percent=c.getScore()/c.getMaxScore();
            total+=percent*c.getWeight();
            weightSum+=c.getWeight();
        }
        if(weightSum<=0){
            enrollmentDAO.updateFinalGrade(enrollmentId,0,null,0);
            return;
        }
        double normalized=weightSum==100?total:(total/weightSum)*100;
        normalized=Math.max(0,Math.min(100,normalized));
        GradeEvaluation evaluation=GradeScale.evaluate(normalized);
        enrollmentDAO.updateFinalGrade(enrollmentId,normalized,evaluation.getLetter(),evaluation.getGradePoint());
    }
}