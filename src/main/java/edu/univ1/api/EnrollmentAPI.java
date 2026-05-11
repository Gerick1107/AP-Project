package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.util.List;

public class EnrollmentAPI{
    private final EnrollmentService enrollmentService=new EnrollmentService();
    public String register(long studentId,long sectionId){
        return enrollmentService.register(studentId,sectionId);
    }
    public String drop(long studentId,long sectionId){
        return enrollmentService.drop(studentId,sectionId);
    }
    public List<Section> getSchedule(long studentId){
        return enrollmentService.getStudentSchedule(studentId);
    }
}