package com.xmos.xta.gui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction m_aboutAction;
    private IWorkbenchAction m_exitAction;
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
  	    m_aboutAction = ActionFactory.ABOUT.create(window);
    	register(m_aboutAction);  
    	
    	m_exitAction = ActionFactory.QUIT.create(window);
  	    register(m_exitAction);    	
    }

    protected void fillMenuBar(IMenuManager menuBar) {
   	    MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
   	    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    	
   	    menuBar.add(fileMenu);
   	    menuBar.add(helpMenu);

   	    fileMenu.add(m_exitAction);
   	    helpMenu.add(m_aboutAction);
    }
}
