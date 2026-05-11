package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;

public class AdminService{
    private final UserAuthDAO userAuthDAO=new UserAuthDAO();
    private final StudentDAO studentDAO=new StudentDAO();
    private final InstructorDAO instructorDAO=new InstructorDAO();
    private final CourseDAO courseDAO=new CourseDAO();
    private final SectionDAO sectionDAO=new SectionDAO();
    private final SettingsDAO settingsDAO=new SettingsDAO();
    private final EnrollmentDAO enrollmentDAO=new EnrollmentDAO();
    private final NotificationService notificationService=new NotificationService();
    public String createStudent(String username,String password,String rollNo,String program,int year){
        long userId=userAuthDAO.insertUser(username,"STUDENT",PasswordHasher.hash(password));
        if(userId<=0){
            return "Failed to create student user.";
        }
        Student s=new Student(userId,rollNo,program,year);
        boolean ok=studentDAO.insertStudent(s);
        return ok ? "Student created successfully." : "Failed to create student profile.";
    }
    public String createInstructor(String username,String password,String department){
        long userId=userAuthDAO.insertUser(username,"INSTRUCTOR",PasswordHasher.hash(password));
        if(userId<=0){
            return "Failed to create instructor user.";
        }
        Instructor ins=new Instructor(userId,department);
        boolean ok=instructorDAO.insertInstructor(ins);
        return ok ? "Instructor created successfully." : "Failed to create instructor profile.";
    }
    public String createAdminAccount(String username,String password){
        long userId=userAuthDAO.insertUser(username,"ADMIN",PasswordHasher.hash(password));
        return (userId>0) ? "Admin user created." : "Failed to create admin user.";
    }
    public String addCourse(String code,String title,int credits){
        Course c=new Course(0,code,title,credits);
        long id=courseDAO.insertCourse(c);
        return (id>0) ? "Course created." : "Failed to create course.";
    }
    public String updateCourse(Course c){
        boolean ok=courseDAO.updateCourse(c);
        return ok ? "Course updated." : "Failed to update course.";
    }
    public String addSection(long courseId,long instructorId,String dayTime,String room,int capacity,String semester,int year){
        Section s=new Section(0,courseId,instructorId,dayTime,room,capacity,semester,year);
        long id=sectionDAO.insertSection(s);
        return (id>0) ? "Section created." : "Failed to create section.";
    }
    public String updateSection(Section s){
        boolean ok=sectionDAO.updateSection(s);
        return ok ? "Section updated." : "Failed to update section.";
    }
    public String setMaintenanceMode(boolean enabled){
        boolean current=settingsDAO.getMaintenanceMode();
        boolean ok=settingsDAO.setMaintenanceMode(enabled);
        if(ok && current!=enabled){
            notificationService.publishMaintenanceStatus(enabled);
        }
        return ok ? "Maintenance mode updated." : "Failed to update maintenance mode.";
    }
    public boolean isMaintenanceMode(){
        return settingsDAO.getMaintenanceMode();
    }
    public String deleteUser(String username){
        User user=userAuthDAO.findByUsername(username);
        if(user==null){
            return "User not found.";
        }
        if("ADMIN".equalsIgnoreCase(user.getRole())){
            return "Cannot delete administrator accounts.";
        }
        long userId = user.getUserId();
        switch(user.getRole()){
            case "STUDENT"->{
                if(!enrollmentDAO.getByStudent(userId).isEmpty()){
                    return "Student still has active enrollments.";
                }
                if(!studentDAO.deleteStudent(userId)){
                    return "Failed to delete student profile.";
                }
            }
            case "INSTRUCTOR"->{
                if(!sectionDAO.getByInstructor(userId).isEmpty()){
                    return "Instructor still teaches sections.";
                }
                if(!instructorDAO.deleteInstructor(userId)){
                    return "Failed to delete instructor profile.";
                }
            }
            default->{}
        }
        boolean removedAuth=userAuthDAO.deleteUser(userId);
        return removedAuth ? "User deleted." : "Failed to delete authentication record.";
    }
    public String deleteCourse(long courseId){
        if(!sectionDAO.getByCourse(courseId).isEmpty()){
            return "Cannot delete course with existing sections.";
        }
        boolean ok = courseDAO.deleteCourse(courseId);
        return ok ? "Course deleted." : "Failed to delete course.";
    }
    public String deleteSection(long sectionId){
        if(enrollmentDAO.sectionHasStudents(sectionId)){
            return "Cannot delete section with enrolled students.";
        }
        boolean ok=sectionDAO.deleteSection(sectionId);
        return ok ? "Section deleted." : "Failed to delete section.";
    }
}