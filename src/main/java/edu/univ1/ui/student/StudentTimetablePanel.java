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

public class StudentTimetablePanel extends JFrame{
    private final User user;
    private final StudentAPI studentAPI=new StudentAPI();
    private final JTable tblTimetable=new JTable();
    private final JTextField txtFilter=new JTextField(18);
    private TableRowSorter<DefaultTableModel> sorter;
    public StudentTimetablePanel(User user){
        super("My Timetable");
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
        JLabel title=new JLabel("\u23F0 My Timetable");
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
        tblTimetable.setModel(new DefaultTableModel(new Object[]{"Section","Course","Instructor","Day/Time","Room","Semester","Year"},0){
            public boolean isCellEditable(int r,int c){return false;}
        });
        styleTable(tblTimetable);
        sorter=new TableRowSorter<>((DefaultTableModel) tblTimetable.getModel());
        tblTimetable.setRowSorter(sorter);
        JScrollPane sc=new JScrollPane(tblTimetable);
        sc.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        sc.setBackground(new Color(30,35,45));
        add(sc,"growx, pushy");
        JPanel bottom=new JPanel(new MigLayout("","[]20[]",""));
        bottom.setOpaque(false);
        JButton btnRefresh=fancyButton("Refresh");
        JButton btnClose=fancyButton("Close");
        bottom.add(btnRefresh);
        bottom.add(btnClose);
        add(bottom,"growx");
        btnRefresh.addActionListener(e->loadTimetable());
        btnClose.addActionListener(e->dispose());
        txtFilter.getDocument().addDocumentListener(new DocumentListener(){
            private void update(){
                if(sorter==null) return;
                String text=txtFilter.getText().trim();
                if(text.isEmpty()){
                    sorter.setRowFilter(null);
                }else{
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)"+java.util.regex.Pattern.quote(text)));
                }
            }
            @Override public void insertUpdate(DocumentEvent e){update();}
            @Override public void removeUpdate(DocumentEvent e){update();}
            @Override public void changedUpdate(DocumentEvent e){update();}
        });
        loadTimetable();
        setSize(700,480);
        UIUtils.center(this);
    }
    private void styleTable(JTable t){
        t.setRowHeight(28);
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
    private void loadTimetable(){
        List<Section> secs=studentAPI.getTimetable(user.getUserId());
        DefaultTableModel m=(DefaultTableModel) tblTimetable.getModel();
        m.setRowCount(0);
        if(secs!=null){
            for(Section s:secs){
                m.addRow(new Object[]{
                        s.getSectionId(),
                        formatCourse(s),
                        valueOrPlaceholder(s.getInstructorName()),
                        s.getDayTime(),
                        s.getRoom(),
                        s.getSemester(),
                        s.getYear()
                });
            }
        }
    }
    private String formatCourse(Section s){
        if(s.getCourseCode()==null&&s.getCourseTitle()==null){
            return "Course "+s.getCourseId();
        }
        if(s.getCourseCode()==null) return s.getCourseTitle();
        if(s.getCourseTitle()==null) return s.getCourseCode();
        return s.getCourseCode()+" — "+s.getCourseTitle();
    }
    private String valueOrPlaceholder(String value){
        return(value==null||value.isBlank())?"-":value;
    }
    public void open(){
        setVisible(true);
    }
}