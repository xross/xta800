package com.xmos.xta.gui.trees;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.xmos.xta.gui.PathUpdater;
import com.xmos.xta.gui.core.PathStatus;

public class TreeRouteDisplayer implements PathUpdater{

    private JTree m_tree;
    private JPanel m_routeTreePanel;
    private PathStatus m_pathStatus;
    private DefaultMutableTreeNode m_rootNode;

    public TreeRouteDisplayer(PathStatus pathStatus) {
        super();
        m_pathStatus = pathStatus;
        m_routeTreePanel = new JPanel();
        m_routeTreePanel.setPreferredSize(new Dimension(300, 300));
        m_rootNode = new DefaultMutableTreeNode("No Routes Registered");
         
        m_tree = new JTree(m_rootNode);
        
        ToolTipManager.sharedInstance().registerComponent(m_tree);
        TreeCellRenderer renderer = new TreeBarRenderer();
        m_tree.setCellRenderer(renderer);
        
        m_tree.setExpandsSelectedPaths(true);
        m_routeTreePanel.setLayout(new BorderLayout());
        m_routeTreePanel.add(new JScrollPane(m_tree), BorderLayout.CENTER);
        m_tree.setCellRenderer(new TreeBarRenderer());
        m_tree.setExpandsSelectedPaths(true);
        m_tree.addMouseListener(new XMouseListener( m_tree, m_pathStatus, this));
    }     

    public void notifyUpdateState() { 
        
        TreeModel treeModel = m_tree.getModel();
        refresh((TreeNode)treeModel.getRoot());
    }
    
    private void refresh(TreeNode node) {
        ((DefaultTreeModel)m_tree.getModel()).nodeChanged(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            refresh(node.getChildAt(i));
        }
    }

    public JPanel getPanel(){
        return m_routeTreePanel;
    }

    public void setSelectedPath(TreePath selectedPath, DefaultMutableTreeNode centerNode) {
        
        if(selectedPath == null){
            m_rootNode = new DefaultMutableTreeNode( "No Routes Registered");
            m_tree.setModel(new DefaultTreeModel(m_rootNode));
        } else {
            m_tree.setModel(this.m_pathStatus.getTreeModel());
            m_tree.setSelectionPath(selectedPath);
            m_tree.scrollPathToVisible(selectedPath);
        }
    }

    public void pushSelectedNode(DefaultMutableTreeNode selectedNode) {
        if(m_tree.getSelectionPath()!= null && !(selectedNode.getUserObject() instanceof String)){
            m_pathStatus.setRouteSelection(selectedNode, this);
        }
    }
}
