package com.xmos.xta.gui;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JComponent;

import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.FileFinder;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.XeObject;
import com.xmos.xeobjectdisplayer.DisassemblyDisplayer;
import com.xmos.xeobjectdisplayer.SourceDisplayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;

/*
 * This class encapsulates a full set of displayers (source and disassembly)
 * and provides methods on them such as highlighting a specific pc.
 */
public class XtaXeObjectDisplayer  {

    private ArrayList<SourceDisplayer>      m_sourceDisplayers;
    private DisassemblyDisplayer            m_disassemblyDisplayer;
    private XeObject                        m_xeObject;
    private String                          m_startPc;
    private String                          m_endPc;
    private String                          m_startLine;
    private String                          m_endLine;
    private Engine                          m_engine;

    public XtaXeObjectDisplayer(XeLoader xeLoader, FileFinder fileFinder, JComponent caller, Engine engine) {
        m_xeObject = new XeObject(xeLoader, fileFinder, caller);
        m_engine = engine;
        makeSourceDisplayers(m_xeObject);
        makeDisassemblyDisplayer(m_xeObject);
        engine.setXeObject(m_xeObject);
        m_startPc = "";
        m_endPc = "";
        m_startLine = "";
        m_endLine = "";
    }

    private void makeSourceDisplayers(XeObject xeObject){
        m_sourceDisplayers = new ArrayList<SourceDisplayer>();
        for(SourceFile sourceFile : xeObject.getSourceFiles()){
            SourceDisplayer sourceDisplayer = new SourceDisplayer(sourceFile, m_engine, this);
            m_sourceDisplayers.add(sourceDisplayer);
        }
        return;
    }

    private void makeDisassemblyDisplayer(XeObject xeObject){
        m_disassemblyDisplayer = new DisassemblyDisplayer(xeObject.getFunctions(), m_engine, this);
        return;

    }

    public void registerFileFinder(FileFinder fileFinder, JComponent caller){
        m_xeObject.registerFileFinder(fileFinder, caller);
    }

    public ArrayList<SourceDisplayer> getSourceDisplayers(){
        return m_sourceDisplayers;
    }

    public DisassemblyDisplayer getDisassemblyDisplayer(){
        return m_disassemblyDisplayer;
    }

    public XeObject getXeObject(){
        return m_xeObject;
    }

    public void setFnops(ArrayList<XtaInstruction> fnops){

    }

    public void setPCColourNumber(ArrayList<Integer> pcs, double colourNumber, Color baseColour) {

    }

    public void redraw(){

    }

    public void clearHighlights() {

    } 

    public void clearFnops(){

    }

    public void setFoldAll(boolean fold) {

    }

    public ArrayList<String> getSourceFiles() {
        return m_xeObject.getSourceFileNames();
    }

    public void setM_startPc(String pc) {
        m_startPc = pc;
        m_startLine  = "";
    }

    public String getM_startPc() {
        return m_startPc;
    }
    
    public String getM_endPc() {
        return m_endPc;
    }

    public void setM_endPc(String pc) {
        m_endPc = pc;
        m_endLine  = "";
    }

    public String getM_startLine() {
        return m_startLine;
    }

    public void setM_startLine(String line) {
        m_startLine = line;
        m_startPc  = "";
    }

    public String getM_endLine() {
        return m_endLine;
    }

    public void setM_endLine(String line) {
        m_endLine = line;
        m_endPc  = "";
    }

}
