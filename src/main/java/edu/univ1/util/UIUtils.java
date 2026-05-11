package edu.univ1.util;
import javax.swing.*;

public class UIUtils{
    public static void info(String message){
        JOptionPane.showMessageDialog(null,message,"Info",JOptionPane.INFORMATION_MESSAGE);
    }
    public static void error(String message){
        JOptionPane.showMessageDialog(null,message,"Error",JOptionPane.ERROR_MESSAGE);
    }
    public static void center(JFrame frame){
        frame.setLocationRelativeTo(null);
    }
}
