package edu.univ1.ui.instructor;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.service.*;
import edu.univ1.ui.student.*;
import edu.univ1.ui.admin.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorGradePanel extends JFrame{
    private final User user;
    private final long sectionId;
    private final InstructorAPI instructorAPI=new InstructorAPI();
    private final GradeAPI gradeAPI=new GradeAPI();
    private final EnrollmentDAO enrollmentDAO=new EnrollmentDAO();
    private final JTable tbl=new JTable();
    private TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtFilter=new JTextField(18);
    private Map<Long,List<Grade>> gradesByEnrollment=new HashMap<>();
    private Map<Long,Enrollment> enrollmentMetaById=new HashMap<>();
    public InstructorGradePanel(User user,long sectionId){
        this.user=user;
        this.sectionId=sectionId;
        setTitle("Grade Entry - Section "+sectionId);
        setSize(980,600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        buildHeader();
        buildFilter();
        buildTable();
        buildButtons();
        loadTable();
    }
    public void open(){
        setVisible(true);
    }
    private void buildHeader(){
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83D\uDCCB Enter / Update Grades");
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
        add(header,"growx");
        JPanel banner=SystemBannerFactory.buildMaintenanceBanner();
        if(banner!=null){
            add(banner,"growx");
        }
    }
    private void buildFilter(){
        JPanel filterCard=new JPanel(new MigLayout("insets 12 15 12 15","[][grow]",""));
        filterCard.setOpaque(true);
        filterCard.setBackground(new Color(30,35,45));
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(12,15,12,15)
        ));
        JLabel lblSearch=new JLabel("Search:");
        lblSearch.setForeground(new Color(220, 220, 230));
        lblSearch.setFont(new Font("SansSerif",Font.PLAIN,13));
        filterCard.add(lblSearch,"right");
        styleField(txtFilter);
        filterCard.add(txtFilter,"growx");
        add(filterCard,"growx");
        txtFilter.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e){applyFilter();}
            @Override public void removeUpdate(DocumentEvent e){applyFilter();}
            @Override public void changedUpdate(DocumentEvent e){applyFilter();}
        });
    }
    private void buildTable(){
        styleTable(tbl);
        JScrollPane scroll=new JScrollPane(tbl);
        scroll.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        scroll.setBackground(new Color(30,35,45));
        add(scroll,"grow, push");
    }
    private void buildButtons(){
        JPanel card=new JPanel(new MigLayout("insets 15 0 15 0","[]20[]20[]20[]20[]20[]"));
        card.setOpaque(false);
        JButton btnAddComponent=fancyButton("Add Component");
        JButton btnUpdateComponent=fancyButton("Update Component");
        JButton btnDeleteComponent=fancyButton("Delete Component");
        JButton btnRefresh=fancyButton("Refresh");
        JButton btnStats=fancyButton("Class Stats");
        JButton btnClose=fancyButton("Close");
        card.add(btnAddComponent);
        card.add(btnUpdateComponent);
        card.add(btnDeleteComponent);
        card.add(btnRefresh);
        card.add(btnStats);
        card.add(btnClose);
        add(card,"align center");
        btnAddComponent.addActionListener(e->addComponent());
        btnUpdateComponent.addActionListener(e->updateComponent());
        btnDeleteComponent.addActionListener(e->deleteComponent());
        btnRefresh.addActionListener(e->loadTable());
        btnStats.addActionListener(e->showStats());
        btnClose.addActionListener(e->dispose());
    }
    private void loadTable(){
        List<Student> students=instructorAPI.getStudentsInSection(sectionId);
        List<Grade> grades=gradeAPI.getGradesForSection(sectionId);
        List<Enrollment> enrollments=enrollmentDAO.getBySection(sectionId);
        gradesByEnrollment=new HashMap<>();
        for(Grade g:grades){
            gradesByEnrollment.computeIfAbsent(g.getEnrollmentId(),k->new ArrayList<>()).add(g);
        }
        Map<Long,Long> enrollmentByStudent=new HashMap<>();
        enrollmentMetaById=new HashMap<>();
        for(Enrollment e:enrollments){
            enrollmentByStudent.put(e.getStudentId(),e.getEnrollmentId());
            enrollmentMetaById.put(e.getEnrollmentId(),e);
        }
        DefaultTableModel model=new DefaultTableModel(
                new Object[]{"Student","Roll No","Program","Year","Components","Final Score","Letter","EnrollmentID"},
                0
        ){
            @Override public boolean isCellEditable(int row,int column){return false;}
        };
        for(Student s:students){
            Long enrollmentId=enrollmentByStudent.get(s.getUserId());
            List<Grade> studentGrades=(enrollmentId!=null)
                    ?gradesByEnrollment.getOrDefault(enrollmentId,new ArrayList<>())
                    :new ArrayList<>();
            Enrollment summary=enrollmentId!=null?enrollmentMetaById.get(enrollmentId):null;
            String studentName=s.getDisplayName()!=null?s.getDisplayName():String.valueOf(s.getUserId());
            double totalWeight=0;
            for(Grade g:studentGrades){
                totalWeight+=g.getWeight();
            }
            String finalLetter="-";
            String finalScore="-";
            if(Math.abs(totalWeight-100.0)<0.01){
                if(summary!=null&&summary.getFinalLetter()!=null){
                    finalLetter=summary.getFinalLetter();
                }
                if(summary!=null&&summary.getFinalScore()>0){
                    finalScore=String.format("%.2f",summary.getFinalScore());
                }
            }
            model.addRow(new Object[]{
                    studentName,
                    s.getRollNo(),
                    s.getProgram(),
                    s.getYear(),
                    formatComponents(studentGrades),
                    finalScore,
                    finalLetter,
                    enrollmentId
            });
        }
        tbl.setModel(model);
        if(tbl.getColumnModel().getColumnCount()>7){
            var hidden=tbl.getColumnModel().getColumn(7);
            hidden.setMinWidth(0);
            hidden.setMaxWidth(0);
            hidden.setPreferredWidth(0);
        }
        if(sorter==null){
            sorter=new TableRowSorter<>(model);
            tbl.setRowSorter(sorter);
        }else{
            sorter.setModel(model);
        }
        applyFilter();
    }
    private void addComponent(){
        long selectedEnrollmentId=getSelectedEnrollmentId();
        if(selectedEnrollmentId<=0){
            UIUtils.error("Select a student row first.");
            return;
        }
        List<Enrollment> enrollments=enrollmentDAO.getBySection(sectionId);
        if(enrollments==null||enrollments.isEmpty()){
            UIUtils.error("No students enrolled in this section.");
            return;
        }
        List<Grade> existingGrades=gradeAPI.getGradesForEnrollment(selectedEnrollmentId);
        double totalWeight=0;
        for(Grade g:existingGrades){
            totalWeight+=g.getWeight();
        }
        JTextField txtComponent=new JTextField();
        JTextField txtScore=new JTextField("0");
        JTextField txtMaxScore=new JTextField("100");
        JTextField txtWeight=new JTextField("0");
        styleField(txtComponent);
        styleField(txtScore);
        styleField(txtMaxScore);
        styleField(txtWeight);
        JPanel panel=buildGradeForm(txtComponent,txtScore,txtMaxScore,txtWeight);
        int result=JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Grade Component",
                JOptionPane.OK_CANCEL_OPTION
        );
        if(result!=JOptionPane.OK_OPTION) return;
        String component=txtComponent.getText().trim();
        if(component.isEmpty()){
            UIUtils.error("Component name cannot be empty.");
            return;
        }
        String scoreStr=txtScore.getText().trim();
        double score;
        try{
            score=Double.parseDouble(scoreStr);
        }catch(Exception ex){
            UIUtils.error("Score must be numeric.");
            return;
        }
        double maxScore;
        try{
            maxScore=Double.parseDouble(txtMaxScore.getText().trim());
        }catch(Exception ex){
            UIUtils.error("Max score must be numeric.");
            return;
        }
        double weight;
        try{
            weight=Double.parseDouble(txtWeight.getText().trim());
        }catch(Exception ex){
            UIUtils.error("Weight must be numeric.");
            return;
        }
        if(totalWeight+weight>100){
            UIUtils.error("Total weightage cannot exceed 100%. Current total: "+String.format("%.1f",totalWeight)+"%");
            return;
        }
        int successCount=0;
        for(Enrollment e:enrollments){
            double studentScore=(e.getEnrollmentId()==selectedEnrollmentId)?score:0;
            String message=gradeAPI.addGrade(e.getEnrollmentId(),component,studentScore,maxScore,weight);
            if(message.contains("saved")){
                successCount++;
            }
        }
        UIUtils.info("Component added for "+successCount+" student(s).");
        loadTable();
    }
    private void updateComponent(){
        long enrollmentId=getSelectedEnrollmentId();
        if(enrollmentId<=0){
            UIUtils.error("Select a student row first.");
            return;
        }
        List<Grade> components=gradesByEnrollment.get(enrollmentId);
        if(components==null||components.isEmpty()){
            UIUtils.error("No components found for this enrollment.");
            return;
        }
        JComboBox<Grade> cboComponents=new JComboBox<>(components.toArray(new Grade[0]));
        styleComboBox(cboComponents);
        cboComponents.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus){
                Component c=super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                if(value instanceof Grade g){
                    setText(g.getComponent()+" ("+String.format("%.1f",g.getScore())+"/"+String.format("%.1f",g.getMaxScore())+")");
                }
                return c;
            }
        });
        Grade initial=components.get(0);
        JTextField txtComponent=new JTextField(initial.getComponent());
        JTextField txtScore=new JTextField(String.valueOf(initial.getScore()));
        JTextField txtMaxScore=new JTextField(String.valueOf(initial.getMaxScore()));
        JTextField txtWeight=new JTextField(String.valueOf(initial.getWeight()));
        styleField(txtComponent);
        styleField(txtScore);
        styleField(txtMaxScore);
        styleField(txtWeight);
        cboComponents.addActionListener(e->{
            Grade selected=(Grade) cboComponents.getSelectedItem();
            if(selected!=null){
                txtComponent.setText(selected.getComponent());
                txtScore.setText(String.valueOf(selected.getScore()));
                txtMaxScore.setText(String.valueOf(selected.getMaxScore()));
                txtWeight.setText(String.valueOf(selected.getWeight()));
            }
        });
        JPanel panel=new JPanel(new MigLayout("wrap 1","[grow]"));
        panel.setBackground(new Color(30,35,45));
        JPanel selector=new JPanel(new MigLayout("insets 0","[][grow]",""));
        selector.setOpaque(false);
        JLabel lblComp=new JLabel("Component:");
        lblComp.setForeground(new Color(0, 0, 0));
        lblComp.setFont(new Font("SansSerif",Font.PLAIN,13));
        selector.add(lblComp);
        selector.add(cboComponents,"growx");
        panel.add(selector,"growx");
        panel.add(buildGradeForm(txtComponent,txtScore,txtMaxScore,txtWeight),"growx");
        int result=JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Grade Component",
                JOptionPane.OK_CANCEL_OPTION
        );
        if(result!=JOptionPane.OK_OPTION) return;
        Grade selected=(Grade) cboComponents.getSelectedItem();
        if(selected==null){
            UIUtils.error("No component selected.");
            return;
        }
        String component=txtComponent.getText().trim();
        String scoreStr=txtScore.getText().trim();
        double score;
        try{
            score=Double.parseDouble(scoreStr);
        }catch(Exception ex){
            UIUtils.error("Score must be numeric.");
            return;
        }
        double maxScore;
        try{
            maxScore=Double.parseDouble(txtMaxScore.getText().trim());
        }catch(Exception ex){
            UIUtils.error("Max score must be numeric.");
            return;
        }
        double weight;
        try{
            weight=Double.parseDouble(txtWeight.getText().trim());
        }catch(Exception ex){
            UIUtils.error("Weight must be numeric.");
            return;
        }
        double totalWeight=0;
        for(Grade g:components){
            if(g.getGradeId()!=selected.getGradeId()){
                totalWeight+=g.getWeight();
            }
        }
        if(totalWeight+weight>100){
            UIUtils.error("Total weightage cannot exceed 100%. Current total (excluding this component): "+String.format("%.1f",totalWeight)+"%");
            return;
        }
        boolean weightChanged=Math.abs(selected.getWeight()-weight)>0.01;
        boolean maxScoreChanged=Math.abs(selected.getMaxScore()-maxScore)>0.01;
        if(weightChanged||maxScoreChanged){
            String message=gradeAPI.updateComponentWeightAndMaxScoreForSection(sectionId,selected.getComponent(),maxScore,weight);
            gradeAPI.updateGrade(enrollmentId,selected.getGradeId(),component,score,maxScore,weight);
            UIUtils.info(message);
        }else{
            String message=gradeAPI.updateGrade(enrollmentId,selected.getGradeId(),component,score,maxScore,weight);
            UIUtils.info(message);
        }
        loadTable();
    }
    private JPanel buildGradeForm(JTextField txtComponent,JTextField txtScore,JTextField txtMaxScore,JTextField txtWeight){
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.setBackground(new Color(30,35,45));
        JLabel lblComp=new JLabel("Component:");
        lblComp.setForeground(new Color(0, 0, 0));
        lblComp.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblComp);
        panel.add(txtComponent,"growx");
        JLabel lblScore=new JLabel("Score:");
        lblScore.setForeground(new Color(0, 0, 0));
        lblScore.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblScore);
        panel.add(txtScore,"growx");
        JLabel lblMax=new JLabel("Out of:");
        lblMax.setForeground(new Color(0, 0, 0));
        lblMax.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblMax);
        panel.add(txtMaxScore,"growx");
        JLabel lblWeight=new JLabel("Weight (0-100):");
        lblWeight.setForeground(new Color(0, 0, 0));
        lblWeight.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblWeight);
        panel.add(txtWeight,"growx");
        return panel;
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
    private long getSelectedEnrollmentId(){
        int row=tbl.getSelectedRow();
        if(row==-1){
            return -1;
        }
        int modelRow=tbl.convertRowIndexToModel(row);
        Object enrollmentObj=tbl.getModel().getValueAt(modelRow,7);
        if(enrollmentObj instanceof Number num&&num.longValue()>0){
            return num.longValue();
        }
        return -1;
    }
    private String formatComponents(List<Grade> grades){
        if(grades==null||grades.isEmpty()){
            return "No components yet";
        }
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<grades.size();i++){
            Grade g=grades.get(i);
            sb.append(g.getComponent())
                    .append(": ")
                    .append(String.format("%.1f/%.1f",g.getScore(),g.getMaxScore()))
                    .append(" (")
                    .append(String.format("%.0f%%",g.getWeight()))
                    .append(")");
            if(i<grades.size()-1){
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
    private void deleteComponent(){
        long enrollmentId=getSelectedEnrollmentId();
        if(enrollmentId<=0){
            UIUtils.error("Select a student row first.");
            return;
        }
        List<Grade> components=gradesByEnrollment.get(enrollmentId);
        if(components==null||components.isEmpty()){
            UIUtils.error("No components found for this enrollment.");
            return;
        }
        JComboBox<Grade> cboComponents=new JComboBox<>(components.toArray(new Grade[0]));
        styleComboBox(cboComponents);
        cboComponents.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus){
                Component c=super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                if(value instanceof Grade g){
                    setText(g.getComponent()+" ("+String.format("%.1f",g.getScore())+"/"+String.format("%.1f",g.getMaxScore())+")");
                }
                return c;
            }
        });
        JPanel panel=new JPanel(new MigLayout("wrap 1","[grow]"));
        panel.setBackground(new Color(30,35,45));
        JLabel lblSelect=new JLabel("Select component to delete:");
        lblSelect.setForeground(new Color(0, 0, 0));
        lblSelect.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblSelect);
        panel.add(cboComponents,"growx");
        int result=JOptionPane.showConfirmDialog(
                this,
                panel,
                "Delete Grade Component",
                JOptionPane.OK_CANCEL_OPTION
        );
        if(result!=JOptionPane.OK_OPTION) return;
        Grade selected=(Grade) cboComponents.getSelectedItem();
        if(selected==null){
            UIUtils.error("No component selected.");
            return;
        }
        int confirm=JOptionPane.showConfirmDialog(
                this,
                "Delete component '"+selected.getComponent()+"' for all students? This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if(confirm!=JOptionPane.YES_OPTION){
            return;
        }
        String message=gradeAPI.deleteComponentForSection(sectionId,selected.getComponent());
        UIUtils.info(message);
        loadTable();
    }
    private void showStats(){
        GradeStats stats=gradeAPI.getSectionStats(sectionId);
        if(stats==null){
            UIUtils.info("No scores available yet.");
            return;
        }
        String message=String.format(
                """
                        Average: %.2f
                        Mean: %.2f
                        Median: %.2f
                        Min: %.2f
                        Max: %.2f
                        """,
                stats.getAverage(),
                stats.getMean(),
                stats.getMedian(),
                stats.getMin(),
                stats.getMax()
        );
        UIUtils.info(message);
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
            public Component getTableCellRendererComponent(
                    JTable table,Object value,boolean isSelected,
                    boolean hasFocus,int row,int col){
                Component c=super.getTableCellRendererComponent(
                        table,value,isSelected,hasFocus,row,col);
                if(!isSelected){
                    c.setBackground(row%2==0?new Color(30,35,45):new Color(35,40,50));
                    c.setForeground(new Color(240,240,250));
                }
                return c;
            }
        });
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
    private void styleComboBox(JComboBox<?> combo){
        combo.setFont(new Font("SansSerif",Font.PLAIN,14));
        combo.setBackground(new Color(40,45,55));
        combo.setForeground(new Color(240,240,250));
        combo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(80,85,95),1,true),
                new EmptyBorder(8,12,8,12)
        ));
    }
    private void applyFilter(){
        if(sorter==null) return;
        String query=txtFilter.getText().trim();
        if(query.isEmpty()){
            sorter.setRowFilter(null);
        }else{
            sorter.setRowFilter(RowFilter.regexFilter("(?i)"+java.util.regex.Pattern.quote(query)));
        }
    }
}