package edu.univ1.service;
import edu.univ1.domain.GradeEvaluation;

public class GradeScale{
    private GradeScale(){}
    public static GradeEvaluation evaluate(double scoreOutOf100){
        double score=Math.max(0,Math.min(100,scoreOutOf100));
        if(score>=100) return new GradeEvaluation("A+",10);
        if(score>=90) return new GradeEvaluation("A",10);
        if(score>=80) return new GradeEvaluation("A-",9);
        if(score>=70) return new GradeEvaluation("B",8);
        if(score>=60) return new GradeEvaluation("B-",7);
        if(score>=50) return new GradeEvaluation("C",6);
        if(score>=40) return new GradeEvaluation("C-",5);
        if(score>=30) return new GradeEvaluation("D",4);
        return new GradeEvaluation("F",2);
    }
}