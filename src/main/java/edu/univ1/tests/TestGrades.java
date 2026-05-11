package edu.univ1.tests;
import edu.univ1.service.GradeService;
public class TestGrades {
    public static void main(String[] args) {
        GradeService gs = new GradeService();
        System.out.println(gs.addGrade(1, "Midterm", 85, 100, 40));
        System.out.println(gs.updateGrade(1, 1, "Midterm", 90, 100, 40));
        System.out.println(gs.getGradesForEnrollment(1));
    }
}