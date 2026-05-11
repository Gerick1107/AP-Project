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
import java.awt.*;

public class AdminUserManagementPanel extends JFrame{
    private final User admin;
    private final AdminAPI adminAPI=new AdminAPI();
    private final UserAuthDAO userAuthDAO=new UserAuthDAO();
    public AdminUserManagementPanel(User admin){
        this.admin=admin;
        setTitle("User Management");
        setSize(620,420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83D\uDC65 Manage Users");
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
        JPanel card=new JPanel(new MigLayout("wrap 1","[grow]","20[]15[]15[]15[]15[]"));
        card.setOpaque(true);
        card.setBackground(new Color(30,35,45));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(25,35,25,35)
        ));
        JButton btnAddStudent=fancyButton("Create Student");
        JButton btnAddInstructor=fancyButton("Create Instructor");
        JButton btnAddAdmin=fancyButton("Create Admin Account");
        JButton btnDeleteUser=fancyButton("Delete User");
        JButton btnClose=fancyButton("Close");
        card.add(btnAddStudent,"growx");
        card.add(btnAddInstructor,"growx");
        card.add(btnAddAdmin,"growx");
        card.add(btnDeleteUser,"growx");
        card.add(btnClose,"growx,gaptop 10");
        add(card,"growx,push,align center");
        btnAddStudent.addActionListener(e->createStudent());
        btnAddInstructor.addActionListener(e->createInstructor());
        btnAddAdmin.addActionListener(e->createAdmin());
        btnDeleteUser.addActionListener(e->deleteUser());
        btnClose.addActionListener(e->dispose());
    }
    public void open(){
        setVisible(true);
    }
    private void createStudent(){
        JTextField txtUsername=new JTextField();
        JPasswordField txtPassword=new JPasswordField();
        JTextField txtRoll=new JTextField();
        JTextField txtProgram=new JTextField();
        JTextField txtYear=new JTextField();
        styleField(txtUsername);
        stylePasswordField(txtPassword);
        styleField(txtRoll);
        styleField(txtProgram);
        styleField(txtYear);
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.setBackground(new Color(30,35,45));
        JLabel lblUser=new JLabel("Username:");
        lblUser.setForeground(new Color(0, 0, 0));
        lblUser.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblUser);
        panel.add(txtUsername,"growx");
        JLabel lblPass=new JLabel("Password:");
        lblPass.setForeground(new Color(0,0,0));
        lblPass.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblPass);
        panel.add(txtPassword,"growx");
        JLabel lblRoll=new JLabel("Roll No:");
        lblRoll.setForeground(new Color(0,0,0));
        lblRoll.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblRoll);
        panel.add(txtRoll,"growx");
        JLabel lblProgram=new JLabel("Program:");
        lblProgram.setForeground(new Color(0,0,0));
        lblProgram.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblProgram);
        panel.add(txtProgram,"growx");
        JLabel lblYear=new JLabel("Year:");
        lblYear.setForeground(new Color(0,0,0));
        lblYear.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblYear);
        panel.add(txtYear,"growx");
        int res=JOptionPane.showConfirmDialog(
                this,panel,"Create Student",JOptionPane.OK_CANCEL_OPTION
        );
        if(res!=JOptionPane.OK_OPTION) return;
        int year;
        try{
            year=Integer.parseInt(txtYear.getText().trim());
        }catch(Exception ex){
            UIUtils.error("Year must be numeric.");
            return;
        }
        String msg=adminAPI.createStudent(
                txtUsername.getText().trim(),
                new String(txtPassword.getPassword()).trim(),
                txtRoll.getText().trim(),
                txtProgram.getText().trim(),
                year
        );
        UIUtils.info(msg);
    }
    private void createInstructor(){
        JTextField txtUsername=new JTextField();
        JPasswordField txtPassword=new JPasswordField();
        JTextField txtDepartment=new JTextField();
        styleField(txtUsername);
        stylePasswordField(txtPassword);
        styleField(txtDepartment);
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.setBackground(new Color(30,35,45));
        JLabel lblUser=new JLabel("Username:");
        lblUser.setForeground(new Color(0,0,0));
        lblUser.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblUser);
        panel.add(txtUsername,"growx");
        JLabel lblPass=new JLabel("Password:");
        lblPass.setForeground(new Color(0,0,0));
        lblPass.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblPass);
        panel.add(txtPassword,"growx");
        JLabel lblDept=new JLabel("Department:");
        lblDept.setForeground(new Color(0,0,0));
        lblDept.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblDept);
        panel.add(txtDepartment,"growx");
        int res=JOptionPane.showConfirmDialog(
                this,panel,"Create Instructor",JOptionPane.OK_CANCEL_OPTION
        );
        if(res!=JOptionPane.OK_OPTION) return;
        String msg=adminAPI.createInstructor(
                txtUsername.getText().trim(),
                new String(txtPassword.getPassword()).trim(),
                txtDepartment.getText().trim()
        );
        UIUtils.info(msg);
    }
    private void createAdmin(){
        JTextField txtUsername=new JTextField();
        JPasswordField txtPassword=new JPasswordField();
        styleField(txtUsername);
        stylePasswordField(txtPassword);
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.setBackground(new Color(30,35,45));
        JLabel lblUser=new JLabel("Username:");
        lblUser.setForeground(new Color(0,0,0));
        lblUser.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblUser);
        panel.add(txtUsername,"growx");
        JLabel lblPass=new JLabel("Password:");
        lblPass.setForeground(new Color(0,0,0));
        lblPass.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblPass);
        panel.add(txtPassword,"growx");
        int res=JOptionPane.showConfirmDialog(
                this,panel,"Create Admin Account",JOptionPane.OK_CANCEL_OPTION
        );
        if(res!=JOptionPane.OK_OPTION) return;
        String msg=adminAPI.createAdmin(
                txtUsername.getText().trim(),
                new String(txtPassword.getPassword()).trim()
        );
        UIUtils.info(msg);
    }
    private void deleteUser(){
        JTextField txtUsername=new JTextField();
        styleField(txtUsername);
        JPanel inputPanel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        inputPanel.setBackground(new Color(30,35,45));
        JLabel lblUser=new JLabel("Username:");
        lblUser.setForeground(new Color(0,0,0));
        lblUser.setFont(new Font("SansSerif",Font.PLAIN,13));
        inputPanel.add(lblUser);
        inputPanel.add(txtUsername,"growx");
        int inputRes=JOptionPane.showConfirmDialog(
                this,inputPanel,"Delete User",JOptionPane.OK_CANCEL_OPTION
        );
        if(inputRes!=JOptionPane.OK_OPTION) return;
        String username=txtUsername.getText().trim();
        if(username.isEmpty()){
            return;
        }
        User user=userAuthDAO.findByUsername(username);
        String roleInfo="";
        if(user!=null){
            String role=userAuthDAO.getRoleByUserId(user.getUserId());
            if(role!=null){
                roleInfo=" (Role: "+role+")";
            }
        }
        int confirm=JOptionPane.showConfirmDialog(
                this,
                "Delete user '"+username+"'"+roleInfo+"? This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if(confirm!=JOptionPane.YES_OPTION){
            return;
        }
        String msg=adminAPI.deleteUser(username);
        UIUtils.info(msg);
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
    private void stylePasswordField(JPasswordField field){
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
}