package com.igorpavinich.messenger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by Igor Pavinich on 18.12.2017.
 */
public class SHA {

    /**
     * encrypt password using SHA-512 Algorithm
     * @param passwordToHash
     * @return encrypted Password
     */
    public static String encrypt(String passwordToHash)
    {

        passwordToHash = "#Void_"+passwordToHash+"_Chat$";

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
