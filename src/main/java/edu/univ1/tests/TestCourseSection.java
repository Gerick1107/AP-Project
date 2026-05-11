package edu.univ1.tests;
import edu.univ1.service.AdminService;
public class TestCourseSection {
    public static void main(String[] args) {
        AdminService admin = new AdminService();
        System.out.println(admin.addCourse("CSE0", "Intro To Prog", 4));
        System.out.println(admin.addSection(
                1,
                 1, // instructorId (must exist)
                "Fri 9-10am",
                "C11",
                30,
                "3",
                2
        ));
    }
}