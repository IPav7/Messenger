package com.igorpavinich.messenger;

/**
 * Created by Igor Pavinich on 30.11.2017.
 */

public class CheckInput {
    static boolean checkLogin(String login){
        if(login.isEmpty())
            return false;
        if(login.length() < 3 || login.length()>20)
            return false;
        for(int i=0; i<login.length(); i++){
            if(!Character.isLetter(login.charAt(i)) && !Character.isDefined(login.charAt(i)))
                return false;
        }
        return true;
    }

    static boolean checkPassword(String password){
        if(password.isEmpty())
            return false;
        if(password.length() < 5 || password.length() > 20)
            return false;
        for(int i=0; i<password.length(); i++){
            if(!Character.isLetter(password.charAt(i)) && !Character.isDefined(password.charAt(i)))
                return false;
        }
        return true;
    }

    static boolean checkName(String name){
        if(name.isEmpty())
            return false;
        if(name.length() < 2 || name.length() > 20)
            return false;
        if(Character.isLowerCase(name.charAt(0)))
            return false;
        for (int i = 0; i < name.length(); i++) {
            if(!Character.isLetter(name.charAt(i)))
                return false;
        }
        for (int i = 1; i < name.length(); i++) {
            if(Character.isUpperCase(name.charAt(i)))
                return false;
        }
        return true;
    }
}
