package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.util.List;

public class GradeAPI{
    private final GradeService gradeService=new GradeService();
    public String addGrade(long enrollmentId,String component,double score,double maxScore,double weight){
        return gradeService.addGrade(enrollmentId,component,score,maxScore,weight);
    }
    public String updateGrade(long enrollmentId,long gradeId,String component,double score,double maxScore,double weight){
        return gradeService.updateGrade(enrollmentId,gradeId,component,score,maxScore,weight);
    }
    public List<Grade> getGradesForSection(long sectionId){
        return gradeService.getGradesForSection(sectionId);
    }
    public List<Grade> getGradesForEnrollment(long enrollmentId){
        return gradeService.getGradesForEnrollment(enrollmentId);
    }
    public String deleteGrade(long enrollmentId,long gradeId){
        return gradeService.deleteGrade(enrollmentId,gradeId);
    }
    public GradeStats getSectionStats(long sectionId){
        return gradeService.getSectionStats(sectionId);
    }
    public List<Grade> getGradesByComponentAndSection(long sectionId,String componentName){
        return gradeService.getGradesByComponentAndSection(sectionId,componentName);
    }
    public String updateComponentWeightAndMaxScoreForSection(long sectionId,String componentName,double maxScore,double weight){
        return gradeService.updateComponentWeightAndMaxScoreForSection(sectionId,componentName,maxScore,weight);
    }
    public String deleteComponentForSection(long sectionId,String componentName){
        return gradeService.deleteComponentForSection(sectionId,componentName);
    }
}