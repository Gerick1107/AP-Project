package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.util.List;
public class CatalogService{
    private final CourseDAO courseDAO=new CourseDAO();
    private final SectionDAO sectionDAO=new SectionDAO();
    private final SectionViewAdapter sectionViewAdapter=new SectionViewAdapter();
    public List<Course> getAllCourses(){
        return courseDAO.getAllCourses();
    }
    public List<Section> getSectionsForCourse(long courseId){
        return sectionViewAdapter.enrich(sectionDAO.getByCourse(courseId));
    }
    public List<Section> getInstructorSections(long instructorId){
        return sectionViewAdapter.enrich(sectionDAO.getByInstructor(instructorId));
    }
    public List<Section> getAllSections(){
        return sectionViewAdapter.enrich(sectionDAO.getAll());
    }
    public List<Course> searchCourses(String query){
        return courseDAO.getAllCourses().stream()
                .filter(c -> c.getCode().toLowerCase().contains(query.toLowerCase())
                        || c.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}