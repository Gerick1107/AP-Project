package edu.univ1.domain;

public class LoginResult{
    private final LoginStatus status;
    private final User user;
    private final String message;
    private LoginResult(LoginStatus status,User user,String message){
        this.status=status;
        this.user=user;
        this.message=message;
    }
    public static LoginResult success(User user){
        return new LoginResult(LoginStatus.SUCCESS,user,null);
    }
    public static LoginResult requirePasswordChange(User user,String message){
        return new LoginResult(LoginStatus.PASSWORD_CHANGE_REQUIRED,user,message);
    }
    public static LoginResult failure(String message){
        return new LoginResult(LoginStatus.FAILURE,null,message);
    }
    public static LoginResult locked(User user,String message){
        return new LoginResult(LoginStatus.ACCOUNT_LOCKED,user,message);
    }
    public LoginStatus getStatus(){
        return status;
    }
    public User getUser(){
        return user;
    }
    public String getMessage(){
        return message;
    }
}