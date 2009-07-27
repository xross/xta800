package com.xmos.xta.gui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction m_aboutAction;
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
  	    m_aboutAction = ActionFactory.ABOUT.create(window);
    	register(m_aboutAction);    
    }

    protected void fillMenuBar(IMenuManager menuBar) {
   	    MenuManager helpMenu = new MenuManager("Help", null);
   	    menuBar.add(helpMenu);
   	    helpMenu.add(m_aboutAction);
    }
}
