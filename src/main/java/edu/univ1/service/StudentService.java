package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.util.ArrayList;
import java.util.List;

public class StudentService{
    private final StudentDAO studentDAO=new StudentDAO();
    private final EnrollmentDAO enrollmentDAO=new EnrollmentDAO();
    private final SectionDAO sectionDAO=new SectionDAO();
    private final CourseDAO courseDAO=new CourseDAO();
    private final SectionViewAdapter sectionViewAdapter=new SectionViewAdapter();
    public Student getProfile(long userId) {
        return studentDAO.getByUserId(userId);
    }
    public List<Section> getTimetable(long studentId){
        List<Section> result=new ArrayList<>();
        List<Enrollment> enrollments=enrollmentDAO.getByStudent(studentId);
        for(Enrollment e:enrollments){
            Section s=sectionDAO.getById(e.getSectionId());
            if(s!=null){
                result.add(s);
            }
        }
        return sectionViewAdapter.enrich(result);
    }
    public List<TranscriptEntry> getTranscript(long studentId){
        List<TranscriptEntry> transcript=new ArrayList<>();
        List<Enrollment> enrollments=enrollmentDAO.getByStudent(studentId);
        for(Enrollment e:enrollments){
            Section section=sectionDAO.getById(e.getSectionId());
            if(section==null) continue;
            Course course=courseDAO.getById(section.getCourseId());
            String code=course!=null ? course.getCode() : "-";
            String title=course!=null ? course.getTitle() : "Unknown Course";
            int credits=course!=null ? course.getCredits() : 0;
            transcript.add(new TranscriptEntry(
                    e.getEnrollmentId(),
                    section.getCourseId(),
                    code,
                    title,
                    e.getFinalScore(),
                    e.getFinalLetter()==null ? "NA" : e.getFinalLetter(),
                    e.getGradePoint(),
                    credits,
                    section.getSemester(),
                    section.getYear()
            ));
        }
        return transcript;
    }
    public String updateProfile(Student student){
        boolean ok=studentDAO.updateStudent(student);
        return ok ? "Profile updated successfully." : "Failed to update profile.";
    }
    public double calculateGPA(long studentId){
        List<TranscriptEntry> transcript=getTranscript(studentId);
        if (transcript.isEmpty()){
            return 0;
        }
        double totalPoints=0;
        int totalCredits=0;
        for (TranscriptEntry entry:transcript){
            totalPoints+=entry.getGradePoint()* entry.getCredits();
            totalCredits+=entry.getCredits();
        }
        if(totalCredits==0){
            return 0;
        }
        return totalPoints/totalCredits;
    }
}