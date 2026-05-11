package edu.univ1.domain;
import java.time.LocalDate;

public class SystemSettings{
    private final boolean maintenanceMode;
    private final LocalDate registrationDeadline;
    private final LocalDate dropDeadline;
    public SystemSettings(boolean maintenanceMode,LocalDate registrationDeadline,LocalDate dropDeadline){
        this.maintenanceMode=maintenanceMode;
        this.registrationDeadline=registrationDeadline;
        this.dropDeadline=dropDeadline;
    }
    public boolean isMaintenanceMode(){
        return maintenanceMode;
    }
    public LocalDate getRegistrationDeadline(){
        return registrationDeadline;
    }
    public LocalDate getDropDeadline(){
        return dropDeadline;
    }
}