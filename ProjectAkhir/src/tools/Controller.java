/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import View.ViewAnalysis;
import View.ViewLogin;
import View.ViewRegister;
import View.ViewUpload;

/**
 *
 * @author mfaja
 */

public class Controller {
    MainModel MM ;
    private String user, pass;

        
    public void setMo(MainModel mo){
        this.MM = mo;
    }

    public Controller() {

    }
    
    public void clearInput(){
        MM.clearUser();
        MM.clearPass();
    }
    
    public void clearUser(){
        MM.clearUser();
    }
    
    public void clearPass(){
        MM.clearPass();
    }
    
    public void getPath(){
        MM.setPath();
    }
    
    public void inputUser(ViewRegister vr){
        this.user = vr.getUser();
        this.pass = vr.getPass();
        MM.setUsername(this.user);
        MM.setPassword(this.pass);
        MM.registerData(this);
    }
    
    public void loginData(ViewLogin vl){
        this.user = vl.getUser();
        this.pass = vl.getPass();
        MM.setUsername(this.user);
        MM.setPassword(this.pass);
        MM.loginData(this, vl);
    }
    
    public void viewTable(ViewUpload vu){
        MM.table(vu);
    }
    
    public void updateBar(ViewAnalysis va){
        MM.bar(va);
        
    }
    
    public void insertBar(ViewAnalysis va){
        MM.analyize();
        
    }
    
    public void deleteTab(ViewUpload vu){
        MM.deleteRow(vu);
    }
    
    public void clearTab(){
        MM.clearTab();
    }
    
}
