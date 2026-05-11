package edu.univ1.tests;
import edu.univ1.service.AdminService;
import edu.univ1.service.EnrollmentService;
public class TestMaintenance {
    public static void main(String[] args) {
        AdminService admin = new AdminService();
        EnrollmentService enroll = new EnrollmentService();
        System.out.println(admin.setMaintenanceMode(true));
        System.out.println(enroll.register(2, 1)); // should say maintenance message
        System.out.println(admin.setMaintenanceMode(false));
        System.out.println(enroll.register(2, 1));
    }
}