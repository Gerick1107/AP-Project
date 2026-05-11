package edu.univ1.tests;
import edu.univ1.service.AdminService;
import edu.univ1.service.EnrollmentService;
public class TestEnrollment {
    public static void main(String[] args) {
        AdminService admin = new AdminService();
        EnrollmentService enroll = new EnrollmentService();
        System.out.println(admin.createStudent("stud1", "123", "0", "CSE", 1));
        System.out.println(enroll.register(2, 1)); // studentId = 2, sectionId = 1
        System.out.println(enroll.register(2, 1));
        admin.addSection(
                1,
                1,
                "Fri 9-10am",
                "C12",
                30,
                "3",
                2
        );
        System.out.println(enroll.register(2, 2));
    }
}
