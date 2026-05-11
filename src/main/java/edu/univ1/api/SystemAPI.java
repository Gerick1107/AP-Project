package edu.univ1.api;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import edu.univ1.util.*;
import java.time.LocalDate;

public class SystemAPI{
    private final SystemService systemService=new SystemService();
    public SystemSettings getSettings(){
        return systemService.getSystemSettings();
    }
    public boolean updateDeadlines(LocalDate registrationDeadline,LocalDate dropDeadline){
        return systemService.updateDeadlines(registrationDeadline,dropDeadline);
    }
}