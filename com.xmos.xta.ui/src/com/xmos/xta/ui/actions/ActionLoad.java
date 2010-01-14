package com.xmos.xta.ui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.ConsoleNotifier;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.engine.CommandRunner;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.GuiScriptManager;
import com.xmos.xta.ui.GuiProgressMonitor;
import com.xmos.xta.ui.XtaPlugin;

public class ActionLoad extends ActionDelegate implements IWorkbenchWindowPulldownDelegate {
	protected static final int FILE_LIST_SIZE = 10;
	private IAction m_action;
	private SelectionListener m_listListener;
	
	public ActionLoad() {
		m_listListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				assert(false);
			}

			public void widgetSelected(SelectionEvent e) {
				String selectedFile = ((MenuItem)e.getSource()).getText();
				loadFile(selectedFile);
			}
		};
	}

	public void init(IAction action) {
		m_action = action;
		
		String selectedFile = GuiEngine.getCommandLineXeFile();
		if (selectedFile != null) {
			loadFile(selectedFile);
		}
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		m_action = action;
		
		FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Open File");
        String[] filterExt = { "*.xe;*.xta", "*.*" };
        fileDialog.setFilterExtensions(filterExt);
        
        XtaPlugin plugin = XtaPlugin.getDefault();        
        String lastDir = plugin.getPreferenceStore().getString("LastDir");
        
        fileDialog.setFilterPath(lastDir);        
        String fileName = fileDialog.open();
        
        if (fileName == null)
        	return;
                
        String path = new File(fileName).getParent();
        plugin.getPreferenceStore().setValue("LastDir", path);
        loadFile(fileName);
	}
	
	public void loadFile(String fileName) {
		if (fileName.endsWith(".xta")) {
        	boolean success = GuiScriptManager.openScriptFile(fileName);
            updateFileHistory(fileName, success);
        } else {
        	loadXeFile(fileName);
        }
	}

   	public void loadXeFile(final String xeFileName) {
       	final File xeFile = new File(xeFileName);
        Job job = new Job("Load: " + xeFileName) {
			protected IStatus run(final IProgressMonitor monitor) {
				final Engine engine = GuiEngine.getEngine();
				engine.setMonitor(new GuiProgressMonitor(monitor));

				boolean success = engine.loadExecutable(GuiEngine.s_guiSrc, xeFile, new CommandRunner() {
					public void run(String command, CommandSource src, int commandNum) {
						runSrcLevelCommand(command, src, commandNum);
					}
				});
				
				GuiEngine.broadcast(Broadcaster.BROADCAST_SRC_COMMANDS_DONE, "");
				engine.setMonitor(null);
	        	if (success && (m_action != null)) {	        		
	        		m_action.setToolTipText("Currently loaded " + xeFile.getAbsolutePath());
	        	} 	        		        	
	        	
	        	updateFileHistory(xeFileName, success);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	private void updateFileHistory(String fileName, boolean success) {
    	XtaPlugin plugin = XtaPlugin.getDefault(); 
    	String fileHistory = plugin.getPreferenceStore().getString("FileHistory");
    	String[] files = fileHistory.split(";");
    	List<String> fileList = new LinkedList<String>();
    	for (String file : files) {
    		if (file.length() > 0) {
    			fileList.add(file);
    		}
    	}
    		        	
    	fileList.remove(fileName);
    	if (success) {
    		fileList.add(fileName);
    	}
    	while (fileList.size() > FILE_LIST_SIZE) {
    		fileList.remove(0);
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	for (String file : fileList){
    		sb.append(file +";");
    	}
    	
    	plugin.getPreferenceStore().setValue("FileHistory", sb.toString());
	}

    private void runSrcLevelCommand(String commandStr, CommandSource src, int commandNum) {
    	Console console = GuiEngine.getConsole();
    	if (commandNum == 0) {
    		console.display("\n");
    	}
	    Notifier notifier = new ConsoleNotifier(console);

		Shell shell = new Shell(console, GuiEngine.getEngine(), notifier);
		shell.execute(commandStr, src);
    }

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
		XtaPlugin plugin = XtaPlugin.getDefault();
     	String fileHistory = plugin.getPreferenceStore().getString("FileHistory");
     	
     	String[] files = fileHistory.split(";");
     	
     	List<String> fileStrings = new ArrayList<String>();
     	
     	for (String file : files){
     		fileStrings.add(file);
     	}
     	
     	Collections.reverse(fileStrings);
     	
    	for (String file : fileStrings){
    		if (file.length() > 0 ){
	        	MenuItem item = new MenuItem(menu, SWT.NONE);
	    		item.setText(file);
	    		item.addSelectionListener(m_listListener);
    		}
        }
		
		return menu;
	}
}
