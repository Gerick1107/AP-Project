package edu.univ1.ui;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.service.*;
import edu.univ1.ui.admin.*;
import edu.univ1.ui.student.*;
import edu.univ1.ui.instructor.*;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class LoginPage extends JFrame{
    private final AuthAPI authAPI=new AuthAPI();
    public LoginPage(){
        setTitle("IIITD ERP - Login");
        setSize(520,420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("wrap 1,insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel logoLabel=createLogoLabel();
        if(logoLabel!=null){
            header.add(logoLabel,"align center");
        }
        JLabel title=new JLabel("IIITD ERP");
        title.setForeground(new Color(52,190,170));
        title.setFont(new Font("SansSerif",Font.BOLD,38));
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52,190,170,150),2,true),
                new EmptyBorder(8,20,8,20)
        ));
        header.add(title,"align center,gaptop 12");
        JLabel subtitle=new JLabel("Student Information System");
        subtitle.setForeground(new Color(160,170,180));
        subtitle.setFont(new Font("SansSerif",Font.ITALIC,13));
        header.add(subtitle,"align center,gaptop 8");
        JSeparator separator=new JSeparator();
        separator.setForeground(new Color(52,190,170,100));
        separator.setBackground(new Color(52,190,170,100));
        header.add(separator, "growx, gaptop 15");
        add(header, "growx");
        JPanel banner=SystemBannerFactory.buildMaintenanceBanner();
        if(banner!=null){
            add(banner,"growx");
        }
        JPanel card=new JPanel(new MigLayout("wrap 1","[grow]","20[]10[]10[]20[]"));
        card.setOpaque(true);
        card.setBackground(new Color(30,35,45));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(25,35,25,35)
        ));
        JLabel lblLogin=new JLabel("Login To Continue");
        lblLogin.setFont(new Font("SansSerif",Font.BOLD,20));
        lblLogin.setForeground(new Color(52,190,170));
        JLabel lblUser=new JLabel("Username:");
        lblUser.setForeground(new Color(220,220,230));
        lblUser.setFont(new Font("SansSerif",Font.PLAIN,13));
        JTextField txtUser=new JTextField(15);
        styleField(txtUser);
        JLabel lblPass=new JLabel("Password:");
        lblPass.setForeground(new Color(220,220,230));
        lblPass.setFont(new Font("SansSerif",Font.PLAIN,13));
        JPasswordField txtPass=new JPasswordField(15);
        stylePasswordField(txtPass);
        JCheckBox chkShow=new JCheckBox("Show Password");
        chkShow.setOpaque(false);
        chkShow.setForeground(new Color(180,180,190));
        chkShow.addActionListener(e->
                txtPass.setEchoChar(chkShow.isSelected() ? (char)0 : '•')
        );
        JButton btnLogin=fancyButton("Login");
        JButton btnChangePassword=fancyButton("Change Password");
        card.add(lblLogin,"align center,gaptop 10");
        card.add(lblUser,"gaptop 10");
        card.add(txtUser,"growx");
        card.add(lblPass,"gaptop 10");
        card.add(txtPass,"growx");
        card.add(chkShow,"gaptop 5");
        card.add(btnLogin,"gaptop 15,growx");
        card.add(btnChangePassword,"growx");
        add(card, "growx,push,align center");
        btnLogin.addActionListener(e->{
            String username=txtUser.getText().trim();
            String password=new String(txtPass.getPassword());
            LoginResult result=authAPI.login(username,password);
            if(result==null){
                UIUtils.error("Unable to process login right now.");
                return;
            }
            if(result.getStatus()==LoginStatus.SUCCESS && result.getUser()!=null){
                UIUtils.info("Login successful!");
                dispose();
                Router.route(result.getUser());
                return;
            }
            if(result.getStatus()==LoginStatus.PASSWORD_CHANGE_REQUIRED && result.getUser()!=null){
                handlePasswordReset(result, password);
                return;
            }
            if(result.getStatus()==LoginStatus.ACCOUNT_LOCKED){
                UIUtils.error(result.getMessage()!=null ? result.getMessage() : "Account locked. Please change your password.");
                if(result.getUser()!=null){
                    openSelfServicePasswordChange(result.getUser().getUsername());
                }else{
                    openSelfServicePasswordChange(username);
                }
                return;
            }
            String message=result.getMessage()!=null ? result.getMessage() : "Invalid username or password.";
            UIUtils.error(message);
        });
        btnChangePassword.addActionListener(e->openSelfServicePasswordChange());
    }
    private void handlePasswordReset(LoginResult result,String currentPassword){
        JPasswordField txtNew=new JPasswordField();
        JPasswordField txtConfirm=new JPasswordField();
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.add(new JLabel("New Password:"));
        panel.add(txtNew);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(txtConfirm);
        int res=JOptionPane.showConfirmDialog(
                this,panel,"Password Change Required",JOptionPane.OK_CANCEL_OPTION
        );
        if(res!=JOptionPane.OK_OPTION){
            UIUtils.error("Password change is required to continue.");
            return;
        }
        String newPass=new String(txtNew.getPassword()).trim();
        String confirm=new String(txtConfirm.getPassword()).trim();
        if(newPass.isEmpty()||confirm.isEmpty()){
            UIUtils.error("Password cannot be empty.");
            return;
        }
        if(!newPass.equals(confirm)){
            UIUtils.error("Passwords do not match.");
            return;
        }
        boolean changed=authAPI.changePassword(result.getUser().getUserId(),currentPassword,newPass);
        if(changed){
            UIUtils.info("Password updated. Logging you in...");
            dispose();
            Router.route(result.getUser());
        }else{
            UIUtils.error("Failed to update password. Please try again.");
        }
    }
    private void openSelfServicePasswordChange(String presetUsername){
        JTextField txtUsername=new JTextField();
        if(presetUsername!=null){
            txtUsername.setText(presetUsername);
        }
        JPasswordField txtCurrent=new JPasswordField();
        JPasswordField txtNew=new JPasswordField();
        JPasswordField txtConfirm=new JPasswordField();
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.add(new JLabel("Username:"));
        panel.add(txtUsername);
        panel.add(new JLabel("Current Password:"));
        panel.add(txtCurrent);
        panel.add(new JLabel("New Password:"));
        panel.add(txtNew);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(txtConfirm);
        int res=JOptionPane.showConfirmDialog(
                this,panel,"Change Password",JOptionPane.OK_CANCEL_OPTION
        );
        if(res!=JOptionPane.OK_OPTION){
            return;
        }
        String username=txtUsername.getText().trim();
        String current=new String(txtCurrent.getPassword()).trim();
        String newPass=new String(txtNew.getPassword()).trim();
        String confirm=new String(txtConfirm.getPassword()).trim();
        if(username.isEmpty()||current.isEmpty()||newPass.isEmpty()){
            UIUtils.error("All fields are required.");
            return;
        }
        if(!newPass.equals(confirm)){
            UIUtils.error("Passwords do not match.");
            return;
        }
        LoginResult loginResult=authAPI.login(username,current);
        boolean authenticated=
                loginResult!=null &&
                loginResult.getUser()!=null &&
                (loginResult.getStatus()==LoginStatus.SUCCESS
                        ||loginResult.getStatus()==LoginStatus.PASSWORD_CHANGE_REQUIRED
                        ||loginResult.getStatus()==LoginStatus.ACCOUNT_LOCKED);
        if(!authenticated){
            UIUtils.error("Invalid credentials provided.");
            return;
        }
        boolean changed=authAPI.changePassword(loginResult.getUser().getUserId(),current,newPass);
        if(changed){
            UIUtils.info("Password updated successfully. Please log in with your new password.");
        }else{
            UIUtils.error("Failed to change password.");
        }
    }
    private void openSelfServicePasswordChange(){
        openSelfServicePasswordChange(null);
    }
    private JLabel createLogoLabel(){
        try{
            java.net.URL logoUrl=getClass().getResource("/logo.png");
            if(logoUrl!=null){
                ImageIcon originalIcon=new ImageIcon(logoUrl);
                Image originalImage=originalIcon.getImage();
                int targetHeight=60;
                int targetWidth=(int)(originalImage.getWidth(null)*(double)targetHeight/originalImage.getHeight(null));
                Image scaledImage=originalImage.getScaledInstance(targetWidth,targetHeight,Image.SCALE_SMOOTH);
                ImageIcon scaledIcon=new ImageIcon(scaledImage);
                JLabel logoLabel=new JLabel(scaledIcon);
                logoLabel.setOpaque(false);
                return logoLabel;
            }else{
                System.out.println("Logo image not found at /logo.png. Please place logo.png in src/main/resources/");
                return null;
            }
        }catch(Exception e){
            System.out.println("Error loading logo: "+e.getMessage());
            return null;
        }
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
}