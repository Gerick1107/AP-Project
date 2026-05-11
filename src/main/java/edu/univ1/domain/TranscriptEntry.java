package edu.univ1.domain;

public class TranscriptEntry{
    private long enrollmentId;
    private long courseId;
    private String courseCode;
    private String courseTitle;
    private double finalScore;
    private String letterGrade;
    private double gradePoint;
    private int credits;
    private String semester;
    private int year;
    public TranscriptEntry(long enrollmentId,long courseId,String courseCode,String courseTitle,
                           double finalScore,String letterGrade,double gradePoint,
                           int credits,String semester,int year){
        this.enrollmentId=enrollmentId;
        this.courseId=courseId;
        this.courseCode=courseCode;
        this.courseTitle=courseTitle;
        this.finalScore=finalScore;
        this.letterGrade=letterGrade;
        this.gradePoint=gradePoint;
        this.credits=credits;
        this.semester=semester;
        this.year=year;
    }
    public long getEnrollmentId(){
        return enrollmentId;
    }
    public long getCourseId(){
        return courseId;
    }
    public String getCourseCode(){
        return courseCode;
    }
    public String getCourseTitle(){
        return courseTitle;
    }
    public double getFinalScore(){
        return finalScore;
    }
    public String getLetterGrade(){
        return letterGrade;
    }
    public double getGradePoint(){
        return gradePoint;
    }
    public int getCredits(){
        return credits;
    }
    public String getSemester(){
        return semester;
    }
    public int getYear(){
        return year;
    }
}