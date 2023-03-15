package com.example.noteit.util;

import java.util.Random;

public class Randomize {
    private Randomize(){}
    private static Randomize instance;
    public static Randomize getInstance(){
        if(instance == null){
            instance = new Randomize();
        }
        return instance;
    }
    private Random rdm = new Random();
    public String randomOTP(){
        char[] otp = new char[6];
        String numbers = "0123456789";
        for(int i = 0; i < 6;i++){
            otp[i] = numbers.charAt(rdm.nextInt(numbers.length()));
        }
        return new String(otp);
    }
    public String randomCode(){
        char[] otp = new char[6];
        String numbers = "0123456789";
        for(int i = 0; i < 6;i++){
            otp[i] = numbers.charAt(rdm.nextInt(numbers.length()));
        }
        return new String(otp);
    }

}
