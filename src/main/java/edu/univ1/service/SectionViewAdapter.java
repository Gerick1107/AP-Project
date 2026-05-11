package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SectionViewAdapter{
    private final CourseDAO courseDAO=new CourseDAO();
    private final DirectoryService directoryService=new DirectoryService();
    public List<Section> enrich(List<Section> sections){
        if(sections==null||sections.isEmpty()){
            return sections;
        }
        Map<Long,Course> courseMap=courseDAO.getCoursesByIds(distinctCourseIds(sections));
        Map<Long,String> instructorNames=directoryService.getUsernames(distinctInstructorIds(sections));
        for(Section section:sections){
            applyCourse(section,courseMap.get(section.getCourseId()));
            section.setInstructorName(instructorNames.get(section.getInstructorId()));
        }
        return sections;
    }
    private void applyCourse(Section section,Course course){
        if(course!=null){
            section.setCourseCode(course.getCode());
            section.setCourseTitle(course.getTitle());
        }
    }
    private Collection<Long> distinctCourseIds(List<Section> sections){
        return sections.stream()
                .map(Section::getCourseId)
                .collect(Collectors.toSet());
    }
    private List<Long> distinctInstructorIds(List<Section> sections){
        Set<Long> ids=sections.stream()
                .map(Section::getInstructorId)
                .collect(Collectors.toSet());
        return new java.util.ArrayList<>(ids);
    }
}