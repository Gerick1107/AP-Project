package edu.univ1.domain;

public class Section{
    private long sectionId;
    private long courseId;
    private long instructorId;
    private String dayTime;
    private String room;
    private int capacity;
    private String semester;
    private int year;
    private String courseCode;
    private String courseTitle;
    private String instructorName;
    public Section(){
        this.courseCode=null;
        this.courseTitle=null;
        this.instructorName=null;
    }
    public Section(long sectionId,long courseId,long instructorId,String dayTime,String room,int capacity,
                   String semester,int year){
        this.sectionId=sectionId;
        this.courseId=courseId;
        this.instructorId=instructorId;
        this.dayTime=dayTime;
        this.room=room;
        this.capacity=capacity;
        this.semester=semester;
        this.year=year;
        this.courseCode=null;
        this.courseTitle=null;
        this.instructorName=null;
    }
    public long getSectionId(){
        return sectionId;
    }
    public void setSectionId(long sectionId){
        this.sectionId = sectionId;
    }
    public long getCourseId(){
        return courseId;
    }
    public void setCourseId(long courseId){
        this.courseId=courseId;
    }
    public long getInstructorId(){
        return instructorId;
    }
    public void setInstructorId(long instructorId){
        this.instructorId=instructorId;
    }
    public String getDayTime(){
        return dayTime;
    }
    public void setDayTime(String dayTime){
        this.dayTime = dayTime;
    }
    public String getRoom(){
        return room;
    }
    public void setRoom(String room){
        this.room=room;
    }
    public int getCapacity(){
        return capacity;
    }
    public void setCapacity(int capacity){
        this.capacity=capacity;
    }
    public String getSemester(){
        return semester;
    }
    public void setSemester(String semester){
        this.semester=semester;
    }
    public int getYear(){
        return year;
    }
    public void setYear(int year){
        this.year=year;
    }
    public String getCourseCode(){
        return courseCode;
    }
    public void setCourseCode(String courseCode){
        this.courseCode=courseCode;
    }
    public String getCourseTitle(){
        return courseTitle;
    }
    public void setCourseTitle(String courseTitle){
        this.courseTitle=courseTitle;
    }
    public String getInstructorName(){
        return instructorName;
    }
    public void setInstructorName(String instructorName){
        this.instructorName=instructorName;
    }
    @Override
    public String toString(){
        return "Section{"+
                "sectionId="+sectionId+
                ", courseId="+courseId+
                ", instructorId="+instructorId+
                ", dayTime='"+dayTime+'\''+
                ", room='"+room+'\''+
                ", capacity="+capacity+
                ", semester='"+semester+'\''+
                ", year="+year+
                ", courseCode='"+courseCode+'\'' +
                ", courseTitle='"+courseTitle+'\''+
                ", instructorName='"+instructorName+'\''+
                '}';
    }
}