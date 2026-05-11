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

public class StudentEnrollmentPanel extends JFrame{
    private final User user;
    private final CatalogAPI catalogAPI=new CatalogAPI();
    private final EnrollmentAPI enrollAPI=new EnrollmentAPI();
    private final StudentAPI studentAPI=new StudentAPI();
    private final JTextField txtSearch=new JTextField(20);
    private final JTable tblCourses=new JTable();
    private final JTable tblSections=new JTable();
    private final JTable tblSchedule=new JTable();
    private final JTextField txtSectionFilter=new JTextField(18);
    private final JTextField txtScheduleFilter=new JTextField(18);
    private TableRowSorter<DefaultTableModel> courseSorter;
    private TableRowSorter<DefaultTableModel> sectionSorter;
    private TableRowSorter<DefaultTableModel> scheduleSorter;
    public StudentEnrollmentPanel(User user){
        super("Course Catalog & Enrollment");
        this.user=user;
        initUI();
    }
    private void initUI(){
        setLayout(new MigLayout("wrap 2","[grow]20[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83D\uDCD6 Course Catalog & Enrollment");
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
            add(banner,"span, growx");
        }
        JPanel searchCard=new JPanel(new MigLayout("insets 12 15 12 15","[]10[grow][]"));
        searchCard.setOpaque(true);
        searchCard.setBackground(new Color(30,35,45));
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(12,15,12,15)
        ));
        JLabel lblSearch=new JLabel("Search courses (code or title):");
        lblSearch.setForeground(new Color(220, 220, 230));
        lblSearch.setFont(new Font("SansSerif",Font.PLAIN,13));
        searchCard.add(lblSearch);
        styleField(txtSearch);
        searchCard.add(txtSearch,"growx");
        JButton btnSearch=fancyButton("Search");
        searchCard.add(btnSearch);
        add(searchCard,"span, growx");
        add(newSectionHeader("Courses"),"growx");
        add(newSectionHeader("Sections & Schedule"),"growx, wrap");
        tblCourses.setModel(new DefaultTableModel(new Object[]{"CourseId","Code","Title","Credits"},0){
            public boolean isCellEditable(int r,int c){return false;}
        });
        styleTable(tblCourses);
        courseSorter=new TableRowSorter<>((DefaultTableModel) tblCourses.getModel());
        tblCourses.setRowSorter(courseSorter);
        JScrollPane spCourses=new JScrollPane(tblCourses);
        spCourses.setPreferredSize(new Dimension(420,240));
        spCourses.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        spCourses.setBackground(new Color(30,35,45));
        add(spCourses,"growx");
        JPanel right=new JPanel(new MigLayout("wrap 1","[grow]"));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(8,8,8,8));
        JPanel sectionsHeaderRow=new JPanel(new MigLayout("insets 0","[grow][]8[]",""));
        sectionsHeaderRow.setOpaque(false);
        sectionsHeaderRow.add(newSectionHeader("Sections"),"growx");
        JLabel lblFilter1=new JLabel("Filter:");
        lblFilter1.setForeground(new Color(220, 220, 230));
        lblFilter1.setFont(new Font("SansSerif",Font.PLAIN,13));
        sectionsHeaderRow.add(lblFilter1);
        styleField(txtSectionFilter);
        sectionsHeaderRow.add(txtSectionFilter,"w 160!");
        right.add(sectionsHeaderRow,"growx");
        tblSections.setModel(new DefaultTableModel(new Object[]{"Section","Course","Instructor","Schedule","Room","Capacity"},0){
            public boolean isCellEditable(int r,int c){return false;}
        });
        styleTable(tblSections);
        sectionSorter=new TableRowSorter<>((DefaultTableModel) tblSections.getModel());
        tblSections.setRowSorter(sectionSorter);
        JScrollPane spSections=new JScrollPane(tblSections);
        spSections.setPreferredSize(new Dimension(420,160));
        spSections.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        spSections.setBackground(new Color(30,35,45));
        right.add(spSections,"growx");
        JButton btnRegister=fancyButton("Register Selected Section");
        right.add(btnRegister,"growx, gapy 8");
        JPanel scheduleHeaderRow=new JPanel(new MigLayout("insets 10 0 0 0","[grow][]8[]",""));
        scheduleHeaderRow.setOpaque(false);
        JLabel lblSchedule=new JLabel("My Schedule");
        lblSchedule.setForeground(new Color(52,190,170));
        lblSchedule.setFont(new Font("SansSerif",Font.BOLD,16));
        scheduleHeaderRow.add(lblSchedule,"growx");
        JLabel lblFilter2=new JLabel("Filter:");
        lblFilter2.setForeground(new Color(220, 220, 230));
        lblFilter2.setFont(new Font("SansSerif",Font.PLAIN,13));
        scheduleHeaderRow.add(lblFilter2);
        styleField(txtScheduleFilter);
        scheduleHeaderRow.add(txtScheduleFilter,"w 160!");
        right.add(scheduleHeaderRow,"growx");
        tblSchedule.setModel(new DefaultTableModel(new Object[]{"Section","Course","Schedule","Room","Instructor"},0){
            public boolean isCellEditable(int r,int c){return false;}
        });
        styleTable(tblSchedule);
        scheduleSorter=new TableRowSorter<>((DefaultTableModel) tblSchedule.getModel());
        tblSchedule.setRowSorter(scheduleSorter);
        JScrollPane spSched=new JScrollPane(tblSchedule);
        spSched.setPreferredSize(new Dimension(420,120));
        spSched.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        spSched.setBackground(new Color(30,35,45));
        right.add(spSched,"growx");
        JButton btnDrop=fancyButton("Drop Selected Section");
        right.add(btnDrop,"growx, gapy 8");
        add(right,"growx");
        attachFilter(txtSectionFilter,sectionSorter);
        attachFilter(txtScheduleFilter,scheduleSorter);
        JPanel footer=new JPanel(new MigLayout("insets 10 0 0 0","[grow]"));
        footer.setOpaque(false);
        JButton btnClose=fancyButton("Close");
        footer.add(btnClose,"align center");
        add(footer,"span, growx");
        btnClose.addActionListener(e->dispose());
        btnSearch.addActionListener(e->doSearch());
        tblCourses.getSelectionModel().addListSelectionListener(e->{
            int r=tblCourses.getSelectedRow();
            if(r!=-1){
                int modelRow=tblCourses.convertRowIndexToModel(r);
                Object v=tblCourses.getModel().getValueAt(modelRow,0);
                long courseId=(v instanceof Long)?(Long)v:((Number)v).longValue();
                loadSectionsForCourse(courseId);
            }
        });
        btnRegister.addActionListener(e->registerSelectedSection());
        btnDrop.addActionListener(e->dropSelectedSection());
        loadAllCourses();
        loadSchedule();
        setSize(980,640);
        UIUtils.center(this);
    }
    private JLabel newSectionHeader(String text){
        JLabel l=new JLabel(text);
        l.setForeground(new Color(52,190,170));
        l.setFont(new Font("SansSerif",Font.BOLD,16));
        l.setBorder(new EmptyBorder(6,0,6,0));
        return l;
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
    private void attachFilter(JTextField field,TableRowSorter<DefaultTableModel> sorter){
        DocumentListener listener=new DocumentListener(){
            private void update(){
                if(sorter==null) return;
                String text=field.getText().trim();
                if(text.isEmpty()){
                    sorter.setRowFilter(null);
                }else{
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)"+java.util.regex.Pattern.quote(text)));
                }
            }
            @Override public void insertUpdate(DocumentEvent e){update();}
            @Override public void removeUpdate(DocumentEvent e){update();}
            @Override public void changedUpdate(DocumentEvent e){update();}
        };
        field.getDocument().addDocumentListener(listener);
    }
    private void doSearch(){
        String q=txtSearch.getText().trim();
        List<Course> results;
        if(q.isEmpty()){
            results=catalogAPI.getAllCourses();
        }else{
            results=catalogAPI.searchCourses(q);
        }
        DefaultTableModel m=(DefaultTableModel) tblCourses.getModel();
        m.setRowCount(0);
        if(results!=null){
            for(Course c:results){
                m.addRow(new Object[]{c.getCourseId(),c.getCode(),c.getTitle(),c.getCredits()});
            }
        }
    }
    private void loadAllCourses(){
        List<Course> courses=catalogAPI.getAllCourses();
        DefaultTableModel m=(DefaultTableModel) tblCourses.getModel();
        m.setRowCount(0);
        if(courses!=null){
            for(Course c:courses){
                m.addRow(new Object[]{c.getCourseId(),c.getCode(),c.getTitle(),c.getCredits()});
            }
        }
    }
    private void loadSectionsForCourse(long courseId){
        List<Section> secs=catalogAPI.getSectionsForCourse(courseId);
        DefaultTableModel m=(DefaultTableModel) tblSections.getModel();
        m.setRowCount(0);
        if(secs!=null){
            for(Section s:secs){
                m.addRow(new Object[]{
                        s.getSectionId(),
                        formatCourse(s),
                        valueOrPlaceholder(s.getInstructorName()),
                        s.getDayTime(),
                        s.getRoom(),
                        s.getCapacity()
                });
            }
        }
    }
    private void loadSchedule(){
        List<Section> sched=enrollAPI.getSchedule(user.getUserId());
        DefaultTableModel m=(DefaultTableModel) tblSchedule.getModel();
        m.setRowCount(0);
        if(sched!=null){
            for(Section s:sched){
                m.addRow(new Object[]{
                        s.getSectionId(),
                        formatCourse(s),
                        s.getDayTime(),
                        s.getRoom(),
                        valueOrPlaceholder(s.getInstructorName())
                });
            }
        }
    }
    private String formatCourse(Section s){
        if(s.getCourseCode()==null&&s.getCourseTitle()==null){
            return "Course "+s.getCourseId();
        }
        if(s.getCourseCode()==null){
            return s.getCourseTitle();
        }
        if(s.getCourseTitle()==null){
            return s.getCourseCode();
        }
        return s.getCourseCode()+" — "+s.getCourseTitle();
    }
    private String valueOrPlaceholder(String value){
        return(value==null||value.isBlank())?"-":value;
    }
    private void registerSelectedSection(){
        int r=tblSections.getSelectedRow();
        if(r==-1){
            UIUtils.error("Select a section to register.");
            return;
        }
        int modelRow=tblSections.convertRowIndexToModel(r);
        Object v=tblSections.getModel().getValueAt(modelRow,0);
        long sectionId=(v instanceof Long)?(Long)v:((Number)v).longValue();
        String res=enrollAPI.register(user.getUserId(),sectionId);
        UIUtils.info(res);
        loadSchedule();
    }
    private void dropSelectedSection(){
        int r=tblSchedule.getSelectedRow();
        if(r==-1){
            UIUtils.error("Select a section from your schedule to drop.");
            return;
        }
        int modelRow=tblSchedule.convertRowIndexToModel(r);
        Object v=tblSchedule.getModel().getValueAt(modelRow,0);
        long sectionId=(v instanceof Long)?(Long)v:((Number)v).longValue();
        String res=enrollAPI.drop(user.getUserId(),sectionId);
        UIUtils.info(res);
        loadSchedule();
    }
    public void open(){
        setVisible(true);
    }
}
