package com.xmos.xta.gui;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.text.BadLocationException;

import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.FileFinder;
import com.xmos.xeobject.XeObject;
import com.xmos.xeobject.displayer.DisassemblyDisplayer;
import com.xmos.xeobject.displayer.Line;
import com.xmos.xeobject.displayer.SourceDisplayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;

/*
 * This class encapsulates a full set of displayers (source and disassembly)
 * and provides methods on them such as highlighting a specific pc.
 */
public class XeObjectDisplayer  {
    
    private SourceDisplayer[]    m_sourceDisplayers;
    private DisassemblyDisplayer m_disassemblyDisplayer;
    private XeObject             m_xeObject;
    private JTabbedPane          m_tabbedPane;
    
    public XeObjectDisplayer(XeLoader xeLoader, FileFinder fileFinder, JComponent caller, Engine engine) {
        m_xeObject = new XeObject(xeLoader, fileFinder, caller);
//        m_sourceDisplayers = m_xeObject.createSourceDisplayers(engine);
//        m_disassemblyDisplayer = m_xeObject.createDisassemblyDisplayer(engine);
        engine.setXeObject(m_xeObject);
    }
    
    public void setTabbedPane(JTabbedPane tabbedPane){
        m_tabbedPane = tabbedPane;
    }

    public void registerFileFinder(FileFinder fileFinder, JComponent caller){
        m_xeObject.registerFileFinder(fileFinder, caller);
    }
    
    public SourceDisplayer[] getSourceDisplayers(){
        return m_sourceDisplayers;
    }

    public DisassemblyDisplayer getDisassemblyDisplayer(){
        return m_disassemblyDisplayer;
    }
    
    public XeObject getXeObject(){
        return m_xeObject;
    }
    
    public void setFnops(ArrayList<XtaInstruction> fnops){
        for(AssemblyLine as : m_xeObject.getInstructions()){
            as.setFnopCount(0);
        }
        for(XtaInstruction fnop : fnops){
            AssemblyLine line = m_xeObject.getAssemblyLine(fnop.getPc());
            line.setFnopCount(line.getFnopCount() + 1);
        }
        return;
    }
 
    public void setPCColourNumber(ArrayList<Integer> pcs, double colourNumber, Color baseColour) {
        for(int pc : pcs){
            m_xeObject.getAssemblyLine(pc).getLineAttributes().setColourNumber(colourNumber);
        } 
        for(SourceDisplayer sd : m_sourceDisplayers){
            sd.setBaseColour(baseColour);
        }
        m_disassemblyDisplayer.setBaseColour(baseColour);

    }
    
    public void redraw(){
        for(SourceDisplayer sd : m_sourceDisplayers){
            try {
                sd.renderFileToDoc();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        try {
            m_disassemblyDisplayer.renderFileToDoc();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public void clearHighlights() {
        m_xeObject.clearHighlights();
        if(m_tabbedPane.getTabCount()>1){
            for(int i=0;i<m_tabbedPane.getTabCount()-1;i++){
                m_tabbedPane.setIconAt(i, new ImageIcon());
            }
        }
    } 

    public void clearFnops(){
        for(AssemblyLine as : m_xeObject.getInstructions()){
            as.setFnopCount(0);
        }
    }

    public void setFoldAll(boolean fold) {
        for(SourceDisplayer sd : m_sourceDisplayers){
            for(Line line : sd.getLines()){
                sd.setLineFold(line, fold);
            }
        }
        for(Line line : m_disassemblyDisplayer.getLines()){
            m_disassemblyDisplayer.setLineFold(line, fold);
        }
        redraw();
    }

    public ArrayList<String> getSourceFiles() {
        return m_xeObject.getSourceFileNames();
    }
    
}
