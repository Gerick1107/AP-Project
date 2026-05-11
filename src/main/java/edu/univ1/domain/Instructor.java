package edu.univ1.domain;

public class Instructor{
    private long userId;
    private String department;
    private String displayName;
    public Instructor(){
        this.displayName=null;
    }
    public Instructor(long userId,String department){
        this.userId=userId;
        this.department=department;
        this.displayName=null;
    }
    public long getUserId(){
        return userId;
    }
    public void setUserId(long userId){
        this.userId=userId;
    }
    public String getDepartment(){
        return department;
    }
    public void setDepartment(String department){
        this.department=department;
    }
    public String getDisplayName(){
        return displayName;
    }
    public void setDisplayName(String displayName){
        this.displayName=displayName;
    }
    @Override
    public String toString(){
        if(displayName!=null&&!displayName.isBlank()){
            return displayName;
        }
        return "User"+userId;
    }
}