package com.xmos.xta.swing.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.swing.GlobalConfig;
import com.xmos.xta.swing.PathUpdater;
import com.xmos.xta.swing.XtaGui;
import com.xmos.xta.swing.XtaXeObjectDisplayer;

public class PathStatus implements Displayer {

    private ArrayList<PathUpdater>  m_pathUpdaters;
    private TreeModel               m_treeModel;
    private TreePath                m_selectedPath;
    private SymbolTable             m_table;
    private XtaXeObjectDisplayer       m_displayers;
    private GlobalConfig            m_globalConfig;
    private DefaultMutableTreeNode  m_centerNode;
    private Engine                  m_engine;
    private int                     m_treeSize;
    private JFrame                  m_mainFrame;
    private XtaGui                  m_xtaGui;

    public PathStatus(GlobalConfig config, Engine engine, JFrame mainFrame, XtaGui xtaGui) {
        m_pathUpdaters = new ArrayList<PathUpdater>();
        m_engine = engine;
        m_globalConfig = config;
        m_treeSize =0;
        m_mainFrame = mainFrame;
        m_xtaGui = xtaGui;
    }

    public void setSelectedNodeAsCenterNode(){
        m_centerNode = (DefaultMutableTreeNode) m_selectedPath.getLastPathComponent();
    }

    public void displayRoute(Route route, DebugInfo debugInfo,
            MemoryImage memoryImage, SymbolTable table) {
        
        m_table = table;
        m_treeSize = 0;
        DefaultMutableTreeNode m_rootNode = new DefaultMutableTreeNode(new RoutePath(route, m_engine));
        m_treeModel = new DefaultTreeModel(m_rootNode);
        for (Block path : route.completePaths()){
            ((DefaultTreeModel)m_treeModel).insertNodeInto(
                    addPath(0, path, null),
                    m_rootNode,
                    ((DefaultTreeModel)m_treeModel).getChildCount(m_rootNode));
        }
        m_selectedPath = new TreePath(m_rootNode);
        m_centerNode = m_rootNode;
        for (PathUpdater pathUpdater : m_pathUpdaters){
            pathUpdater.setSelectedPath(m_selectedPath, m_centerNode);
        }

    }

    private DefaultMutableTreeNode addPath(int depth, Block path, Block caller) {
        DefaultMutableTreeNode newPath;
        newPath = new DefaultMutableTreeNode( new RootPath(path, caller, depth, m_engine, m_mainFrame, m_xtaGui));
        m_treeSize++;
        assert(newPath!=null);
        for (Block pathBlock : path.getPathBlocks()) {
            DefaultMutableTreeNode block = new DefaultMutableTreeNode(new BlockPath(pathBlock));
            
            if (pathBlock.numLoopPaths() > 0) {
                DefaultMutableTreeNode loop = new DefaultMutableTreeNode(new LoopPath(pathBlock, path, m_engine));
                m_treeSize++;
                for (Block loopPath : pathBlock.getLoopPaths()) {
                    loop.add(addPath(depth + 1, loopPath, pathBlock));
                }
                newPath.add(loop);
            }

            m_treeSize++;
            newPath.add(block);
        }
        path.doneWithPathBlocks();        
        return newPath;
    }    

    public void setXeObjectDisplayer(XtaXeObjectDisplayer displayers){
        m_displayers = displayers;
    }

    public TreeModel getTreeModel(){
        return m_treeModel;
    }

    public void registerPathUpdater(PathUpdater pathUpdater){
        m_pathUpdaters.add(pathUpdater);
    }

    public void notifyAnalysePaths() {

    }

    public void notifyLoadExecutable() {
        m_treeModel = null;
        for(PathUpdater pathUpdater : m_pathUpdaters){
            pathUpdater.setSelectedPath(null, null);
        }
    }

    public void notifyUpdateState() {
        for(PathUpdater pathUpdater : m_pathUpdaters){
            pathUpdater.notifyUpdateState();
        }

    }

    public SymbolTable getSymbolTable(){
        return m_table;

    }

    public void setRouteSelection(DefaultMutableTreeNode selectedNode, PathUpdater updatee) {
        Object root = (TreeNode)m_treeModel.getRoot();
        LinkedList<Object> list = new LinkedList<Object>();
        if(!root.equals(selectedNode)){
            boolean found = findPath((TreeNode)m_treeModel.getRoot(), selectedNode, list);
            assert(found);
            list.add(root);
            list.removeFirst();
        } else {
            list.add(root);
        }
        boolean foundCenterNode = false;

        for(Object o : list.toArray()){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
            if(node.equals(m_centerNode)) foundCenterNode = true;
        }


        Collections.reverse(list);
        m_selectedPath = new TreePath(list.toArray());
        
        makeHighlighting();
        if(foundCenterNode == false){
            //rewind back as little as is necessary
            m_centerNode = (DefaultMutableTreeNode) m_selectedPath.getLastPathComponent();
        }

        //then send it out to every registered path updater
        for(PathUpdater pathUpdater : m_pathUpdaters){
            if(!pathUpdater.equals(updatee)) pathUpdater.setSelectedPath(m_selectedPath, m_centerNode);
        }
    }

    //this produces the highlighting information for the source area
    private void makeHighlighting(){
        assert(m_selectedPath!=null);
        m_displayers.clearHighlights();
        RouteNode node = 
            (RouteNode) ((DefaultMutableTreeNode)m_selectedPath.getLastPathComponent()).getUserObject();
        Vector<XtaInstruction> highlightedPcs = node.getInstructions();
        ArrayList<Integer> pcs = new ArrayList<Integer>();
        for(XtaInstruction inst: highlightedPcs){
            if(!pcs.contains(inst.getPc())) {
                pcs.add(inst.getPc());
            }
        }
        m_displayers.setPCColourNumber(pcs, 1.0, m_globalConfig.getM_highlightedRouteColour());
        
        pcs.clear();
        ArrayList<XtaInstruction> fnops = collectFnops();
        m_displayers.clearFnops();
        m_displayers.setFnops(fnops);

    }

    private ArrayList<XtaInstruction> collectFnops(){
        if (m_selectedPath.getPathCount() > 1) {
            DefaultMutableTreeNode pathNode  = (DefaultMutableTreeNode)(m_selectedPath.getPath())[1];
            return getNodeFnops(pathNode);
        } else {
            return new ArrayList<XtaInstruction>();
        }
    }

    private ArrayList<XtaInstruction> getNodeFnops(DefaultMutableTreeNode pathNode) {
        ArrayList<XtaInstruction> fnops = new ArrayList<XtaInstruction>();
        
        RouteNode node = (RouteNode)pathNode.getUserObject();
        for (XtaInstruction inst : node.getInstructions()){
            if (inst.isFnop()&& !fnops.contains(inst.getPc())) {
                fnops.add(inst);
            }
        }
        
        for(int i = 0; i < pathNode.getChildCount(); i++){
            ArrayList<XtaInstruction> newFnops = getNodeFnops((DefaultMutableTreeNode)pathNode.getChildAt(i));
            for(XtaInstruction fnop : newFnops){
                if (!fnops.contains(fnop)) {
                    fnops.add(fnop);
                }
            }
        }
        return fnops;
    }

    private boolean findPath(TreeNode node, Object leaf, LinkedList<Object> list){
        if (node.equals(leaf)){
            list.add(node);
            return true;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (findPath(node.getChildAt(i), leaf, list)) {
                list.add(node.getChildAt(i));
                return true;
            }
        }
        return false;
    }

    public TreePath getSelectedPath(){
        return m_selectedPath;
    }
    public DefaultMutableTreeNode getCenterNode(){
        return m_centerNode;
    }
}
