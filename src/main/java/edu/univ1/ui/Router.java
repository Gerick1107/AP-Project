package edu.univ1.ui;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.service.*;
import edu.univ1.ui.admin.*;
import edu.univ1.ui.student.*;
import edu.univ1.ui.instructor.*;

public class Router{
    public static void route(User user){
        String role=user.getRole();
        switch(role){
            case "ADMIN":
                new AdminDashboard(user).setVisible(true);
                break;
            case "STUDENT":
                new StudentDashboard(user).setVisible(true);
                break;
            case "INSTRUCTOR":
                new InstructorDashboard(user).setVisible(true);
                break;
            default:
                throw new IllegalStateException("Unknown role: "+role);
        }
    }
}