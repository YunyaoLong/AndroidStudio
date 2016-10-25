package com.example.yunyao.lab4;

/**
 * Created by yunyao on 2016/10/18.
 */

public class FruitSource {
    private int FruitSrc;
    private String FruitName;

    public FruitSource(int FruitSrc, String FruitName) {
        this.FruitSrc = FruitSrc;
        this.FruitName = FruitName;
    }
    public int getFruitSrc(){return FruitSrc;}
    public String getFruitName(){return FruitName;}

}
