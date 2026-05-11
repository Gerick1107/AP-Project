package edu.univ1.auth;
import edu.univ1.api.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;

public class LoginService{
    private final UserAuthDAO userAuthDAO=new UserAuthDAO();
    private final SecurityService securityService=new SecurityService();
    public LoginResult login(String username,String password){
        User user=userAuthDAO.findByUsername(username);
        if(user==null){
            return LoginResult.failure("Invalid Username Or Password.");
        }
        if(!user.isActive()){
            return LoginResult.failure("Account Is Disabled.");
        }
        String storedHash=userAuthDAO.getPasswordHash(user.getUserId());
        if(!PasswordHasher.verify(password,storedHash)){
            int attempts=securityService.incrementFailedAttempts(user.getUserId());
            if(attempts>=5){
                securityService.markMustChangePassword(user.getUserId(),true);
                return LoginResult.locked(user,"Too Many Failed Attempts. Please Change Your Password.");
            }
            return LoginResult.failure("Invalid Username Or Password.");
        }
        securityService.resetFailedAttempts(user.getUserId());
        if(securityService.mustChangePassword(user.getUserId())){
            return LoginResult.requirePasswordChange(user,"Password Change Required Before Login.");
        }
        userAuthDAO.updateLastLogin(user.getUserId());
        return LoginResult.success(user);
    }
    public boolean changePassword(long userId,String currentPassword,String newPassword){
        String storedHash=userAuthDAO.getPasswordHash(userId);
        if(!PasswordHasher.verify(currentPassword,storedHash)){
            return false;
        }
        boolean updated=userAuthDAO.updatePassword(userId,PasswordHasher.hash(newPassword));
        if(updated){
            securityService.resetFailedAttempts(userId);
            securityService.markMustChangePassword(userId,false);
        }
        return updated;
    }
}