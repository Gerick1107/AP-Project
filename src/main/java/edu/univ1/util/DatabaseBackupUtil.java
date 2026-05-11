package edu.univ1.util;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import java.io.File;
import java.io.IOException;

public class DatabaseBackupUtil{
    private static final String DB_NAME=DatabaseConfig.ERP_DB_URL.substring(DatabaseConfig.ERP_DB_URL.lastIndexOf('/')+1);
    private DatabaseBackupUtil(){}
    public static boolean backup(String filePath){
        ProcessBuilder builder=new ProcessBuilder(
                "mysqldump",
                "-u",DatabaseConfig.ERP_DB_USER,
                "-p"+DatabaseConfig.ERP_DB_PASSWORD,
                DB_NAME
        );
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectOutput(new File(filePath));
        return run(builder);
    }
    public static boolean restore(String filePath){
        ProcessBuilder builder=new ProcessBuilder(
                "mysql",
                "-u",DatabaseConfig.ERP_DB_USER,
                "-p"+DatabaseConfig.ERP_DB_PASSWORD,
                DB_NAME
        );
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectInput(new File(filePath));
        return run(builder);
    }
    private static boolean run(ProcessBuilder builder){
        try{
            Process process=builder.start();
            int exit=process.waitFor();
            return exit==0;
        }catch(IOException|InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return false;
    }
}