package com.xmos.xta.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public interface PathUpdater {
    
    //the engine has been updated
    void notifyUpdateState();
    
    //user has selected a path
    void setSelectedPath(TreePath selectedPath, DefaultMutableTreeNode m_centerNode);
    public void pushSelectedNode(DefaultMutableTreeNode selectedNode);
    
    
}
