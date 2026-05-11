package edu.univ1.util;
import edu.univ1.domain.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class PDFUtils{
    public static void exportTranscriptPDF(List<TranscriptEntry> entries,String filePath){
        try(PDDocument doc=new PDDocument()){
            PDPage page=new PDPage();
            doc.addPage(page);
            PDPageContentStream cs=new PDPageContentStream(doc,page);
            PDRectangle box=page.getMediaBox();
            float margin=40;
            float y=box.getUpperRightY()-margin;
            float rowHeight=24;
            float[] colWidths={60,145,60,55,55,55,70,60};
            String[] headers={"Code","Course Title","Score","Letter","Points","Credits","Semester","Year"};
            float logoHeight=drawLogoAndTitle(cs,doc,page,margin,y,"Transcript");
            y-=logoHeight+10;
            drawSubtitle(cs,margin,y,"Generated on "+LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            y-=30;
            double totalPoints=0;
            int totalCredits=0;
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),12);
            drawTableRow(cs,margin,y,rowHeight,colWidths,headers,true,false);
            y-=rowHeight;
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),11);
            int stripe=0;
            for(TranscriptEntry entry:entries){
                if(y<margin+50){
                    cs.close();
                    page=new PDPage();
                    doc.addPage(page);
                    cs=new PDPageContentStream(doc,page);
                    y=page.getMediaBox().getUpperRightY()-margin;
                    drawTitle(cs,margin,y,"Transcript (cont.)");
                    y-=30;
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),12);
                    drawTableRow(cs,margin,y,rowHeight,colWidths,headers,true,false);
                    y-=rowHeight;
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),11);
                    stripe=0;
                }
                String[] row={
                        entry.getCourseCode(),
                        entry.getCourseTitle(),
                        String.format("%.2f",entry.getFinalScore()),
                        entry.getLetterGrade(),
                        String.format("%.2f",entry.getGradePoint()),
                        String.valueOf(entry.getCredits()),
                        entry.getSemester(),
                        String.valueOf(entry.getYear())
                };
                drawTableRow(cs,margin,y,rowHeight,colWidths,row,false,stripe%2==0);
                y-=rowHeight;
                stripe++;
                totalPoints+=entry.getGradePoint()*entry.getCredits();
                totalCredits+=entry.getCredits();
            }
            double cgpa=totalCredits>0? totalPoints/totalCredits:0;
            y-=18;
            cs.setNonStrokingColor(new Color(52,190,170));
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),12);
            cs.beginText();
            cs.newLineAtOffset(margin,Math.max(y,margin));
            cs.showText(String.format("Credits Earned: %d    CGPA: %.2f",totalCredits,cgpa));
            cs.endText();
            cs.setNonStrokingColor(Color.BLACK);
            cs.close();
            doc.save(filePath);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void exportRosterPDF(List<Student> students,String filePath){
        try(PDDocument doc=new PDDocument()){
            PDPage page=new PDPage();
            doc.addPage(page);
            PDPageContentStream cs=new PDPageContentStream(doc,page);
            PDRectangle box=page.getMediaBox();
            float margin=40;
            float y=box.getUpperRightY()-margin;
            float rowHeight=22;
            float[] colWidths={160,110,160,60};
            String[] headers={"Name","Roll No","Program","Year"};
            float logoHeight=drawLogoAndTitle(cs,doc,page,margin,y,"Section Roster");
            y-=logoHeight+10;
            drawSubtitle(cs,margin,y,"Generated on "+LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            y-=30;
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),12);
            drawTableRow(cs,margin,y,rowHeight,colWidths,headers,true,false);
            y-=rowHeight;
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),11);
            int stripe=0;
            for(Student s:students){
                if(y<margin+40){
                    cs.close();
                    page=new PDPage();
                    doc.addPage(page);
                    cs=new PDPageContentStream(doc,page);
                    y=page.getMediaBox().getUpperRightY()-margin;
                    drawTitle(cs,margin,y,"Section Roster (cont.)");
                    y-=30;
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),12);
                    drawTableRow(cs,margin,y,rowHeight,colWidths,headers,true,false);
                    y-=rowHeight;
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),11);
                    stripe=0;
                }
                String[] row={
                        displayName(s),
                        s.getRollNo(),
                        s.getProgram(),
                        String.valueOf(s.getYear())
                };
                drawTableRow(cs,margin,y,rowHeight,colWidths,row,false,stripe%2==0);
                y-=rowHeight;
                stripe++;
            }
            y-=18;
            cs.setNonStrokingColor(new Color(52,190,170));
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),12);
            cs.beginText();
            cs.newLineAtOffset(margin,Math.max(y,margin));
            cs.showText("Total Students: "+students.size());
            cs.endText();
            cs.setNonStrokingColor(Color.BLACK);
            cs.close();
            doc.save(filePath);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private static float drawLogoAndTitle(PDPageContentStream cs,PDDocument doc,PDPage page,float x,float y,String title) throws IOException{
        float logoHeight=0;
        float logoX=x;
        try{
            InputStream logoStream=PDFUtils.class.getResourceAsStream("/logo.png");
            if(logoStream!=null){
                PDImageXObject logo=PDImageXObject.createFromByteArray(doc,logoStream.readAllBytes(),"logo");
                float logoWidth=logo.getWidth()*0.15f;
                logoHeight=logo.getHeight()*0.15f;
                cs.drawImage(logo,logoX,y-logoHeight,logoWidth,logoHeight);
            }
        }catch(Exception e){
        }
        cs.setNonStrokingColor(Color.BLACK);
        PDType1Font font=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        float fontSize=24;
        cs.setFont(font,fontSize);
        float textWidth=font.getStringWidth(title)/1000f*fontSize;
        float pageWidth=page.getMediaBox().getWidth();
        float titleX=(pageWidth-textWidth)/2f;
        cs.beginText();
        cs.newLineAtOffset(titleX,y-20);
        cs.showText(title);
        cs.endText();
        cs.setNonStrokingColor(Color.BLACK);
        return Math.max(logoHeight,26);
    }
    private static void drawTitle(PDPageContentStream cs,float x,float y,String title) throws IOException{
        cs.setNonStrokingColor(new Color(52,190,170));
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),20);
        cs.beginText();
        cs.newLineAtOffset(x,y);
        cs.showText(title);
        cs.endText();
        cs.setNonStrokingColor(Color.BLACK);
    }
    private static void drawSubtitle(PDPageContentStream cs,float x,float y,String subtitle) throws IOException{
        cs.setNonStrokingColor(new Color(100,110,120));
        cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),11);
        cs.beginText();
        cs.newLineAtOffset(x,y);
        cs.showText(subtitle);
        cs.endText();
        cs.setNonStrokingColor(Color.BLACK);
    }
    private static void drawTableRow(PDPageContentStream cs,
                                     float startX,
                                     float startY,
                                     float rowHeight,
                                     float[] colWidths,
                                     String[] values,
                                     boolean header,
                                     boolean shaded) throws IOException{
        float x=startX;
        Color background=header
                ? new Color(52,190,170) : (shaded ? new Color(245,247,250) : Color.WHITE);
        Color textColor=header ? Color.WHITE : new Color(30,35,45);
        for(int i=0;i<colWidths.length && i<values.length;i++){
            cs.setNonStrokingColor(background);
            cs.addRect(x,startY-rowHeight,colWidths[i],rowHeight);
            cs.fill();
            cs.setStrokingColor(new Color(200,205,210));
            cs.addRect(x,startY-rowHeight,colWidths[i],rowHeight);
            cs.stroke();
            cs.setNonStrokingColor(textColor);
            cs.beginText();
            cs.newLineAtOffset(x+4,startY-rowHeight+7);
            cs.showText(truncate(values[i],(int)colWidths[i]/6));
            cs.endText();
            x+=colWidths[i];
        }
        cs.setNonStrokingColor(Color.BLACK);
    }
    private static String truncate(String value,int maxChars){
        if(value==null){
            return "";
        }
        if(value.length()<=maxChars){
            return value;
        }
        return value.substring(0,Math.max(0,maxChars-3))+"...";
    }
    private static String displayName(Student s){
        if(s.getDisplayName()!=null&&!s.getDisplayName().isBlank()){
            return s.getDisplayName();
        }
        return "User "+s.getUserId();
    }
}
