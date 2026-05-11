package edu.univ1.util;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.service.*;
import edu.univ1.ui.*;
import org.apache.commons.csv.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVUtils{
    public static void exportTranscript(List<TranscriptEntry> grades,String filePath){
        try(FileWriter out=new FileWriter(filePath)){
            CSVPrinter printer=new
                    CSVPrinter(out,CSVFormat.DEFAULT.withHeader("Enrollment ID", "Course Code",
                    "Course Title", "Final Score", "Grade Letter", "Grade Point", "Credits", "Semester", "Year"));
            for(TranscriptEntry entry:grades){
                printer.printRecord(
                        entry.getEnrollmentId(),
                        entry.getCourseCode(),
                        entry.getCourseTitle(),
                        String.format("%.2f", entry.getFinalScore()),
                        entry.getLetterGrade(),
                        String.format("%.2f", entry.getGradePoint()),
                        entry.getCredits(),
                        entry.getSemester(),
                        entry.getYear()
                );
            }
            printer.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void exportRoster(List<Student> students,String filePath){
        try(FileWriter out=new FileWriter(filePath)){
            CSVPrinter printer=new CSVPrinter(out,CSVFormat.DEFAULT.withHeader("User ID", "Roll No", "Program", "Year"));
            for(Student s:students){
                printer.printRecord(
                        s.getUserId(),
                        s.getRollNo(),
                        s.getProgram(),
                        s.getYear()
                );
            }
            printer.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void exportCourses(List<Course> courses,String filePath){
        try(FileWriter out=new FileWriter(filePath)){
            CSVPrinter printer=new CSVPrinter(out,CSVFormat.DEFAULT.withHeader("Course ID", "Code", "Title", "Credits"));
            for (Course c:courses){
                printer.printRecord(
                        c.getCourseId(),
                        c.getCode(),
                        c.getTitle(),
                        c.getCredits()
                );
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}