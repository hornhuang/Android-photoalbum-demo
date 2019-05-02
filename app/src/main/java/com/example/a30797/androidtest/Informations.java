package com.example.a30797.androidtest;

import android.graphics.Bitmap;

public class Informations {
    //文体
    private String essay;
    //图片
    private Bitmap imageId;

    Informations(Bitmap imageId, String essay){
        this.imageId = imageId;
        this.essay = essay;
    }

    Informations(){
        this.essay = null;
        this.imageId = null;
    }

    public String getEssay() {
        return essay;
    }

    public void setEssay(String essay) {
        this.essay = essay;
    }

    public Bitmap getImageBitmap() {
        return imageId;
    }

    public void setImageBitmap(Bitmap imageId) {
        this.imageId = imageId;
    }
}
