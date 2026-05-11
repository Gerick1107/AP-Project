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
import java.time.LocalDate;

public class AdminSystemPanel extends JFrame{
    private final User admin;
    private final AdminAPI adminAPI=new AdminAPI();
    private JLabel lblStatus;
    private final SystemAPI systemAPI=new SystemAPI();
    private JTextField txtRegisterDeadline;
    private JTextField txtDropDeadline;
    public AdminSystemPanel(User admin){
        this.admin=admin;
        setTitle("System Controls");
        setSize(620,340);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\u26A0 System Maintenance Controls");
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
        lblStatus=new JLabel();
        lblStatus.setFont(new Font("SansSerif",Font.BOLD,16));
        lblStatus.setBorder(new EmptyBorder(12,12,12,12));
        add(lblStatus,"align center, gaptop 20");
        JPanel deadlines=new JPanel(new MigLayout("wrap 2","[][grow]"));
        deadlines.setOpaque(true);
        deadlines.setBackground(new Color(30,35,45));
        deadlines.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(20,25,20,25)
        ));
        JLabel lblReg=new JLabel("Registration Deadline (YYYY-MM-DD):");
        lblReg.setForeground(new Color(220, 220, 230));
        lblReg.setFont(new Font("SansSerif",Font.PLAIN,13));
        deadlines.add(lblReg);
        txtRegisterDeadline=new JTextField();
        styleField(txtRegisterDeadline);
        deadlines.add(txtRegisterDeadline,"growx");
        JLabel lblDrop=new JLabel("Drop Deadline (YYYY-MM-DD):");
        lblDrop.setForeground(new Color(220, 220, 230));
        lblDrop.setFont(new Font("SansSerif",Font.PLAIN,13));
        deadlines.add(lblDrop);
        txtDropDeadline=new JTextField();
        styleField(txtDropDeadline);
        deadlines.add(txtDropDeadline,"growx");
        JButton btnSaveDeadlines=fancyButton("Save Deadlines");
        deadlines.add(btnSaveDeadlines,"span, align right, gaptop 10");
        add(deadlines,"growx, gaptop 10");
        btnSaveDeadlines.addActionListener(e->saveDeadlines());
        JPanel card=new JPanel(new MigLayout("insets 15 0 15 0","[]20[]20[]20[]20[]"));
        card.setOpaque(false);
        JButton btnEnable=fancyButton("Enable Maintenance Mode");
        JButton btnDisable=fancyButton("Disable Maintenance Mode");
        JButton btnBackup=fancyButton("Backup ERP DB");
        JButton btnRestore=fancyButton("Restore ERP DB");
        JButton btnClose=fancyButton("Close");
        card.add(btnEnable);
        card.add(btnDisable);
        card.add(btnBackup);
        card.add(btnRestore);
        card.add(btnClose);
        add(card,"align center");
        refreshStatus();
        loadDeadlines();
        btnEnable.addActionListener(e->toggle(true));
        btnDisable.addActionListener(e->toggle(false));
        btnBackup.addActionListener(e->backupDatabase());
        btnRestore.addActionListener(e->restoreDatabase());
        btnClose.addActionListener(e->dispose());
    }
    public void open(){
        setVisible(true);
    }
    private void refreshStatus(){
        boolean mode=adminAPI.isMaintenanceMode();
        if(mode){
            lblStatus.setText("System Status: MAINTENANCE MODE ACTIVE");
            lblStatus.setForeground(new Color(255,100,100));
        }else{
            lblStatus.setText("System Status: Normal Operation");
            lblStatus.setForeground(new Color(100,255,100));
        }
    }
    private void toggle(boolean mode){
        String msg=adminAPI.setMaintenanceMode(mode);
        UIUtils.info(msg);
        refreshStatus();
        SystemBannerFactory.refreshBanners();
    }
    private void loadDeadlines(){
        SystemSettings settings=systemAPI.getSettings();
        if(settings==null){
            txtRegisterDeadline.setText("");
            txtDropDeadline.setText("");
            return;
        }
        txtRegisterDeadline.setText(settings.getRegistrationDeadline()!=null
                ?settings.getRegistrationDeadline().toString():"");
        txtDropDeadline.setText(settings.getDropDeadline()!=null
                ?settings.getDropDeadline().toString():"");
    }
    private void saveDeadlines(){
        try{
            LocalDate register=parseDate(txtRegisterDeadline.getText().trim());
            LocalDate drop=parseDate(txtDropDeadline.getText().trim());
            boolean ok=systemAPI.updateDeadlines(register,drop);
            UIUtils.info(ok?"Deadlines updated.":"Failed to update deadlines.");
        }catch(IllegalArgumentException ex){
            UIUtils.error(ex.getMessage());
        }
    }
    private LocalDate parseDate(String value){
        if(value==null||value.isBlank()){
            return null;
        }
        try{
            return LocalDate.parse(value);
        }catch(Exception e){
            throw new IllegalArgumentException("Use YYYY-MM-DD date format.");
        }
    }
    private void backupDatabase(){
        JFileChooser chooser=new JFileChooser();
        chooser.setDialogTitle("Save ERP Backup");
        if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            boolean ok=DatabaseBackupUtil.backup(chooser.getSelectedFile().getAbsolutePath());
            UIUtils.info(ok?"Backup completed.":"Backup failed.");
        }
    }
    private void restoreDatabase(){
        JFileChooser chooser=new JFileChooser();
        chooser.setDialogTitle("Select Backup to Restore");
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            int confirm=JOptionPane.showConfirmDialog(this,
                    "Restoring will overwrite current ERP data. Continue?",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION);
            if(confirm!=JOptionPane.YES_OPTION){
                return;
            }
            boolean ok=DatabaseBackupUtil.restore(chooser.getSelectedFile().getAbsolutePath());
            UIUtils.info(ok?"Restore completed.":"Restore failed.");
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
}