package com.wambui.dice.fastservice;

public class ModelClass {
    private int imageview;
    private String textview1;
    private String textview2;

    ModelClass(int imageview,String textview1,String textview2) {
        this.imageview = imageview;
        this.textview1 = textview1;
        this.textview2 = textview2;
    }

        public int getImageView(){
            return imageview;
        }
        public String getTextView1(){
            return textview1;
        }

    public String getTextView2(){
        return textview2;
    }
    }

