package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentService{
    private final EnrollmentDAO enrollmentDAO=new EnrollmentDAO();
    private final SectionDAO sectionDAO=new SectionDAO();
    private final SettingsDAO settingsDAO=new SettingsDAO();
    private final SectionViewAdapter sectionViewAdapter=new SectionViewAdapter();
    public String register(long studentId,long sectionId){
        if(settingsDAO.getMaintenanceMode()){
            return "Registration unavailable (Maintenance Mode).";
        }
        LocalDate registerDeadline=settingsDAO.getDateValue("register_deadline");
        if(isPastDeadline(registerDeadline)){
            return "Registration deadline has passed.";
        }
        if(enrollmentDAO.isEnrolled(studentId,sectionId)){
            return "Already enrolled in this section.";
        }
        Section section=sectionDAO.getById(sectionId);
        if(section==null){
            return "Section does not exist.";
        }
        if(enrollmentDAO.isEnrolledInCourse(studentId, section.getCourseId())){
            return "Already registered for another section of this course.";
        }
        int enrolledCount=enrollmentDAO.getBySection(sectionId).size();
        if(enrolledCount>=section.getCapacity()){
            return "Section is full.";
        }
        if(hasScheduleConflict(studentId,section)){
            return "Schedule conflict.";
        }
        long id=enrollmentDAO.enroll(studentId, sectionId);
        return (id>0) ? "Enrolled successfully." : "Enrollment failed.";
    }
    public String drop(long studentId,long sectionId){
        if(settingsDAO.getMaintenanceMode()){
            return "Dropping unavailable (Maintenance Mode).";
        }
        LocalDate dropDeadline=settingsDAO.getDateValue("drop_deadline");
        if(isPastDeadline(dropDeadline)){
            return "Drop deadline has passed.";
        }
        boolean ok=enrollmentDAO.drop(studentId,sectionId);
        return ok ? "Dropped successfully." : "Drop failed.";
    }
    public List<Section> getStudentSchedule(long studentId){
        List<Section> schedule=new ArrayList<>();
        List<Enrollment> enrolled=enrollmentDAO.getByStudent(studentId);
        for(Enrollment e:enrolled){
            Section s=sectionDAO.getById(e.getSectionId());
            if(s!=null){
                schedule.add(s);
            }
        }
        return sectionViewAdapter.enrich(schedule);
    }
    private boolean hasScheduleConflict(long studentId,Section newSection){
        List<Section> current=getStudentSchedule(studentId);
        for(Section s:current){
            if(isConflict(s.getDayTime(),newSection.getDayTime())){
                return true;
            }
        }
        return false;
    }
    private boolean isConflict(String dt1,String dt2){
        String[] p1=dt1.split(" ");
        String[] p2=dt2.split(" ");
        if(!p1[0].equalsIgnoreCase(p2[0])){
            return false;
        }
        int start1=Integer.parseInt(p1[1].split("-")[0]);
        int end1=Integer.parseInt(p1[1].split("-")[1]);
        int start2=Integer.parseInt(p2[1].split("-")[0]);
        int end2=Integer.parseInt(p2[1].split("-")[1]);
        return start1<end2 && start2<end1;
    }
    private boolean isPastDeadline(LocalDate deadline){
        return deadline!=null && LocalDate.now().isAfter(deadline);
    }
}