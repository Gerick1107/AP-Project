package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.time.LocalDate;
import java.util.List;

public class NotificationAPI{
    private final NotificationService notificationService=new NotificationService();
    public List<Notification> getStudentNotifications(){
        return notificationService.getStudentNotifications();
    }
    public List<Notification> getRecentNotifications(int limit){
        return notificationService.getRecentNotifications(limit);
    }
    public long sendManualNotification(String title,String message,String createdBy,LocalDate expiresOn){
        return notificationService.publishManualNotification(title,message,createdBy,expiresOn);
    }
    public boolean delete(long notificationId){
        return notificationService.delete(notificationId);
    }
}