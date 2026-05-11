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
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SystemBannerFactory {
    private static final SystemAPI systemAPI=new SystemAPI();
    private static final List<MaintenanceBannerPanel> BANNERS=new CopyOnWriteArrayList<>();
    public static JPanel buildMaintenanceBanner(){
        MaintenanceBannerPanel panel=new MaintenanceBannerPanel();
        BANNERS.add(panel);
        panel.addHierarchyListener(new HierarchyListener(){
            @Override
            public void hierarchyChanged(HierarchyEvent e){
                if((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED)!=0 && !panel.isDisplayable()){
                    BANNERS.remove(panel);
                }
            }
        });
        panel.refresh();
        return panel;
    }
    public static void refreshBanners(){
        BANNERS.forEach(MaintenanceBannerPanel::refresh);
    }
    private static class MaintenanceBannerPanel extends JPanel{
        private final JLabel label;
        MaintenanceBannerPanel(){
            super(new BorderLayout());
            setBackground(new Color(255,230,200));
            label=new JLabel("MAINTENANCE MODE ACTIVE - Some actions may be disabled.");
            label.setForeground(new Color(140,60,0));
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            label.setBorder(new EmptyBorder(4,12,4,12));
            add(label, BorderLayout.CENTER);
        }
        void refresh(){
            SystemSettings settings=systemAPI.getSettings();
            boolean visible=settings!=null && settings.isMaintenanceMode();
            setVisible(visible);
        }
    }
}