package edu.univ1.ui.admin;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.service.*;
import edu.univ1.ui.student.*;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.List;

public class AdminCoursePanel extends JFrame{
    private final User admin;
    private final AdminAPI adminAPI=new AdminAPI();
    private final CourseDAO courseDAO=new CourseDAO();
    private JTable tblCourses;
    private final JTextField txtFilter=new JTextField(18);
    private TableRowSorter<DefaultTableModel> sorter;
    public AdminCoursePanel(User admin){
        this.admin=admin;
        setTitle("Manage Courses");
        setSize(880,560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83D\uDCD6 Course Management");
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
        tblCourses=new JTable();
        styleTable(tblCourses);
        JScrollPane sp=new JScrollPane(tblCourses);
        sp.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        sp.setBackground(new Color(30,35,45));
        add(sp,"grow, push");
        loadCourses();
        JPanel card=new JPanel(new MigLayout("insets 15 0 15 0","[]20[]20[]20[]20[]"));
        card.setOpaque(false);
        JButton btnAdd=fancyButton("Add Course");
        JButton btnUpdate=fancyButton("Update Selected");
        JButton btnDelete=fancyButton("Delete Selected");
        JButton btnExport=fancyButton("Export CSV");
        JButton btnClose=fancyButton("Close");
        card.add(btnAdd);
        card.add(btnUpdate);
        card.add(btnDelete);
        card.add(btnExport);
        card.add(btnClose);
        add(card,"align center");
        btnAdd.addActionListener(e->addCourse());
        btnUpdate.addActionListener(e->updateCourse());
        btnDelete.addActionListener(e->deleteCourse());
        btnExport.addActionListener(e->exportCourses());
        btnClose.addActionListener(e->dispose());
        attachFilter();
    }
    public void open(){
        setVisible(true);
    }
    private void loadCourses(){
        List<Course> list=courseDAO.getAllCourses();
        DefaultTableModel model=new DefaultTableModel(
                new Object[]{"Course ID","Code","Title","Credits"},0
        );
        for(Course c:list){
            model.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCode(),
                    c.getTitle(),
                    c.getCredits()
            });
        }
        tblCourses.setModel(model);
        sorter=new TableRowSorter<>(model);
        tblCourses.setRowSorter(sorter);
        applyFilter();
    }
    private void addCourse(){
        JTextField txtCode=new JTextField();
        JTextField txtTitle=new JTextField();
        JTextField txtCredits=new JTextField();
        styleField(txtCode);
        styleField(txtTitle);
        styleField(txtCredits);
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.setBackground(new Color(30,35,45));
        JLabel lblCode=new JLabel("Code:");
        lblCode.setForeground(new Color(0,0,0));
        lblCode.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblCode);
        panel.add(txtCode,"growx");
        JLabel lblTitle=new JLabel("Title:");
        lblTitle.setForeground(new Color(0,0,0));
        lblTitle.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblTitle);
        panel.add(txtTitle,"growx");
        JLabel lblCredits=new JLabel("Credits:");
        lblCredits.setForeground(new Color(0,0,0));
        lblCredits.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblCredits);
        panel.add(txtCredits,"growx");
        int res=JOptionPane.showConfirmDialog(
                this,panel,"Add Course",
                JOptionPane.OK_CANCEL_OPTION
        );
        if(res!=JOptionPane.OK_OPTION) return;
        int credits;
        try{
            credits=Integer.parseInt(txtCredits.getText().trim());
        }catch(Exception e){
            UIUtils.error("Credits must be numeric.");
            return;
        }
        String msg=adminAPI.addCourse(
                txtCode.getText().trim(),
                txtTitle.getText().trim(),
                credits
        );
        UIUtils.info(msg);
        loadCourses();
    }
    private void updateCourse(){
        int row=tblCourses.getSelectedRow();
        if(row==-1){
            UIUtils.error("Select a course first.");
            return;
        }
        int modelRow=tblCourses.convertRowIndexToModel(row);
        long courseId=((Number) tblCourses.getModel().getValueAt(modelRow,0)).longValue();
        String code=tblCourses.getModel().getValueAt(modelRow,1).toString();
        String title=tblCourses.getModel().getValueAt(modelRow,2).toString();
        int credits=Integer.parseInt(tblCourses.getModel().getValueAt(modelRow,3).toString());
        JTextField txtCode=new JTextField(code);
        JTextField txtTitle=new JTextField(title);
        JTextField txtCredits=new JTextField(String.valueOf(credits));
        styleField(txtCode);
        styleField(txtTitle);
        styleField(txtCredits);
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.setBackground(new Color(30,35,45));
        JLabel lblCode=new JLabel("Code:");
        lblCode.setForeground(new Color(0,0,0));
        lblCode.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblCode);
        panel.add(txtCode,"growx");
        JLabel lblTitle=new JLabel("Title:");
        lblTitle.setForeground(new Color(0,0,0));
        lblTitle.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblTitle);
        panel.add(txtTitle,"growx");
        JLabel lblCredits=new JLabel("Credits:");
        lblCredits.setForeground(new Color(0,0,0));
        lblCredits.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblCredits);
        panel.add(txtCredits,"growx");
        int res=JOptionPane.showConfirmDialog(
                this,panel,"Update Course",
                JOptionPane.OK_CANCEL_OPTION
        );
        if(res!=JOptionPane.OK_OPTION) return;
        int updatedCredits;
        try{
            updatedCredits=Integer.parseInt(txtCredits.getText().trim());
        }catch(Exception e){
            UIUtils.error("Credits must be numeric.");
            return;
        }
        Course updatedCourse=new Course(courseId,txtCode.getText().trim(),txtTitle.getText().trim(),updatedCredits);
        String msg=adminAPI.updateCourse(updatedCourse);
        UIUtils.info(msg);
        loadCourses();
    }
    private void deleteCourse(){
        int row=tblCourses.getSelectedRow();
        if(row==-1){
            UIUtils.error("Select a course to delete.");
            return;
        }
        int modelRow=tblCourses.convertRowIndexToModel(row);
        long courseId=((Number) tblCourses.getModel().getValueAt(modelRow,0)).longValue();
        String code=tblCourses.getModel().getValueAt(modelRow,1).toString();
        int confirm=JOptionPane.showConfirmDialog(
                this,
                "Delete course "+code+"? This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if(confirm!=JOptionPane.YES_OPTION){
            return;
        }
        String msg=adminAPI.deleteCourse(courseId);
        UIUtils.info(msg);
        loadCourses();
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
    private void attachFilter(){
        txtFilter.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e){applyFilter();}
            @Override public void removeUpdate(DocumentEvent e){applyFilter();}
            @Override public void changedUpdate(DocumentEvent e){applyFilter();}
        });
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
    private void exportCourses(){
        List<Course> courses=courseDAO.getAllCourses();
        if(courses==null||courses.isEmpty()){
            UIUtils.error("No courses available for export.");
            return;
        }
        JFileChooser fc=new JFileChooser();
        fc.setDialogTitle("Export Courses as CSV");
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)","csv"));
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            String filePath=fc.getSelectedFile().getAbsolutePath();
            if(!filePath.toLowerCase().endsWith(".csv")){
                filePath+=".csv";
            }
            CSVUtils.exportCourses(courses,filePath);
            UIUtils.info("Courses exported successfully to "+filePath);
        }
    }
}