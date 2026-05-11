package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.util.List;

public class StudentAPI{
    private final StudentService studentService=new StudentService();
    public Student getProfile(long userId){
        return studentService.getProfile(userId);
    }
    public List<Section> getTimetable(long studentId){
        return studentService.getTimetable(studentId);
    }
    public List<TranscriptEntry> getTranscript(long studentId){
        return studentService.getTranscript(studentId);
    }
    public double calculateGPA(long studentId){
        return studentService.calculateGPA(studentId);
    }
    public String updateProfile(Student student){
        return studentService.updateProfile(student);
    }
}