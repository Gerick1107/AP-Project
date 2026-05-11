package edu.univ1.domain;

public class GradeEvaluation{
    private final String letter;
    private final double gradePoint;
    public GradeEvaluation(String letter,double gradePoint){
        this.letter=letter;
        this.gradePoint=gradePoint;
    }
    public String getLetter(){
        return letter;
    }
    public double getGradePoint(){
        return gradePoint;
    }
}