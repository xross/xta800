package com.xmos.xta.rcp;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	private MenuManager m_showViewMenuMgr;
	private IContributionItem m_showViewItem;
    private IWorkbenchAction m_exitAction;
    private IWorkbenchAction m_aboutAction;
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	m_exitAction = ActionFactory.QUIT.create(window);
  	    register(m_exitAction);

  	    m_aboutAction = ActionFactory.ABOUT.create(window);
    	register(m_aboutAction);

    	m_showViewMenuMgr = new MenuManager("Show View", "showView");
    	m_showViewItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
   	    MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
    	MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
   	    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    	
   	    menuBar.add(fileMenu);
    	menuBar.add(windowMenu);
   	    menuBar.add(helpMenu);

   	    fileMenu.add(m_exitAction);
   	    helpMenu.add(m_aboutAction);
    	
    	m_showViewMenuMgr.add(m_showViewItem);
    	windowMenu.add(m_showViewMenuMgr);
    }
}
