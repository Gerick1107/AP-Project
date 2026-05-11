package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;

public class AuthAPI{
    private final LoginService loginService=new LoginService();
    public LoginResult login(String username,String password){
        return loginService.login(username,password);
    }
    public boolean changePassword(long userId,String currentPassword,String newPassword){
        return loginService.changePassword(userId,currentPassword,newPassword);
    }
}