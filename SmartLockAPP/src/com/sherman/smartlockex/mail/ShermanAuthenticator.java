package com.sherman.smartlockex.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class ShermanAuthenticator extends Authenticator {
	String userName=null;  
    String password=null;  
       
    public ShermanAuthenticator(){  
    }  
    public ShermanAuthenticator(String username, String password) {   
        this.userName = username;   
        this.password = password;   
    }   
    protected PasswordAuthentication getPasswordAuthentication(){  
        return new PasswordAuthentication(userName, password);  
    } 
}
