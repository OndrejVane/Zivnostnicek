package com.example.ondrejvane.zivnostnicek.helper;

/**
 * Třída, která bude uchovávat základní informace o uživateli 
 * pro přístup všude v apliakci. Tato třída je singleton.
 */
public class UserInformation {

    private int UserId;
    private String Mail;
    private String FullName;
    private static UserInformation self = null;

    private UserInformation(){}

    public static synchronized UserInformation getInstance() {
        if (self == null){
            self = new UserInformation();
        }
        return self;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String name) {
        this.FullName = name;
    }


    
}
