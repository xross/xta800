package com.xmos.xta.swing;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.FileFinder;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.swing.codewindow.DisassemblyDisplayer;
import com.xmos.xta.swing.codewindow.SourceDisplayer;
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
    private JComponent                      m_caller;

    public XtaXeObjectDisplayer(XeLoader xeLoader, FileFinder fileFinder, JComponent caller, Engine engine) {
        m_xeObject = new XeObject(xeLoader, fileFinder);
        m_engine = engine;
        makeSourceDisplayers(m_xeObject);
        makeDisassemblyDisplayer(m_xeObject);
        engine.setXeObject(m_xeObject);
        m_startPc = "";
        m_endPc = "";
        m_startLine = "";
        m_endLine = "";
        m_caller = caller;
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
    }

    public void registerFileFinder(FileFinder fileFinder){
        m_xeObject.registerFileFinder(fileFinder);
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

    public void setPCColourNumber(ArrayList<Integer> pcs, double colourNumber, Color baseColour) {
        for(SourceDisplayer sd : m_sourceDisplayers){
            sd.highlightPCs(pcs, baseColour);
        }
        m_disassemblyDisplayer.highlightPCs(pcs, baseColour);
    }
    
    public void clearHighlights() {
        for(SourceDisplayer sd : m_sourceDisplayers){
            sd.removeHighlights();
        }
        m_disassemblyDisplayer.removeHighlights();
    } 

    public void clearFnops(){
        for(SourceDisplayer sd : m_sourceDisplayers){
            sd.clearFnops();
        }
        m_disassemblyDisplayer.clearFnops();
    }
    
    public void setFnops(ArrayList<XtaInstruction> fnops){
        ArrayList<Integer> fnopPcs = new ArrayList<Integer>();
        for(XtaInstruction inst : fnops){
            if(!fnopPcs.contains(inst.getPc()))fnopPcs.add(inst.getPc());
        }
        
        for(SourceDisplayer sd : m_sourceDisplayers){
            sd.setFnops(fnopPcs);
        }
        m_disassemblyDisplayer.setFnops(fnopPcs);
    }
    
    public void snapToPc(int pc){
        /*
        for(SourceDisplayer sd : m_sourceDisplayers){
            sd.setFnops(fnopPcs);
        }
        m_disassemblyDisplayer.setFnops(fnopPcs);
        */
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

    public void setPausable(final Line line) {
        
        final JDialog id = new JDialog();
        
        id.setLocationRelativeTo(m_caller);
        id.setTitle("Set Instruction Time");
        id.setVisible(true);

        int initialCycles=line.getCycles();
        JLabel timeLbl = new JLabel("Time: ");
        JLabel cyclesLbl = new JLabel("Cycles: ");
        final JSpinner cyclesSpinner = new JSpinner();
        
        SpinnerModel cyclesModel = new SpinnerNumberModel(initialCycles,0,Integer.MAX_VALUE,1); 
        cyclesSpinner.setModel(cyclesModel);
        
        int initialTime = (int)line.getTime();
        final JSpinner timeSpinner = new JSpinner();
        SpinnerModel timeModel = new SpinnerNumberModel(initialTime,0,Integer.MAX_VALUE,1); 
        
        /*
        ((JSpinner.DefaultEditor)timeSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e) {
                String text = ((JSpinner.DefaultEditor)timeSpinner.getEditor()).getTextField().getText();
                try {
                    int time = Integer.parseInt(text);
                    int cycles = 0;
                    if (m_engine.getConfig().m_numThreads > 4) {
                        cycles = ((int)((time * m_engine.getConfig().m_frequency) / (m_engine.getConfig().m_numThreads * 1000)));
                    }
                    else {
                        cycles =((int)((time * m_engine.getConfig().m_frequency) / (4 * 1000)));
                    }
                    
                    cyclesSpinner.setValue(cycles);
                } catch (NumberFormatException e1) {
                }
            }
        });
        */
        
        
        
        
        timeSpinner.setModel(timeModel);
        
        
        JButton setButton = new JButton("Set");
        
        setButton.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                setTime(cyclesSpinner, id, line);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                id.dispose();
            }
        });
        id.setLayout(new GridLayout(3,2));
        id.add(cyclesLbl);
        
        id.add(cyclesSpinner);
        id.add(timeLbl);
        id.add(timeSpinner);
        id.add(setButton);
        id.add(cancelButton);
        id.pack();
    }

    private void setTime(JSpinner cyclesSpinner, JDialog id, Line line){
        int numCycles = (Integer)cyclesSpinner.getValue();
                boolean done = m_engine.setInstructionNumCycles(line.getLineNumber(), numCycles);
                if(done == false){
                    JOptionPane.showMessageDialog(m_caller,
                            m_engine.getErrorString(),
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
                id.dispose();
    }
}
