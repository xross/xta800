package com.xmos.xta.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.Unknown;

public class UnknownDisplayer implements Displayer {

    private ArrayList<Unknown> m_unknowns;
    private JList m_unknownList;
    private JPanel m_panel;
    private DefaultListModel m_listModel;
    private Route m_route;
    
    public UnknownDisplayer(){
        m_listModel = new DefaultListModel();
        m_unknownList = new JList(m_listModel);
        m_unknownList.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                mousePress(e); 
            }
        });
        m_unknownList.setCellRenderer(new UnknownCellRenderer());
        m_panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(m_unknownList);
        m_panel.setLayout(new BorderLayout());
        m_panel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void mousePress(MouseEvent e) {
        Unknown selectedUnknown = (Unknown) m_unknownList.getSelectedValue();
    }
    
    public void displayRoute(Route route, DebugInfo debugInfo,
            MemoryImage memoryImage, SymbolTable table) {
        m_route = route;
        m_unknowns = route.getUnknowns();
        for(Unknown unknown : m_unknowns){
            m_listModel.addElement(unknown);
        }
    }

    public void notifyAnalysePaths() {
        m_listModel.clear();
    }

    public void notifyLoadExecutable() { 
        m_listModel.clear();
    }

    public void notifyUpdateState() {
        if(m_route==null){
            return;
        }
        m_unknowns = m_route.getUnknowns();
        m_listModel.clear();
        for(Unknown unknown : m_unknowns){
            m_listModel.addElement(unknown);
        }
        m_unknownList.repaint();
    }

    public JPanel getUnknownPanel() {
        return m_panel;
    }

}
class UnknownCellRenderer extends JLabel implements ListCellRenderer {

    private static final long serialVersionUID = 1L;

    public Component getListCellRendererComponent(
      JList list,              // the list
      Object value,            // value to display
      int index,               // cell index
      boolean isSelected,      // is the cell selected
      boolean cellHasFocus)    // does the cell have focus
    {
        Unknown u = (Unknown)value;
        setText(u.getUnknownString());
        setIcon(u.getIcon());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
