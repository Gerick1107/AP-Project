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
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminSectionPanel extends JFrame{
    private final User admin;
    private final AdminAPI adminAPI=new AdminAPI();
    private final CatalogAPI catalogAPI=new CatalogAPI();
    private final CourseDAO courseDAO=new CourseDAO();
    private final InstructorDAO instructorDAO=new InstructorDAO();
    private final DirectoryService directoryService=new DirectoryService();
    private JTable tbl;
    private final JTextField txtFilter=new JTextField(18);
    private TableRowSorter<DefaultTableModel> sorter;
    private final Map<Long,Section> sectionIndex=new HashMap<>();
    public AdminSectionPanel(User admin){
        this.admin=admin;
        setTitle("Manage Sections");
        setSize(1020,640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83D\uDDC2 Section Management");
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
        tbl=new JTable();
        styleTable(tbl);
        JScrollPane sp=new JScrollPane(tbl);
        sp.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        sp.setBackground(new Color(30,35,45));
        add(sp,"grow, push");
        loadSections();
        JPanel card=new JPanel(new MigLayout("insets 15 0 15 0","[]20[]20[]20[]"));
        card.setOpaque(false);
        JButton btnAdd=fancyButton("Add Section");
        JButton btnUpdate=fancyButton("Update Selected");
        JButton btnDelete=fancyButton("Delete Selected");
        JButton btnClose=fancyButton("Close");
        card.add(btnAdd);
        card.add(btnUpdate);
        card.add(btnDelete);
        card.add(btnClose);
        add(card,"align center");
        btnAdd.addActionListener(e->addSection());
        btnUpdate.addActionListener(e->updateSection());
        btnDelete.addActionListener(e->deleteSection());
        btnClose.addActionListener(e->dispose());
        attachFilter();
    }
    public void open(){
        setVisible(true);
    }
    private void loadSections(){
        List<Section> list=catalogAPI.getAllSections();
        DefaultTableModel model=new DefaultTableModel(
                new Object[]{"Section","Course","Instructor","Day/Time","Room","Capacity","Semester","Year"},
                0
        ){
            @Override public boolean isCellEditable(int row,int column){return false;}
        };
        sectionIndex.clear();
        for(Section s:list){
            sectionIndex.put(s.getSectionId(),s);
            model.addRow(new Object[]{
                    s.getSectionId(),
                    formatCourse(s),
                    valueOrPlaceholder(s.getInstructorName()),
                    s.getDayTime(),
                    s.getRoom(),
                    s.getCapacity(),
                    s.getSemester(),
                    s.getYear()
            });
        }
        tbl.setModel(model);
        sorter=new TableRowSorter<>(model);
        tbl.setRowSorter(sorter);
        applyFilter();
    }
    private void addSection(){
        List<Course> courses=courseDAO.getAllCourses();
        List<Instructor> instructors=instructorDAO.getAllInstructors();
        List<Long> instructorIds=instructors.stream().map(Instructor::getUserId).collect(Collectors.toList());
        Map<Long,String> names=directoryService.getUsernames(instructorIds);
        for(Instructor inst:instructors){
            String username=names.get(inst.getUserId());
            inst.setDisplayName(username!=null?username:"User "+inst.getUserId());
        }
        JComboBox<Course> cboCourse=new JComboBox<>(courses.toArray(new Course[0]));
        JComboBox<Instructor> cboInstructor=new JComboBox<>(instructors.toArray(new Instructor[0]));
        styleComboBox(cboCourse);
        styleComboBox(cboInstructor);
        JTextField txtDT=new JTextField();
        JTextField txtRoom=new JTextField();
        JTextField txtCap=new JTextField();
        JTextField txtSem=new JTextField();
        JTextField txtYear=new JTextField();
        styleField(txtDT);
        styleField(txtRoom);
        styleField(txtCap);
        styleField(txtSem);
        styleField(txtYear);
        JPanel p=new JPanel(new MigLayout("wrap 2","[][grow]"));
        p.setBackground(new Color(30,35,45));
        JLabel lblCourse=new JLabel("Course:");
        lblCourse.setForeground(new Color(0, 0, 0));
        lblCourse.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblCourse);
        p.add(cboCourse,"growx");
        JLabel lblInstructor=new JLabel("Instructor:");
        lblInstructor.setForeground(new Color(0, 0, 0));
        lblInstructor.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblInstructor);
        p.add(cboInstructor,"growx");
        JLabel lblDT=new JLabel("Day/Time:");
        lblDT.setForeground(new Color(0, 0, 0));
        lblDT.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblDT);
        p.add(txtDT,"growx");
        JLabel lblRoom=new JLabel("Room:");
        lblRoom.setForeground(new Color(0, 0, 0));
        lblRoom.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblRoom);
        p.add(txtRoom,"growx");
        JLabel lblCap=new JLabel("Capacity:");
        lblCap.setForeground(new Color(0, 0, 0));
        lblCap.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblCap);
        p.add(txtCap,"growx");
        JLabel lblSem=new JLabel("Semester:");
        lblSem.setForeground(new Color(0, 0, 0));
        lblSem.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblSem);
        p.add(txtSem,"growx");
        JLabel lblYear=new JLabel("Year:");
        lblYear.setForeground(new Color(0, 0, 0));
        lblYear.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblYear);
        p.add(txtYear,"growx");
        int res=JOptionPane.showConfirmDialog(this,p,"Add Section",JOptionPane.OK_CANCEL_OPTION);
        if(res!=JOptionPane.OK_OPTION) return;
        int cap,year;
        try{
            cap=Integer.parseInt(txtCap.getText().trim());
            year=Integer.parseInt(txtYear.getText().trim());
            if (cap < 0) {
                UIUtils.error("Capacity cannot be negative.");
                return;
            }
        }catch(Exception e){
            UIUtils.error("Capacity and Year must be numeric.");
            return;
        }
        Course c=(Course) cboCourse.getSelectedItem();
        Instructor inst=(Instructor) cboInstructor.getSelectedItem();
        String msg=adminAPI.addSection(
                c.getCourseId(),
                inst.getUserId(),
                txtDT.getText().trim(),
                txtRoom.getText().trim(),
                cap,
                txtSem.getText().trim(),
                year
        );
        UIUtils.info(msg);
        loadSections();
    }
    private void updateSection(){
        int row=tbl.getSelectedRow();
        if(row==-1){
            UIUtils.error("Select a section first.");
            return;
        }
        int modelRow=tbl.convertRowIndexToModel(row);
        long sectionId=((Number) tbl.getModel().getValueAt(modelRow,0)).longValue();
        Section existing=sectionIndex.get(sectionId);
        if(existing==null){
            UIUtils.error("Section metadata unavailable.");
            return;
        }
        long courseId=existing.getCourseId();
        long instructorUserId=existing.getInstructorId();
        String dt=tbl.getValueAt(row,3).toString();
        String room=tbl.getValueAt(row,4).toString();
        int cap=Integer.parseInt(tbl.getValueAt(row,5).toString());
        String sem=tbl.getValueAt(row,6).toString();
        int year=Integer.parseInt(tbl.getValueAt(row,7).toString());
        List<Course> courses=courseDAO.getAllCourses();
        List<Instructor> instructors=instructorDAO.getAllInstructors();
        List<Long> instructorIds=instructors.stream().map(Instructor::getUserId).collect(Collectors.toList());
        Map<Long,String> names=directoryService.getUsernames(instructorIds);
        for(Instructor inst:instructors){
            String username=names.get(inst.getUserId());
            inst.setDisplayName(username!=null?username:"User "+inst.getUserId());
        }
        JComboBox<Course> cboCourse=new JComboBox<>(courses.toArray(new Course[0]));
        JComboBox<Instructor> cboInstructor=new JComboBox<>(instructors.toArray(new Instructor[0]));
        styleComboBox(cboCourse);
        styleComboBox(cboInstructor);
        for(Course c:courses){
            if(c.getCourseId()==courseId) cboCourse.setSelectedItem(c);
        }
        for(Instructor i:instructors){
            if(i.getUserId()==instructorUserId) cboInstructor.setSelectedItem(i);
        }
        JTextField txtDT=new JTextField(dt);
        JTextField txtRoom=new JTextField(room);
        JTextField txtCap=new JTextField(String.valueOf(cap));
        JTextField txtSem=new JTextField(sem);
        JTextField txtYear=new JTextField(String.valueOf(year));
        styleField(txtDT);
        styleField(txtRoom);
        styleField(txtCap);
        styleField(txtSem);
        styleField(txtYear);
        JPanel p=new JPanel(new MigLayout("wrap 2","[][grow]"));
        p.setBackground(new Color(30,35,45));
        JLabel lblCourse=new JLabel("Course:");
        lblCourse.setForeground(new Color(0, 0, 0));
        lblCourse.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblCourse);
        p.add(cboCourse,"growx");
        JLabel lblInstructor=new JLabel("Instructor:");
        lblInstructor.setForeground(new Color(0, 0, 0));
        lblInstructor.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblInstructor);
        p.add(cboInstructor,"growx");
        JLabel lblDT=new JLabel("Day/Time:");
        lblDT.setForeground(new Color(0, 0, 0));
        lblDT.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblDT);
        p.add(txtDT,"growx");
        JLabel lblRoom=new JLabel("Room:");
        lblRoom.setForeground(new Color(0, 0, 0));
        lblRoom.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblRoom);
        p.add(txtRoom,"growx");
        JLabel lblCap=new JLabel("Capacity:");
        lblCap.setForeground(new Color(0, 0, 0));
        lblCap.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblCap);
        p.add(txtCap,"growx");
        JLabel lblSem=new JLabel("Semester:");
        lblSem.setForeground(new Color(0, 0, 0));
        lblSem.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblSem);
        p.add(txtSem,"growx");
        JLabel lblYear=new JLabel("Year:");
        lblYear.setForeground(new Color(0, 0, 0));
        lblYear.setFont(new Font("SansSerif",Font.PLAIN,13));
        p.add(lblYear);
        p.add(txtYear,"growx");
        int res=JOptionPane.showConfirmDialog(this,p,"Update Section",JOptionPane.OK_CANCEL_OPTION);
        if(res!=JOptionPane.OK_OPTION) return;
        int newCap,newYear;
        try{
            newCap=Integer.parseInt(txtCap.getText().trim());
            newYear=Integer.parseInt(txtYear.getText().trim());
            if (newCap < 0) {
                UIUtils.error("Capacity cannot be negative.");
                return;
            }
        }catch(Exception e){
            UIUtils.error("Capacity and Year must be numeric.");
            return;
        }
        Section updated=new Section(
                sectionId,
                ((Course) cboCourse.getSelectedItem()).getCourseId(),
                ((Instructor) cboInstructor.getSelectedItem()).getUserId(),
                txtDT.getText().trim(),
                txtRoom.getText().trim(),
                newCap,
                txtSem.getText().trim(),
                newYear
        );
        String msg=adminAPI.updateSection(updated);
        UIUtils.info(msg);
        loadSections();
    }
    private void deleteSection(){
        int row=tbl.getSelectedRow();
        if(row==-1){
            UIUtils.error("Select a section to delete.");
            return;
        }
        int modelRow=tbl.convertRowIndexToModel(row);
        long sectionId=((Number) tbl.getModel().getValueAt(modelRow,0)).longValue();
        int confirm=JOptionPane.showConfirmDialog(
                this,
                "Delete section "+sectionId+"? Any enrolled students will prevent deletion.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if(confirm!=JOptionPane.YES_OPTION){
            return;
        }
        String msg=adminAPI.deleteSection(sectionId);
        UIUtils.info(msg);
        loadSections();
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
}