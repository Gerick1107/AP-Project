package edu.univ1.ui.student;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.service.*;
import edu.univ1.ui.admin.*;
import edu.univ1.ui.instructor.*;
import edu.univ1.ui.*;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.util.List;

public class StudentGradesPanel extends JFrame{
    private final User user;
    private final StudentAPI studentAPI=new StudentAPI();
    private final GradeAPI gradeAPI=new GradeAPI();
    private final JTable tblTranscript=new JTable();
    private final JTextField txtFilter=new JTextField(20);
    private TableRowSorter<DefaultTableModel> sorter;
    private final JLabel lblGPA=new JLabel();
    public StudentGradesPanel(User user){
        super("My Grades & Transcript");
        this.user=user;
        initUI();
    }
    private void initUI(){
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\u270D\uFE0F My Grades & Transcript");
        title.setForeground(new Color(52,190,170));
        title.setFont(new Font("SansSerif",Font.BOLD,28));
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52,190,170,150),2,true),
                new EmptyBorder(8,20,8,20)
        ));
        header.add(title,"align center");
        JSeparator separator=new JSeparator();
        separator.setForeground(new Color(52,190,170,100));
        separator.setBackground(new Color(52,190,170,100));
        header.add(separator,"growx,gaptop 15");
        add(header,"span, growx");
        JPanel banner=SystemBannerFactory.buildMaintenanceBanner();
        if(banner!=null){
            add(banner,"growx");
        }
        JPanel gpaCard=new JPanel(new MigLayout("insets 12 15 12 15"));
        gpaCard.setOpaque(true);
        gpaCard.setBackground(new Color(30,35,45));
        gpaCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(12,15,12,15)
        ));
        lblGPA.setFont(new Font("SansSerif",Font.BOLD,18));
        lblGPA.setForeground(new Color(52,190,170));
        lblGPA.setText("Current GPA: "+String.format("%.2f",studentAPI.calculateGPA(user.getUserId())));
        gpaCard.add(lblGPA);
        add(gpaCard,"growx");
        DefaultTableModel model=new DefaultTableModel(
                new Object[]{"EnrollmentId","Course Code","Course Title","Final Score","Letter","Points","Credits","Semester","Year"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        tblTranscript.setModel(model);
        sorter=new TableRowSorter<>(model);
        tblTranscript.setRowSorter(sorter);
        styleTable(tblTranscript);
        JScrollPane sp=new JScrollPane(tblTranscript);
        sp.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        sp.setBackground(new Color(30,35,45));
        sp.setPreferredSize(new Dimension(760,340));
        add(sp,"growx, pushy");
        JPanel filterCard=new JPanel(new MigLayout("insets 12 15 12 15","[][grow]"));
        filterCard.setOpaque(true);
        filterCard.setBackground(new Color(30,35,45));
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(12,15,12,15)
        ));
        JLabel lblFilter=new JLabel("Search / Filter:");
        lblFilter.setForeground(new Color(220, 220, 230));
        lblFilter.setFont(new Font("SansSerif",Font.PLAIN,13));
        filterCard.add(lblFilter);
        styleField(txtFilter);
        filterCard.add(txtFilter,"growx");
        add(filterCard,"growx");
        txtFilter.getDocument().addDocumentListener(new DocumentListener(){
            private void filter(){
                String text=txtFilter.getText().trim();
                if(text.isEmpty()){
                    sorter.setRowFilter(null);
                }else{
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)"+text));
                }
            }
            @Override public void insertUpdate(DocumentEvent e){filter();}
            @Override public void removeUpdate(DocumentEvent e){filter();}
            @Override public void changedUpdate(DocumentEvent e){filter();}
        });
        JPanel card=new JPanel(new MigLayout("insets 15 0 15 0","[]20[]20[]20[]20[]"));
        card.setOpaque(false);
        JButton btnRefresh=fancyButton("Refresh");
        JButton btnExportCSV=fancyButton("Export CSV");
        JButton btnExportPDF=fancyButton("Export PDF");
        JButton btnDetails=fancyButton("View Component Details");
        JButton btnClose=fancyButton("Close");
        card.add(btnRefresh);
        card.add(btnExportCSV);
        card.add(btnExportPDF);
        card.add(btnDetails);
        card.add(btnClose);
        add(card);
        btnRefresh.addActionListener(e->refreshAll());
        btnExportCSV.addActionListener(e->exportCSV());
        btnExportPDF.addActionListener(e->exportPDF());
        btnDetails.addActionListener(e->showSelectedEnrollmentDetails());
        btnClose.addActionListener(e->dispose());
        loadTranscript();
        setSize(820,650);
        UIUtils.center(this);
    }
    private void refreshAll(){
        lblGPA.setText("Current GPA: "+String.format("%.2f",studentAPI.calculateGPA(user.getUserId())));
        loadTranscript();
    }
    private void styleTable(JTable t){
        t.setRowHeight(26);
        t.setFont(new Font("SansSerif",Font.PLAIN,13));
        t.setSelectionBackground(new Color(52,190,170,80));
        t.setSelectionForeground(new Color(240,240,250));
        t.setBackground(new Color(30,35,45));
        t.setForeground(new Color(240,240,250));
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));
        JTableHeader header=t.getTableHeader();
        header.setBackground(new Color(40,45,55));
        header.setForeground(new Color(52,190,170));
        header.setFont(new Font("SansSerif",Font.BOLD,13));
        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
                Component c=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                if(!isSelected){
                    c.setBackground(row%2==0?new Color(30,35,45):new Color(35,40,50));
                    c.setForeground(new Color(240,240,250));
                }
                return c;
            }
        });
    }
    private JButton fancyButton(String text){
        JButton b=new JButton(text){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2d=(Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isPressed()){
                    g2d.setColor(new Color(38,160,145));
                }else if(getModel().isRollover()){
                    g2d.setColor(new Color(62,210,195));
                }else{
                    g2d.setColor(new Color(52,190,170));
                }
                g2d.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(new Color(15,20,30));
        b.setFont(new Font("SansSerif",Font.BOLD,15));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10,16,10,16));
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        return b;
    }
    private void styleField(JTextField field){
        field.setFont(new Font("SansSerif",Font.PLAIN,14));
        field.setBackground(new Color(40,45,55));
        field.setForeground(new Color(240,240,250));
        field.setCaretColor(new Color(52,190,170));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(80,85,95),1,true),
                new EmptyBorder(8,12,8,12)
        ));
        field.addFocusListener(new java.awt.event.FocusAdapter(){
            public void focusGained(java.awt.event.FocusEvent evt){
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(52,190,170),2,true),
                        new EmptyBorder(7,11,7,11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt){
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(80,85,95),1,true),
                        new EmptyBorder(8,12,8,12)
                ));
            }
        });
    }
    private void loadTranscript(){
        List<TranscriptEntry> transcript=studentAPI.getTranscript(user.getUserId());
        DefaultTableModel m=(DefaultTableModel) tblTranscript.getModel();
        m.setRowCount(0);
        if(transcript!=null){
            for(TranscriptEntry entry:transcript){
                m.addRow(new Object[]{
                        entry.getEnrollmentId(),
                        entry.getCourseCode(),
                        entry.getCourseTitle(),
                        entry.getFinalScore(),
                        entry.getLetterGrade(),
                        entry.getGradePoint(),
                        entry.getCredits(),
                        entry.getSemester(),
                        entry.getYear()
                });
            }
        }
        sorter.modelStructureChanged();
    }
    private void showSelectedEnrollmentDetails(){
        int r=tblTranscript.getSelectedRow();
        if(r==-1){
            UIUtils.error("Select a grade row (enrollment) to view details.");
            return;
        }
        int modelIndex=tblTranscript.convertRowIndexToModel(r);
        Object v=tblTranscript.getModel().getValueAt(modelIndex,0);
        long enrollmentId=(v instanceof Long)?(Long)v:((Number)v).longValue();
        List<Grade> components=gradeAPI.getGradesForEnrollment(enrollmentId);
        if(components==null||components.isEmpty()){
            UIUtils.info("No component grades found for this enrollment.");
            return;
        }
        StringBuilder sb=new StringBuilder();
        for(Grade g:components){
            sb.append(String.format("%s : %.2f \n",g.getComponent(),g.getScore()));
        }
        JTextArea area=new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced",Font.PLAIN,13));
        area.setBackground(new Color(40,45,55));
        area.setForeground(new Color(240,240,250));
        area.setBorder(new EmptyBorder(6,6,6,6));
        JOptionPane.showMessageDialog(this,new JScrollPane(area),"Components for Enrollment "+enrollmentId,JOptionPane.INFORMATION_MESSAGE);
    }
    private void exportCSV(){
        List<TranscriptEntry> transcript=studentAPI.getTranscript(user.getUserId());
        if(transcript==null||transcript.isEmpty()){
            UIUtils.error("No grades available for export.");
            return;
        }
        JFileChooser fc=new JFileChooser();
        fc.setDialogTitle("Save Transcript as CSV");
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            CSVUtils.exportTranscript(transcript,fc.getSelectedFile().getAbsolutePath());
            UIUtils.info("CSV exported successfully!");
        }
    }
    private void exportPDF(){
        List<TranscriptEntry> transcript=studentAPI.getTranscript(user.getUserId());
        if(transcript==null||transcript.isEmpty()){
            UIUtils.error("No grades available for export.");
            return;
        }
        JFileChooser fc=new JFileChooser();
        fc.setDialogTitle("Save Transcript as PDF");
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            PDFUtils.exportTranscriptPDF(transcript,fc.getSelectedFile().getAbsolutePath());
            UIUtils.info("PDF exported successfully!");
        }
    }
    public void open(){
        setVisible(true);
    }
}
