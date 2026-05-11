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

public class StudentProfilePanel extends JFrame{
    private final User user;
    private final StudentAPI studentAPI=new StudentAPI();
    public StudentProfilePanel(User user){
        this.user=user;
        setTitle("My Profile");
        setSize(550,420);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83D\uDC64 My Profile");
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
        Student s=studentAPI.getProfile(user.getUserId());
        if(s==null){
            UIUtils.error("Unable to load profile.");
            dispose();
            return;
        }
        JPanel card=new JPanel(new MigLayout("wrap 2","[grow][grow]","12[]12[]12[]12[]12"));
        card.setBackground(new Color(30,35,45));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(20,25,20,25)
        ));
        card.setOpaque(true);
        Font labelFont=new Font("SansSerif",Font.BOLD,14);
        Font valueFont=new Font("SansSerif",Font.PLAIN,14);
        JLabel l1=new JLabel("Username:");
        l1.setFont(labelFont);
        l1.setForeground(new Color(220, 220, 230));
        JLabel v1=new JLabel(user.getUsername());
        v1.setFont(valueFont);
        v1.setForeground(new Color(240,240,250));
        JLabel l2=new JLabel("Roll Number:");
        l2.setFont(labelFont);
        l2.setForeground(new Color(220, 220, 230));
        JLabel v2=new JLabel(s.getRollNo());
        v2.setFont(valueFont);
        v2.setForeground(new Color(240,240,250));
        JLabel l3=new JLabel("Program:");
        l3.setFont(labelFont);
        l3.setForeground(new Color(220, 220, 230));
        JLabel v3=new JLabel(s.getProgram());
        v3.setFont(valueFont);
        v3.setForeground(new Color(240,240,250));
        JLabel l4=new JLabel("Year:");
        l4.setFont(labelFont);
        l4.setForeground(new Color(220, 220, 230));
        JLabel v4=new JLabel(String.valueOf(s.getYear()));
        v4.setFont(valueFont);
        v4.setForeground(new Color(240,240,250));
        card.add(l1);
        card.add(v1,"growx");
        card.add(l2);
        card.add(v2,"growx");
        card.add(l3);
        card.add(v3,"growx");
        card.add(l4);
        card.add(v4,"growx");
        add(card,"growx, gaptop 10");
        JPanel btnPanel=new JPanel(new MigLayout("insets 10 0 0 0","[grow]"));
        btnPanel.setOpaque(false);
        JButton btnClose=fancyButton("Close");
        btnPanel.add(btnClose,"center");
        add(btnPanel,"growx");
        btnClose.addActionListener(e->dispose());
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
    public void open(){
        setVisible(true);
    }
}
