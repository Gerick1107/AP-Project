package edu.univ1.domain;

public class Grade{
    private long gradeId;
    private long enrollmentId;
    private String component;
    private double score;
    private double maxScore;
    private double weight;
    private String finalGrade;
    public Grade(){}
    public Grade(long gradeId,long enrollmentId,String component,
                 double score,double maxScore,double weight,String finalGrade){
        this.gradeId=gradeId;
        this.enrollmentId=enrollmentId;
        this.component=component;
        this.score=score;
        this.maxScore=maxScore;
        this.weight=weight;
        this.finalGrade=finalGrade;
    }
    public long getGradeId(){
        return gradeId;
    }
    public void setGradeId(long gradeId){
        this.gradeId = gradeId;
    }
    public long getEnrollmentId(){
        return enrollmentId;
    }
    public void setEnrollmentId(long enrollmentId){
        this.enrollmentId = enrollmentId;
    }
    public String getComponent(){
        return component;
    }
    public void setComponent(String component){
        this.component=component;
    }
    public double getScore(){
        return score;
    }
    public void setScore(double score){
        this.score=score;
    }
    public double getMaxScore(){
        return maxScore;
    }
    public void setMaxScore(double maxScore){
        this.maxScore=maxScore;
    }
    public double getWeight(){
        return weight;
    }
    public void setWeight(double weight){
        this.weight=weight;
    }
    public String getFinalGrade(){
        return finalGrade;
    }
    public void setFinalGrade(String finalGrade){
        this.finalGrade=finalGrade;
    }
    @Override
    public String toString(){
        return "Grade{"+
                "gradeId="+gradeId+
                ", enrollmentId="+enrollmentId+
                ", component='"+component+'\''+
                ", score="+score+
                ", maxScore="+maxScore+
                ", weight="+weight+
                ", finalGrade='"+finalGrade+'\''+
                '}';
    }
}
