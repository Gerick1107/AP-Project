package edu.univ1.auth;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher{
    public static String hash(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }
    public static boolean verify(String password,String hashed){
        if(hashed==null){
            return false;
        }
        return BCrypt.checkpw(password, hashed);
    }
}