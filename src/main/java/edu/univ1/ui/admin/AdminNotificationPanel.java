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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AdminNotificationPanel extends JFrame{
    private final User admin;
    private final NotificationAPI notificationAPI=new NotificationAPI();
    private final DefaultTableModel tableModel=new DefaultTableModel(
            new Object[]{"ID","Title","Audience","Category","Created"},0){
        @Override
        public boolean isCellEditable(int row,int column){
            return false;
        }
    };
    public AdminNotificationPanel(User admin){
        this.admin=admin;
        setTitle("Notifications");
        setSize(680,460);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new MigLayout("wrap 1","[grow]"));
        getContentPane().setBackground(new Color(15,20,30));
        UIUtils.center(this);
        JPanel header=new JPanel(new MigLayout("insets 25 20 20 20","[grow]"));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title=new JLabel("\uD83D\uDCE3 Broadcast Notifications");
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
        JPanel composer=buildComposer();
        add(composer,"growx");
        JTable table=new JTable(tableModel);
        styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane scroll=new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(52,190,170,100),2,true));
        scroll.setBackground(new Color(30,35,45));
        add(scroll,"grow, push");
        JButton btnDelete=fancyButton("Delete Selected");
        btnDelete.addActionListener(e->{
            int row=table.getSelectedRow();
            if(row==-1){
                UIUtils.error("Select a notification to delete.");
                return;
            }
            long id=(long) tableModel.getValueAt(row,0);
            if(notificationAPI.delete(id)){
                UIUtils.info("Notification deleted.");
                loadTable();
            }else{
                UIUtils.error("Unable to delete notification.");
            }
        });
        add(btnDelete,"align right");
        loadTable();
    }
    private JPanel buildComposer(){
        JPanel panel=new JPanel(new MigLayout("wrap 2","[][grow]"));
        panel.setOpaque(true);
        panel.setBackground(new Color(30,35,45));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52,190,170,100),2,true),
                new EmptyBorder(20,25,20,25)
        ));
        JLabel lblTitle=new JLabel("Title:");
        lblTitle.setForeground(new Color(220, 220, 230));
        lblTitle.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblTitle);
        JTextField txtTitle=new JTextField();
        styleField(txtTitle);
        panel.add(txtTitle,"growx");
        JLabel lblMessage=new JLabel("Message:");
        lblMessage.setForeground(new Color(220,220,230));
        lblMessage.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblMessage,"top");
        JTextArea txtMessage=new JTextArea(4,20);
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        styleTextArea(txtMessage);
        panel.add(new JScrollPane(txtMessage),"growx");
        JLabel lblExpires=new JLabel("Expires (YYYY-MM-DD, optional):");
        lblExpires.setForeground(new Color(220, 220, 230));
        lblExpires.setFont(new Font("SansSerif",Font.PLAIN,13));
        panel.add(lblExpires);
        JTextField txtExpires=new JTextField();
        styleField(txtExpires);
        panel.add(txtExpires,"growx");
        JButton btnSend=fancyButton("Send to Students");
        btnSend.addActionListener(e->{
            String title=txtTitle.getText().trim();
            String message=txtMessage.getText().trim();
            String expires=txtExpires.getText().trim();
            if(message.isEmpty()){
                UIUtils.error("Message cannot be empty.");
                return;
            }
            LocalDate expiryDate=null;
            if(!expires.isBlank()){
                try{
                    expiryDate=LocalDate.parse(expires);
                }catch(Exception ex){
                    UIUtils.error("Invalid date format. Use YYYY-MM-DD.");
                    return;
                }
            }
            long id=notificationAPI.sendManualNotification(
                    title.isEmpty()?"Notice":title,
                    message,
                    admin.getUsername(),
                    expiryDate
            );
            if(id>0){
                UIUtils.info("Notification sent.");
                txtTitle.setText("");
                txtMessage.setText("");
                txtExpires.setText("");
                loadTable();
            }else{
                UIUtils.error("Failed to send notification.");
            }
        });
        panel.add(btnSend,"span, align right, gaptop 10");
        return panel;
    }
    private void loadTable(){
        tableModel.setRowCount(0);
        List<Notification> list=notificationAPI.getRecentNotifications(50);
        if(list==null){
            return;
        }
        for(Notification n:list){
            tableModel.addRow(new Object[]{
                    n.getNotificationId(),
                    n.getTitle(),
                    n.getAudience(),
                    n.getCategory(),
                    n.getCreatedAt()
            });
        }
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
    private void styleTable(JTable t){
        t.setRowHeight(24);
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
    private void styleTextArea(JTextArea area){
        area.setFont(new Font("SansSerif",Font.PLAIN,14));
        area.setBackground(new Color(40,45,55));
        area.setForeground(new Color(240,240,250));
        area.setCaretColor(new Color(52,190,170));
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(80,85,95),1,true),
                new EmptyBorder(8,12,8,12)
        ));
    }
}