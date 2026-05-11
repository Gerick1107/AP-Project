package edu.univ1.domain;

public class User{
    private long userId;
    private String username;
    private String role;
    private boolean active;
    public User(){}
    public User(long userId,String username,String role,boolean active){
        this.userId=userId;
        this.username=username;
        this.role=role;
        this.active=active;
    }
    public long getUserId(){
        return userId;
    }
    public void setUserId(long userId){
        this.userId=userId;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role=role;
    }
    public boolean isActive(){
        return active;
    }
    public void setActive(boolean active){
        this.active=active;
    }
    @Override
    public String toString(){
        return "User{"+
                "userId="+userId+
                ", username='"+username+'\''+
                ", role='"+role+'\''+
                ", active="+active+
                '}';
    }
}