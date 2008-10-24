package com.xmos.xta.gui.trees;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import com.xmos.xta.gui.PathUpdater;
import com.xmos.xta.gui.core.PathStatus;
import com.xmos.xta.gui.core.RouteNode;

public class XMouseListener implements MouseListener {
    
    private JTree m_tree;
    private PathStatus m_pathStatus;
    private PathUpdater m_routeDisplayer;
    public XMouseListener(JTree m_tree, PathStatus pathStatus, PathUpdater routeDisplayer) {
        super();
        m_pathStatus = pathStatus;
        this.m_tree = m_tree;
        m_routeDisplayer = routeDisplayer;
    }
    

    public void mousePressed(MouseEvent e) {

        if(e.getButton() == 1){
            if(e.getClickCount() == 2){
                m_pathStatus.setSelectedNodeAsCenterNode();
            }
            if(m_tree.getSelectionPath()==null) return;
            DefaultMutableTreeNode xNode = (DefaultMutableTreeNode)(m_tree.getSelectionPath().getLastPathComponent());
            m_routeDisplayer.pushSelectedNode(xNode);
        } else if (e.getButton() == 3){
            TreePath selectedPath = m_tree.getClosestPathForLocation(e.getX(), e.getY());
            m_tree.setSelectionPath(selectedPath);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getPathComponent(selectedPath.getPathCount()-1);
            if (!(node.getUserObject() instanceof RouteNode)) return;
            RouteNode selectedNode = (RouteNode)node.getUserObject();
            JPopupMenu popup = selectedNode.getPopupMenu();
            JMenuItem expand = new JMenuItem("Expand All");
            expand.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e) {
                    for(int row = 0;row < m_tree.getRowCount();row++){
                        m_tree.expandRow(row);
                    }
                }
            });
            JMenuItem collapse = new JMenuItem("Collapse All");
            collapse.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e) {
                    for(int row = m_tree.getRowCount()-1;row>=0;row--){
                        m_tree.collapseRow(row);
                    }
                    m_tree.expandRow(0);
                }
            });
            if(popup.getComponentCount()>0) popup.addSeparator();
            popup.add(expand);
            popup.add(collapse);
            popup.setOpaque(true);
            popup.setLightWeightPopupEnabled(true);
            popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
        }
    }
    
    
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
