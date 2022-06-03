/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author mfaja
 */
public class HashPassword {
    int key = 6;
    String input;

    public HashPassword(String input) {
        this.input = input;
    }
    public HashPassword() {
        
    }
    
    
    public String encrypt(String mess){
        try {
  
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(mess.getBytes());
  
            BigInteger no = new BigInteger(1, messageDigest);
  
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } 
  
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    
}
