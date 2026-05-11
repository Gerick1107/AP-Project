package edu.univ1.tests;
import edu.univ1.auth.LoginService;
import edu.univ1.data.UserAuthDAO;
import edu.univ1.auth.PasswordHasher;
import edu.univ1.domain.LoginResult;
public class TestAuth {
    public static void main(String[] args) {
        UserAuthDAO dao = new UserAuthDAO();
        long id = dao.insertUser("admin_test", "ADMIN", PasswordHasher.hash("pass123"));
        System.out.println("User ID = " + id);
        LoginService login = new LoginService();
        LoginResult u = login.login("admin_test", "pass123");
        System.out.println(u != null ? "LOGIN OK" : "LOGIN FAILED");
    }
}