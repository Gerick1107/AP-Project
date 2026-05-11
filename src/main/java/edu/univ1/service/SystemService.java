package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.time.LocalDate;

public class SystemService{
    private final SettingsDAO settingsDAO=new SettingsDAO();
    private final NotificationService notificationService=new NotificationService();
    public SystemSettings getSystemSettings(){
        return new SystemSettings(
                settingsDAO.getMaintenanceMode(),
                settingsDAO.getDateValue("register_deadline"),
                settingsDAO.getDateValue("drop_deadline")
        );
    }
    public boolean updateDeadlines(LocalDate registerDeadline,LocalDate dropDeadline){
        boolean ok=true;
        if(registerDeadline!=null){
            ok &= settingsDAO.setDateValue("register_deadline",registerDeadline);
        }else{
            settingsDAO.deleteValue("register_deadline");
        }
        if(dropDeadline!=null){
            ok &= settingsDAO.setDateValue("drop_deadline",dropDeadline);
        }else{
            settingsDAO.deleteValue("drop_deadline");
        }
        if(ok){
            notificationService.publishDeadlineUpdate(registerDeadline,dropDeadline);
        }
        return ok;
    }
}