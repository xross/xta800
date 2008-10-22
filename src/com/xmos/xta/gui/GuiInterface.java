package com.xmos.xta.gui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

public interface GuiInterface {

    public int getChildCount();
    public Color getBaseColor();
    public ImageIcon getIcon();
    public String getToolTipText();
    public String getNodeText();
    
    public JPopupMenu getPopupMenu();
    
}
