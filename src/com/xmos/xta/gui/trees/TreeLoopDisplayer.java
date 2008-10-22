package com.xmos.xta.gui.trees;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Route;
import com.xmos.xta.gui.PathUpdater;
import com.xmos.xta.gui.core.PathStatus;

public class TreeLoopDisplayer implements PathUpdater {
    private JTree m_tree;
    private JPanel m_histogramPanel;
    
    public TreeLoopDisplayer(PathStatus pathStatus) {
        super();
        m_histogramPanel = new JPanel();
        m_histogramPanel.setPreferredSize(new Dimension(300, 300));
        DefaultMutableTreeNode empty = new DefaultMutableTreeNode( "No Routes Registered");
        m_tree = new JTree(empty);
        JScrollPane scrollPane = new JScrollPane(m_tree);
        m_histogramPanel.setLayout(new BorderLayout());
        m_histogramPanel.add(scrollPane, BorderLayout.CENTER);
       
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
        return m_histogramPanel;
    }

    public void displayRoute(Route route, DebugInfo debugInfo,
            MemoryImage memoryImage, SymbolTable table) {

    }

    public void notifyAnalysePaths() {
        // TODO Auto-generated method stub
        
    }

    public void notifyLoadExecutable() {
        // TODO Auto-generated method stub
        
    }

    public void pushSelectedNode(DefaultMutableTreeNode selectedNode) {
        // TODO Auto-generated method stub
        
    }

    public void setSelectedPath(TreePath selectedPath,
            DefaultMutableTreeNode node) {
        // TODO Auto-generated method stub
        
    }
}