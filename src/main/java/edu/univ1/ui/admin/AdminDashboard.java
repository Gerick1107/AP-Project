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

public class AdminDashboard extends JFrame{
    private final User admin;
    public AdminDashboard(User admin){
        this.admin=admin;
        setTitle("Admin Dashboard");
        setSize(560,420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\u2699 Admin Dashboard");
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
        JPanel card=new JPanel(new MigLayout("wrap 1","[grow]","20[]15[]15[]15[]15[]15[]"));
        card.setOpaque(true);
        card.setBackground(new Color(30,35,45));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(25,35,25,35)
        ));
        JButton btnUsers=fancyButton("Manage Users");
        JButton btnCourses=fancyButton("Manage Courses");
        JButton btnSections=fancyButton("Manage Sections");
        JButton btnSystem=fancyButton("System Settings");
        JButton btnNotifications=fancyButton("Notifications");
        JButton btnLogout=logoutButton("Logout");
        card.add(btnUsers,"growx");
        card.add(btnCourses,"growx");
        card.add(btnSections,"growx");
        card.add(btnSystem,"growx");
        card.add(btnNotifications,"growx");
        card.add(btnLogout,"growx,gaptop 10");
        add(card,"growx,push,align center");
        btnUsers.addActionListener(e->new AdminUserManagementPanel(admin).open());
        btnCourses.addActionListener(e->new AdminCoursePanel(admin).open());
        btnSections.addActionListener(e->new AdminSectionPanel(admin).open());
        btnSystem.addActionListener(e->new AdminSystemPanel(admin).open());
        btnNotifications.addActionListener(e->new AdminNotificationPanel(admin).open());
        btnLogout.addActionListener(e->{
            dispose();
            new LoginPage().setVisible(true);
        });
    }
    public void open(){
        setVisible(true);
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
    private JButton logoutButton(String text){
        JButton b=new JButton(text){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2d=(Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isPressed()){
                    g2d.setColor(new Color(30,120,110));
                }else if(getModel().isRollover()){
                    g2d.setColor(new Color(40,150,135));
                }else{
                    g2d.setColor(new Color(35,140,125));
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
}
