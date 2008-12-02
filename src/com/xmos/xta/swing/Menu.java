package com.xmos.xta.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.xmos.xeobject.FileFinder;
import com.xmos.xta.engine.Engine;

public class Menu extends JMenuBar  implements FileFinder
{
    private static final long       serialVersionUID = 1L;
    private JMenu                   fileMenu;
    private JMenu                   toolsMenu;
    private JMenu                   helpMenu;   
    private Engine                  m_engine;
    private JMenuItem               options;
    private JMenuItem               exit;
    private JMenuItem               open;
    private JMenuItem               about;
    private JMenuItem               console;
    private GlobalConfig            m_globalConfig;
    private File                    m_currentDirectory;
    private JFrame                  m_mainFrame;
    private XtaGui                  m_xtaGui;
    
    
    public Menu(GlobalConfig globalConfig, Engine engine, JFrame mainFrame, XtaGui xtaGui) {
        super();
        m_engine = engine;
        m_mainFrame = mainFrame;
        m_globalConfig = globalConfig;
        m_xtaGui = xtaGui;
        createMenuBar();
    }
    
    public File openFileFinder(String fileToFind) {
        
        if(XtaGui.DEBUG){
            //first check all the last saved source files
            for(String fileName : m_globalConfig.getM_sourceFileNames()){
                File sourceFile = new File(fileName);
                if(sourceFile.exists() && fileToFind.equals(sourceFile.getName())){
                    return sourceFile;
                }
            }
        }
        
        JFileChooser fc = new JFileChooser();
        String fileName;
        if (m_currentDirectory == null){
            fileName = new File(m_globalConfig.getLastOpenedFile()).getParent();
        } else {
            fileName = m_currentDirectory.getParent();
        }
        fileName = fileName + File.separator + fileToFind;
        m_currentDirectory = new File(fileName);
            
        fc.setSelectedFile(m_currentDirectory);
        XFileFilter fileFilter = new XFileFilter();
        fileFilter.registerAcceptableFileTypes("xc");
        fileFilter.registerAcceptableFileTypes("xm");
        fileFilter.registerAcceptableFileTypes("s");
        fileFilter.registerAcceptableFileTypes("c");
        fc.addChoosableFileFilter(fileFilter);
        fc.setFileFilter(fileFilter);
        int returnVal = fc.showOpenDialog(m_mainFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            m_currentDirectory = fc.getSelectedFile();
            return fc.getSelectedFile();
        } else {
            return null;
        }
    }
    
    private void createMenuBar() {
        toolsMenu = new JMenu("Tools");
        helpMenu = new JMenu("Help"); 
        options = new JMenuItem("Options");
        exit = new JMenuItem("Exit");
        open = new JMenuItem("Open File");
        about = new JMenuItem("About");
        console = new JMenuItem("Console");
        fileMenu = new JMenu("File");
        
        
        fileMenu.setMnemonic('F');
        open.setMnemonic('O');
        
        fileMenu.add(open);
        
        fileMenu.addSeparator();
        LinkedList<String> history = new LinkedList<String>();
        history.addAll(m_globalConfig.getFileHistory());
        Collections.reverse(history);
        int fileNumber = 1;
        for (final String fileName : history){
            JMenuItem file = new JMenuItem(fileNumber + ": " +fileName);
            file.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    open(fileName);
            }});
            
            file.setMnemonic(Integer.toString(fileNumber).charAt(0));
            fileMenu.add(file);
            fileNumber++;
        }
        fileMenu.addSeparator();
        exit.setMnemonic('X');
        fileMenu.add(exit);


        // Create menus
        this.add(fileMenu);
        this.add(toolsMenu);
        this.add(helpMenu);

        //Tools menu items
        options.setMnemonic('O');
        toolsMenu.add(options);
        console.setMnemonic('C');
        toolsMenu.add(console);
        toolsMenu.setMnemonic('T');
        
        // Help menu items
        helpMenu.setMnemonic('H');
        about.setMnemonic('A');
        helpMenu.add(about);
        
        console.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                console();
        }});
        
        options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                options();
        }});
        
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
        }});
        
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                open();
                
                
        }});
        
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                about();
        }});
    }
    
    private void options() {

    }

    private void console(){
        new Thread() {
    	    public void run() {
    		   	ConsoleArea consoleArea = new ConsoleArea(m_engine, m_globalConfig, true);
    		   	
    		    JDialog consoleFrame = new JDialog(m_mainFrame, "Console");    	
    		  	consoleFrame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);    		  	   		  	 
    		  	consoleFrame.add(consoleArea.getScrollPane());
    		  	consoleFrame.setPreferredSize(m_globalConfig.getM_consoleFrameDimensions());
    		  	consoleFrame.setLocation(m_globalConfig.getM_consoleLocationOnScreen());
    		  	consoleFrame.pack();
    		  	consoleFrame.setVisible(true);
    		  	
    		  	consoleFrame.addComponentListener(new ComponentAdapter(){
    		        public void componentMoved(ComponentEvent e) {
    		            m_globalConfig.setM_consoleLocationOnScreen(e.getComponent().getLocationOnScreen());
    		        }
    		        public void componentResized(ComponentEvent e) {
    		            m_globalConfig.setM_consoleFrameDimensions(e.getComponent().getSize());
    		        }
    		    });
    		  	
    	        m_xtaGui.registerConsole(consoleArea.getConsoleTextArea());
    		  	consoleArea.execute();
    	        m_xtaGui.unregisterConsole(consoleArea.getConsoleTextArea());

    	        consoleFrame.dispose();    		  	
    		    }
    		}.start();    	    	
    }
    
    private void open(){
        JFileChooser fc = new JFileChooser();
        XFileFilter fileFilter = new XFileFilter();
        fileFilter.registerAcceptableFileTypes("xe");

        fc.addChoosableFileFilter(fileFilter);
        fc.setFileFilter(fileFilter);
        String lastOpenedFile = m_globalConfig.getLastOpenedFile();
        if(lastOpenedFile!= null){
           fc.setSelectedFile(new File(lastOpenedFile)); 
        } else {
            fc.setSelectedFile(new File(System.getProperty("user.dir"))); 
        }
        int returnVal = fc.showOpenDialog(Menu.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            m_globalConfig.setLastOpenedFile(selectedFile.getAbsolutePath());
            m_engine.loadExecutable(selectedFile.getAbsolutePath());
        }
        this.removeAll();
        createMenuBar();
    }
    
    public void open(String fileString){
        m_globalConfig.setLastOpenedFile(fileString);
        boolean success = m_engine.loadExecutable(fileString);
        if (!success)
            JOptionPane.showMessageDialog(m_mainFrame,
                    "Error: " + m_engine.getErrorString(),
                    "Engine Error",
                    JOptionPane.ERROR_MESSAGE);
    }
    
    private void exit(){
        m_mainFrame.dispose();
    }
    
    private void about() {
        JFrame about = new JFrame("About");
        about.setPreferredSize(new Dimension(200,200));
        about.pack();
        about.setVisible(true);
    }


    
}
