package com.xmos.xta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.engine.Route;

public class StatusBarDisplayer implements Displayer {

    @SuppressWarnings("unused")
    private GlobalConfig            m_globalConfig;
    private String                  m_status;
    private JPanel                  m_statusPanel;
    private JLabel                  m_textArea;
    private Engine                  m_engine;
    private JFrame                  m_parentFrame;

    public StatusBarDisplayer(Engine engine, JFrame parentFrame, GlobalConfig globalConfig) {
        super();
        m_globalConfig = globalConfig;
        m_engine = engine;
        m_parentFrame = parentFrame;
        m_statusPanel = new JPanel();
        m_status = "";
        m_textArea = new JLabel();

        int fontHeight = m_textArea.getFontMetrics(m_textArea.getFont()).getHeight();
        m_statusPanel.setPreferredSize(new Dimension(fontHeight, fontHeight + 4));
        m_statusPanel.setLayout(new BorderLayout());
        m_textArea.setText(m_status);
        m_statusPanel.add(m_textArea, BorderLayout.WEST);
    }

    //update the contents of the route status
    public void displayRoute(Route route, DebugInfo debugInfo,
            MemoryImage memoryImage, SymbolTable table) {
        m_status = "Ready"; 
        m_textArea.setText(m_status);
        m_statusPanel.repaint();

    }

    public void notifyAnalysePaths() {
        m_status = "Analysing Route"; 
        m_textArea.setText(m_status);
        ProgressPoller poller = new ProgressPoller(m_engine, m_parentFrame);
        poller.start();
    }

    class ProgressPoller extends Thread {
        private Engine                 m_engine;
        private JFrame                 m_parent;

        public ProgressPoller(Engine engine, final JFrame parent){
            m_engine = engine;
            m_parent = parent;
        }
        private ProgressMonitor m_progressMonitor;


        public void run() { 

            Progress p = m_engine.getProgress();  

            if(p.getCurValue() < p.getMaxValue() || m_engine.getErrorString()!=null){
                m_progressMonitor = new ProgressMonitor(m_parent.getContentPane(),
                        "Analysing",
                        "", 0, 100);
                m_progressMonitor.setMillisToDecideToPopup(500);
                m_progressMonitor.setMillisToPopup(500);
                m_progressMonitor.setProgress(1);
                
                boolean abort = false;
                while(!abort){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    m_progressMonitor.setMaximum(p.getMaxValue());
                    m_progressMonitor.setProgress(p.getCurValue());

                    if(p.getState() == Progress.State.IDLE){
                        break;
                    }
                    if(m_progressMonitor.isCanceled()){
                        m_engine.setAbort(true);
                        m_status = "Ready"; 
                        m_textArea.setText(m_status);
                        break;
                    }

                    if(p.getMaxValue() == p.getCurValue()){
                        break;
                    }
                    p = m_engine.getProgress();
                }
            }
        }
    }



    public void notifyLoadExecutable() {
    }	       

    public void notifyUpdateState() {    
    }

    public JPanel getPanel(){
        return m_statusPanel;
    }
    public JDialog getErrorDialog(String errorString){
        JDialog errorWindow = new JDialog();

        return errorWindow;
    }
}
