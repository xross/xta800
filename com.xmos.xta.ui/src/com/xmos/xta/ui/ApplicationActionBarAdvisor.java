package com.xmos.xta.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.xmos.xta.ui.actions.ActionLoad;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction m_aboutAction;
    private IWorkbenchAction m_exitAction;
    private IAction m_openAction;
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
  	    m_aboutAction = ActionFactory.ABOUT.create(window);
    	register(m_aboutAction);  
    	
    	m_exitAction = ActionFactory.QUIT.create(window);
  	    register(m_exitAction);
  	    
  	    m_openAction = new Action() {
  	    	public String getId()   { return "open"; }
  	    	public String getText() { return "Open"; }

  	    	public void run() {
  	    		ActionLoad actionLoad = new ActionLoad();
  	    		actionLoad.run(this);
  	    	}
  	    }; 
    }

    protected void fillMenuBar(IMenuManager menuBar) {
   	    MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
   	    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    	
   	    menuBar.add(fileMenu);
   	    menuBar.add(helpMenu);

   	    fileMenu.add(m_openAction);
   	    fileMenu.add(m_exitAction);
   	    helpMenu.add(m_aboutAction);
    }
}
