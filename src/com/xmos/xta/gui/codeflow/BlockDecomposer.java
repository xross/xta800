package com.xmos.xta.gui.codeflow;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.gui.PathUpdater;
import com.xmos.xta.gui.core.PathStatus;
import com.xmos.xta.gui.core.RoutePath;

public class BlockDecomposer implements PathUpdater {
	final static boolean DISPLAY_GRAPH_VIS = false;

	private JPanel m_graphVizViewer;
	private JScrollPane m_graphScroll;
	private JLabel m_graphLabel;
    private Route                   m_route;
    private PathStatus              m_pathStatus; 
    private TreeModel               m_treeModel;
    private int                     m_analysedRoute;
    private ArrayList<BasicBlock>   m_basicBlocks;
    private ArrayList<ArrayList<BasicBlock>> m_basicBlockDepths;

    private CodeFlowPanel           m_codeFlow;
    
    public BlockDecomposer(PathStatus pathStatus){
        m_pathStatus = pathStatus;
        m_basicBlocks = new ArrayList<BasicBlock>();
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
            DefaultMutableTreeNode routeNode = (DefaultMutableTreeNode)m_treeModel.getRoot();
            RoutePath rp  = (RoutePath) routeNode.getUserObject();
            m_route = rp.getRoute();
            analyseRoute();
            m_codeFlow.update(m_basicBlockDepths);
        } else {
            m_codeFlow.clear();
        }
    }

    private boolean analyseRoute(){
        boolean success = true;

        if(m_route.hashCode() == m_analysedRoute){
            return true;
        } 
        /*
        m_basicBlocks.clear();

        ArrayList<InstructionLink> links = buildInstructionLinks(m_route.getBlocks());

        parseBlocks(m_route.getBlocks(), links);
        int startBlock =parseLinks(links);
        if(startBlock == -1 ) return false;
        HashMap<Integer, BasicBlock> basicBlockMap = new HashMap<Integer, BasicBlock>();
        for(BasicBlock bb : m_basicBlocks){
            for(XtaInstruction inst : bb.getInstructions()){
                basicBlockMap.put(inst.getPc(), bb);
            }
        }

        makeBasicBlockLinks(links, basicBlockMap);

        basicBlockMap.clear();
        
        m_basicBlockDepths.clear();
        resetAllBasicBlockDepths();
        findBasicBlockDepth(m_basicBlocks.get(startBlock), 0, new ArrayList<BasicBlock>());
        
        if (DISPLAY_GRAPH_VIS) 
            printOutGraphvis();
        */
        if(success){
            m_analysedRoute = m_route.hashCode();
        } else {
            m_analysedRoute = 0;
        }
        return success;
    }
    
    
    private void resetAllBasicBlockDepths() {
       for(BasicBlock bb : m_basicBlocks){
           bb.setDepth(-1);
       }
        
    }

    private void findBasicBlockDepth(BasicBlock bb, int depth, ArrayList<BasicBlock> alreadySeen) {
        
        if(bb.getDepth()>-1 ){
            if(!alreadySeen.contains(bb)) {
                int oldDepth = bb.getDepth();
                bb.setDepth(Math.max(bb.getDepth(), depth));
                if(bb.getDepth()> oldDepth){
                    m_basicBlockDepths.get(oldDepth).remove(bb);
                    addNewBlock(bb, bb.getDepth());
                }
            }
            return;
        } 
        addNewBlock(bb, depth);
        bb.setDepth(depth);
        ArrayList<BasicBlock> nowSeen =new ArrayList<BasicBlock>();
        nowSeen.addAll(alreadySeen);
        nowSeen.add(bb);
        for(BasicBlock nextBB : bb.getNextBasicBlocks()){
            assert(nextBB!=null);
            findBasicBlockDepth(nextBB, depth+1,nowSeen);
        }
        
    }
    
    private void addNewBlock(BasicBlock bb, int depth){
        if(depth<m_basicBlockDepths.size()){
            m_basicBlockDepths.get(depth).add(bb);
        } else {
            ArrayList<BasicBlock> blockList = new ArrayList<BasicBlock>();
            blockList.add(bb);
            m_basicBlockDepths.add(blockList);
        }
    }
    
    

    private void makeBasicBlockLinks(ArrayList<InstructionLink> links, HashMap<Integer, BasicBlock> basicBlockMap) {
        for(BasicBlock bb : m_basicBlocks){
            int index = getIndex(bb.getInstructions().get(bb.getInstructions().size()-1), links);
            for(XtaInstruction nextInst :links.get(index).getNextInstructions()){
                BasicBlock toAdd = basicBlockMap.get(nextInst.getPc());
                if(toAdd!=null){
                    bb.addNextBlock(toAdd);
                } else {
                    //System.err.println("known error occured in block decomposer");
                }
            }
        }
    }

    private void printOutGraphvis(){
        
        String dotFile = "";
        dotFile +="digraph g {\n";
        for(BasicBlock bb : m_basicBlocks){
            dotFile +="L" + bb.hashCode() + "  [shape = box, label = \"";
            for(XtaInstruction inst : bb.getInstructions()){
                dotFile += Integer.toHexString(inst.getPc()) +" " + inst.getNmenonic() + " "+ inst.getOperandString() +"\\n";
            }
            dotFile +="\"];\n";
        }
        for(BasicBlock bb : m_basicBlocks){
            for(BasicBlock nb : bb.getNextBasicBlocks()){
                dotFile+="L"+bb.hashCode() + " -> L" + nb.hashCode() + ";\n";
            }
        }
        dotFile += "}\n";
        FileWriter file_writer;
        String graphNumber = "" + dotFile.hashCode() + ".dot";
        //String pictureNumber = "" + dotFile.hashCode() + ".jpg";
        try {
            file_writer = new FileWriter (new File(graphNumber));
            BufferedWriter buf_writer = new BufferedWriter (file_writer);
            buf_writer.write(dotFile);
            buf_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try {
            Process p=Runtime.getRuntime().exec("dot "+graphNumber+" -Tjpg -o "+pictureNumber);
            p.waitFor();
            //ImageIcon icon = new ImageIcon(pictureNumber);
            //m_graphLabel.setIcon(icon);
            //m_graphVizViewer.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
    
    public JPanel getGraphVizPanel(){
        return m_graphVizViewer;
    }
    
    private ArrayList<InstructionLink> buildInstructionLinks( Vector<Block> blocks) {
        ArrayList<InstructionLink> links = new ArrayList<InstructionLink>();
        for(Block block : blocks){
            for(XtaInstruction inst : block.getInstructionsThis()){
                if(!inst.isFnop()){
                    boolean found = false;
                    for(InstructionLink link : links){
                        if (link.getInstruction().getPc() == inst.getPc()) found = true;
                    }
                    if(!found){
                        InstructionLink il = new InstructionLink(inst);
                        links.add(il);
                    }
                }
            }
        }
        return links;
    }

    private int parseLinks(ArrayList<InstructionLink> links) {
        if(m_route.completePaths().size()>0){
            BasicBlock currentBlock = new BasicBlock();
            XtaInstruction startInst = m_route.completePaths().firstElement().getInstructions(false).firstElement();
            int start = getIndex (startInst, links);
            nextLink(start,currentBlock, links );
            assert(!insructionsRemaining(links));
            return start;
        }
        return -1;
    }

    private void nextLink(int index, BasicBlock currentBlock, ArrayList<InstructionLink> links){
        int currentOut = links.get(index).getNextInstructions().size();
        int nextIn = 1;
        if (currentOut == 1){
            nextIn = links.get(getIndex(links.get(index).getNextInstructions().get(0), links)).getPreviousInstructions().size();
        }
        currentBlock.addInstruction(links.get(index).getInstruction());
        if( currentOut==1 ){
            if(nextIn != 1){
                m_basicBlocks.add(currentBlock);
                currentBlock = new BasicBlock();
            }
        } else {
            m_basicBlocks.add(currentBlock);
            currentBlock = new BasicBlock();
        }
        links.get(index).setDone(true);
        if(links.get(index).getNextInstructions().size()>1){
            for(XtaInstruction nextInst :links.get(index).getNextInstructions()){
                currentBlock = new BasicBlock();
                index= getIndex(nextInst, links);
                if(links.get(index).isDone() == false) nextLink(index, currentBlock, links);
            }
        } else {
            for(XtaInstruction nextInst :links.get(index).getNextInstructions()){
                index= getIndex(nextInst, links);
                if(links.get(index).isDone() == false) nextLink(index, currentBlock, links);
            }
        }
    }

    private boolean insructionsRemaining(ArrayList<InstructionLink> links){
        for(InstructionLink il : links){
            if(!il.isDone()) return true;
        }
        return false;
    }

    private int getIndex(XtaInstruction inst, ArrayList<InstructionLink> links){
        for(InstructionLink il : links){
            if(il.getInstruction().getPc() == inst.getPc()){
                return links.indexOf(il);
            }
        }
        assert(false);
        return -1;
    }

    private void parseBlocks(Vector<Block> blocks, ArrayList<InstructionLink> links) {
        for(Block block : blocks){
            for(int i=0;i<block.getInstructionsThis().size();i++){
                XtaInstruction prevInst = null;
                if(i-1>=0){
                    prevInst = block.getInstructionsThis().get(i-1);
                } else {
                    if(block.getParentPath()!=null){
                        prevInst = block.getParentPath().getInstructionsThis().lastElement();
                    } 
                }
                if(prevInst!= null){
                    links.get(getIndex(prevInst,links)).addNext(block.getInstructionsThis().get(i));
                    links.get(getIndex(block.getInstructionsThis().get(i),links)).addPrevious(prevInst);
                }
            }
            XtaInstruction lastInst = block.getInstructionsThis().lastElement();
            for(Block loopBlock : block.getLoopPaths()){
                links.get(getIndex(lastInst,links)).addNext(loopBlock.getInstructions(false).get(0));
                links.get(getIndex(loopBlock.getInstructions(false).get(0),links)).addPrevious(lastInst);
            }
        }
    }

    public JPanel getJPanel() {
        return m_codeFlow;
    }
}
