package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;

public class AdminAPI{
    private final AdminService adminService=new AdminService();
    public String createStudent(String username,String password,String rollNo,String program,int year){
        return adminService.createStudent(username,password,rollNo,program,year);
    }
    public String createInstructor(String username,String password,String department){
        return adminService.createInstructor(username,password,department);
    }
    public String createAdmin(String username,String password){
        return adminService.createAdminAccount(username,password);
    }
    public String addCourse(String code,String title,int credits){
        return adminService.addCourse(code,title,credits);
    }
    public String updateCourse(Course c){
        return adminService.updateCourse(c);
    }
    public String addSection(long courseId,long instructorId,String dayTime,String room,int capacity,String semester,int year){
        return adminService.addSection(courseId,instructorId,dayTime,room,capacity,semester,year);
    }
    public String updateSection(Section s){
        return adminService.updateSection(s);
    }
    public String deleteUser(String username){
        return adminService.deleteUser(username);
    }
    public String deleteCourse(long courseId){
        return adminService.deleteCourse(courseId);
    }
    public String deleteSection(long sectionId){
        return adminService.deleteSection(sectionId);
    }
    public String setMaintenanceMode(boolean mode){
        return adminService.setMaintenanceMode(mode);
    }
    public boolean isMaintenanceMode(){
        return adminService.isMaintenanceMode();
    }
}