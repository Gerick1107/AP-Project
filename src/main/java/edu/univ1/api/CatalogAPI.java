package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.util.List;

public class CatalogAPI{
    private final CatalogService catalogService=new CatalogService();
    public List<Course> getAllCourses(){
        return catalogService.getAllCourses();
    }
    public List<Section> getSectionsForCourse(long courseId){
        return catalogService.getSectionsForCourse(courseId);
    }
    public List<Section> getInstructorSections(long instructorId){
        return catalogService.getInstructorSections(instructorId);
    }
    public List<Section> getAllSections(){
        return catalogService.getAllSections();
    }
    public List<Course> searchCourses(String query){
        return catalogService.searchCourses(query);
    }
}