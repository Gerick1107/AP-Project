package edu.univ1.domain;

public class GradeStats{
    private final double average;
    private final double median;
    private final double mean;
    private final double min;
    private final double max;
    public GradeStats(double average,double median,double mean,double min,double max){
        this.average=average;
        this.median=median;
        this.mean=mean;
        this.min=min;
        this.max = max;
    }
    public double getAverage(){
        return average;
    }
    public double getMedian(){
        return median;
    }
    public double getMean(){
        return mean;
    }
    public double getMin(){
        return min;
    }
    public double getMax(){
        return max;
    }
}

