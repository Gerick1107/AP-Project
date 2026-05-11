package edu.univ1.domain;

public class Student{
    private long userId;
    private String rollNo;
    private String program;
    private int year;
    private String displayName;
    private long enrollmentId;
    public Student(){
        this.displayName=null;
    }
    public Student(long userId,String rollNo,String program,int year){
        this(userId,rollNo,program,year,0L);
    }
    public Student(long userId,String rollNo,String program,int year,long enrollmentId){
        this.userId=userId;
        this.rollNo=rollNo;
        this.program=program;
        this.year=year;
        this.enrollmentId=enrollmentId;
        this.displayName=null;
    }
    public long getUserId(){
        return userId;
    }
    public void setUserId(long userId){
        this.userId=userId;
    }
    public String getRollNo(){
        return rollNo;
    }
    public void setRollNo(String rollNo){
        this.rollNo = rollNo;
    }
    public String getProgram(){
        return program;
    }
    public void setProgram(String program){
        this.program=program;
    }
    public int getYear(){
        return year;
    }
    public void setYear(int year){
        this.year=year;
    }
    public String getDisplayName(){
        return displayName;
    }
    public void setDisplayName(String displayName){
        this.displayName=displayName;
    }
    public long getEnrollmentId(){
        return enrollmentId;
    }
    public void setEnrollmentId(long enrollmentId){
        this.enrollmentId=enrollmentId;
    }
    @Override
    public String toString(){
        return "Student{"+
                "userId="+userId+
                ", rollNo='"+rollNo+'\''+
                ", program='"+program+'\''+
                ", year="+year+
                ", displayName='"+displayName+'\''+
                ", enrollmentId="+enrollmentId+
                '}';
    }
}