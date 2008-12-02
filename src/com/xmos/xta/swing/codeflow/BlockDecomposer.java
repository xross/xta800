package com.xmos.xta.swing.codeflow;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import com.xmos.xta.swing.core.PathStatus;
import com.xmos.xta.swing.PathUpdater;

public class BlockDecomposer implements PathUpdater {
	final static boolean DISPLAY_GRAPH_VIS = false;

	private JPanel m_graphVizViewer;
	private JScrollPane m_graphScroll;
	private JLabel m_graphLabel;
    private PathStatus              m_pathStatus; 
    private TreeModel               m_treeModel;
    private ArrayList<ArrayList<BasicBlock>> m_basicBlockDepths;

    private CodeFlowPanel           m_codeFlow;
    
    public BlockDecomposer(PathStatus pathStatus){
        m_pathStatus = pathStatus;
        m_codeFlow = new CodeFlowPanel();
        m_basicBlockDepths = new ArrayList<ArrayList<BasicBlock>>();
        m_graphVizViewer = new JPanel();
        m_graphScroll = new JScrollPane();
        m_graphLabel = new JLabel();
        m_graphScroll.setViewportView(m_graphLabel);
        m_graphVizViewer.setLayout(new BorderLayout());
        m_graphVizViewer.add(m_graphScroll);
        
    }

    public void notifyUpdateState() {
    }

    public void pushSelectedNode(DefaultMutableTreeNode selectedNode) {
    }


    public void setSelectedPath(TreePath selectedPath, DefaultMutableTreeNode node) {
        m_treeModel = m_pathStatus.getTreeModel();
        if(m_treeModel!=null){
            analyseRoute();
            m_codeFlow.update(m_basicBlockDepths);
        } else {
            m_codeFlow.clear();
        }
    }

    private boolean analyseRoute(){
        boolean success = true;
        return success;
    }
            
    public JPanel getGraphVizPanel(){
        return m_graphVizViewer;
    }    

    public JPanel getJPanel() {
        return m_codeFlow;
    }
}
