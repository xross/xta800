package com.xmos.xta.swing.trees;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.xmos.xta.swing.PathUpdater;

/*
 * it may be necessary to reintroduce this in order to catch key events on the tree
 */

public class XTreeSelectionListener implements TreeSelectionListener {

    private JTree m_tree;
    private PathUpdater m_routeDisplayer;
    
    public XTreeSelectionListener( JTree tree , PathUpdater routeDisplayer) {
        super();
        m_tree = tree;
        m_routeDisplayer = routeDisplayer;
    }

    public void valueChanged(TreeSelectionEvent e) {
        if(m_tree.getSelectionPath()==null) return;
        DefaultMutableTreeNode xNode = (DefaultMutableTreeNode)(m_tree.getSelectionPath().getLastPathComponent());
        m_routeDisplayer.pushSelectedNode(xNode);
    }

}
