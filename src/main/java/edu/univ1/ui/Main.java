package edu.univ1.ui;
import com.formdev.flatlaf.FlatLightLaf;
public class Main{
    public static void main(String[] args){
        FlatLightLaf.setup();
        javax.swing.SwingUtilities.invokeLater(()->{
            new LoginPage().setVisible(true);
        });
    }
}