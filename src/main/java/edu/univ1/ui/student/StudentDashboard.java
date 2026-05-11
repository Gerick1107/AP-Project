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
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame{
    private final User user;
    private final NotificationAPI notificationAPI=new NotificationAPI();
    private final DefaultListModel<String> notificationModel=new DefaultListModel<>();
    public StudentDashboard(User user){
        this.user=user;
        setTitle("Student Dashboard - "+user.getUsername());
        initUI();
    }
    private void initUI(){
        setLayout(new MigLayout("wrap 2","[grow]20[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83C\uDF93 Student Dashboard");
        title.setForeground(new Color(52,190,170));
        title.setFont(new Font("SansSerif",Font.BOLD,28));
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52,190,170,150),2,true),
                new EmptyBorder(8,20,8,20)
        ));
        header.add(title,"align center");
        JLabel welcome=new JLabel("Welcome, "+user.getUsername());
        welcome.setForeground(new Color(160,170,180));
        welcome.setFont(new Font("SansSerif",Font.ITALIC,20));
        header.add(welcome,"align center,gaptop 8");
        JSeparator separator=new JSeparator();
        separator.setForeground(new Color(52,190,170,100));
        separator.setBackground(new Color(52,190,170,100));
        header.add(separator,"growx,gaptop 15");
        add(header,"span, growx");
        JPanel banner=SystemBannerFactory.buildMaintenanceBanner();
        if(banner!=null){
            add(banner,"span, growx");
        }
        JPanel notificationPanel=buildNotificationPanel();
        add(notificationPanel,"span, growx");
        JPanel card=new JPanel(new MigLayout("wrap 1","[grow]","20[]15[]15[]15[]15[]"));
        card.setOpaque(true);
        card.setBackground(new Color(30,35,45));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(25,35,25,35)
        ));
        JButton btnProfile=fancyButton("Profile");
        JButton btnEnroll=fancyButton("Enroll / Catalog");
        JButton btnGrades=fancyButton("Grades / Transcript");
        JButton btnTimetable=fancyButton("Timetable");
        JButton btnLogout=logoutButton("Logout");
        card.add(btnProfile,"growx");
        card.add(btnEnroll,"growx");
        card.add(btnGrades,"growx");
        card.add(btnTimetable,"growx");
        card.add(btnLogout,"growx,gaptop 10");
        add(card,"span, growx, push, align center");
        btnProfile.addActionListener(e->new StudentProfilePanel(user).open());
        btnEnroll.addActionListener(e->new StudentEnrollmentPanel(user).open());
        btnGrades.addActionListener(e->new StudentGradesPanel(user).open());
        btnTimetable.addActionListener(e->new StudentTimetablePanel(user).open());
        btnLogout.addActionListener(e->{
            dispose();
            new LoginPage().setVisible(true);
        });
        setSize(540,360);
        UIUtils.center(this);
    }
    private JPanel buildNotificationPanel(){
        JPanel card=new JPanel(new MigLayout("insets 12 15 12 15","[grow]"));
        card.setOpaque(true);
        card.setBackground(new Color(30,35,45));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(12,15,12,15)
        ));
        JLabel lblTitle=new JLabel("Notifications");
        lblTitle.setForeground(new Color(52,190,170));
        lblTitle.setFont(new Font("SansSerif",Font.BOLD,16));
        card.add(lblTitle,"growx");
        JList<String> list=new JList<>(notificationModel);
        list.setVisibleRowCount(4);
        list.setFont(new Font("SansSerif",Font.PLAIN,12));
        list.setBackground(new Color(40,45,55));
        list.setForeground(new Color(240,240,250));
        list.setSelectionBackground(new Color(52,190,170,80));
        list.setSelectionForeground(new Color(240,240,250));
        JScrollPane scroll=new JScrollPane(list);
        scroll.setBorder(new LineBorder(new Color(80,85,95),1,true));
        scroll.setBackground(new Color(30,35,45));
        JButton btnRefresh=fancyButton("Refresh");
        JButton btnClear=fancyButton("Clear");
        card.add(scroll,"growx, span");
        card.add(btnRefresh,"split 2, right");
        card.add(btnClear);
        btnRefresh.addActionListener(e->loadNotifications());
        btnClear.addActionListener(e->notificationModel.clear());
        loadNotifications();
        return card;
    }
    private void loadNotifications(){
        notificationModel.clear();
        List<Notification> notices=notificationAPI.getStudentNotifications();
        if(notices==null||notices.isEmpty()){
            notificationModel.addElement("No notifications right now.");
            return;
        }
        for(Notification n:notices){
            StringBuilder line=new StringBuilder();
            if(n.getTitle()!=null&&!n.getTitle().isBlank()){
                line.append(n.getTitle()).append(" - ");
            }
            line.append(n.getMessage());
            notificationModel.addElement(line.toString());
        }
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
