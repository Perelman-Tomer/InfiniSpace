package com.example.infinispace;

public class Scorer {

    private int rank;
    private String score;
    private int level;

    public Scorer(int rank , String score){
        this.rank=rank;
        this.score=score;
    }

    public int getRank(){return rank;}
    public void setRank(){this.rank=rank;}
    public String getScore(){
        return score;
    }
    public void setScore(String score){
        this.score=score;
    }




}
