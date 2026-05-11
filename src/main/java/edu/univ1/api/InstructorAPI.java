package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.util.List;

public class InstructorAPI{
    private final InstructorService instructorService=new InstructorService();
    public Instructor getProfile(long userId){
        return instructorService.getProfile(userId);
    }
    public List<Section> getInstructorSections(long instructorId){
        return instructorService.getInstructorSections(instructorId);
    }
    public List<Student> getStudentsInSection(long sectionId){
        return instructorService.getStudentsInSection(sectionId);
    }
    public String updateProfile(Instructor instructor){
        return instructorService.updateProfile(instructor);
    }
}