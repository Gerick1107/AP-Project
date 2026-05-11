package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InstructorService{
    private final InstructorDAO instructorDAO=new InstructorDAO();
    private final SectionDAO sectionDAO=new SectionDAO();
    private final EnrollmentDAO enrollmentDAO=new EnrollmentDAO();
    private final StudentDAO studentDAO=new StudentDAO();
    private final DirectoryService directoryService=new DirectoryService();
    private final SectionViewAdapter sectionViewAdapter=new SectionViewAdapter();
    public Instructor getProfile(long userId){
        return instructorDAO.getByUserId(userId);
    }
    public List<Section> getInstructorSections(long instructorId){
        return sectionViewAdapter.enrich(sectionDAO.getByInstructor(instructorId));
    }
    public String updateProfile(Instructor instructor){
        boolean ok=instructorDAO.updateInstructor(instructor);
        return ok ? "Profile updated successfully." : "Failed to update profile.";
    }
    public List<Student> getStudentsInSection(long sectionId){
        List<Student> list=new ArrayList<>();
        List<Enrollment> enrollments=enrollmentDAO.getBySection(sectionId);
        for(Enrollment e:enrollments){
            Student s=studentDAO.getByUserId(e.getStudentId());
            if(s!=null){
                s.setEnrollmentId(e.getEnrollmentId());
                list.add(s);
            }
        }
        List<Long> studentIds=list.stream().map(Student::getUserId).collect(Collectors.toList());
        Map<Long,String> names=directoryService.getUsernames(studentIds);
        for(Student s:list){
            s.setDisplayName(names.get(s.getUserId()));
        }
        return list;
    }
}