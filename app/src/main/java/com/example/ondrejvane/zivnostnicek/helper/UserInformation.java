package com.example.ondrejvane.zivnostnicek.helper;

import com.example.ondrejvane.zivnostnicek.model.User;

/**
 * Třída, která bude uchovávat základní informace o uživateli 
 * pro přístup všude v apliakci. Tato třída je singleton.
 */
public class UserInformation {

    private int UserId;
    private String Mail;
    private String FullName;
    private int syncNumber;
    private static UserInformation self = null;

    private UserInformation(){}

    public static synchronized UserInformation getInstance() {
        if (self == null){
            self = new UserInformation();
        }
        return self;
    }

    public void setDataFromUser(User user){
        if(self != null){
            self.setUserId(user.getId());
            self.setMail(user.getEmail());
            self.setFullName(user.getFullName());
            self.setSyncNumber(user.getSyncNumber());
        }
    }

    public int getSyncNumber() {
        return syncNumber;
    }

    public void setSyncNumber(int syncNumber) {
        this.syncNumber = syncNumber;
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

    public void resetInstance(){
        self = null;
    }


    
}
