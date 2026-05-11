package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;

public class SecurityService{
    private final SettingsDAO settingsDAO=new SettingsDAO();
    private String key(String prefix,long userId){
        return "security."+prefix+"."+userId;
    }
    public int incrementFailedAttempts(long userId){
        int attempts=getFailedAttempts(userId)+1;
        settingsDAO.setValue(key("failedAttempts",userId),String.valueOf(attempts));
        return attempts;
    }
    public void resetFailedAttempts(long userId){
        settingsDAO.setValue(key("failedAttempts",userId),"0");
    }
    public int getFailedAttempts(long userId){
        String value=settingsDAO.getValue(key("failedAttempts",userId));
        if(value==null){
            return 0;
        }
        try{
            return Integer.parseInt(value);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    public void markMustChangePassword(long userId,boolean required){
        settingsDAO.setValue(key("mustChange",userId),required ? "true" : "false");
    }
    public boolean mustChangePassword(long userId){
        String value=settingsDAO.getValue(key("mustChange",userId));
        return "true".equalsIgnoreCase(value);
    }
}