package edu.univ1.domain;

public class Enrollment{
    private long enrollmentId;
    private long studentId;
    private long sectionId;
    private String status;
    private double finalScore;
    private String finalLetter;
    private double gradePoint;
    public Enrollment(){}
    public Enrollment(long enrollmentId,long studentId,long sectionId,String status){
        this.enrollmentId=enrollmentId;
        this.studentId=studentId;
        this.sectionId=sectionId;
        this.status=status;
        this.finalScore=0;
        this.finalLetter=null;
        this.gradePoint=0;
    }
    public long getEnrollmentId(){
        return enrollmentId;
    }
    public void setEnrollmentId(long enrollmentId){
        this.enrollmentId=enrollmentId;
    }
    public long getStudentId(){
        return studentId;
    }
    public void setStudentId(long studentId){
        this.studentId=studentId;
    }
    public long getSectionId(){
        return sectionId;
    }
    public void setSectionId(long sectionId){
        this.sectionId=sectionId;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public double getFinalScore(){
        return finalScore;
    }
    public void setFinalScore(double finalScore){
        this.finalScore = finalScore;
    }
    public String getFinalLetter(){
        return finalLetter;
    }
    public void setFinalLetter(String finalLetter){
        this.finalLetter=finalLetter;
    }
    public double getGradePoint(){
        return gradePoint;
    }
    public void setGradePoint(double gradePoint){
        this.gradePoint = gradePoint;
    }
    @Override
    public String toString(){
        return "Enrollment{"+
                "enrollmentId="+enrollmentId+
                ", studentId="+studentId+
                ", sectionId="+sectionId+
                ", status='"+status+'\''+
                ", finalScore="+finalScore+
                ", finalLetter='"+finalLetter+'\'' +
                ", gradePoint="+gradePoint+
                '}';
    }
}