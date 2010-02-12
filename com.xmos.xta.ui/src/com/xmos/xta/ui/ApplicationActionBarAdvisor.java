package com.xmos.xta.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
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

import com.xmos.xta.ui.actions.ActionLoad;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction m_aboutAction;
    private IWorkbenchAction m_exitAction;
    private IWorkbenchAction m_saveAction;
    private IWorkbenchAction m_saveAsAction;

    private IAction m_openAction;
	private MenuManager m_showViewMenuMgr;
	private IContributionItem m_showViewItem;
    
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

    	m_saveAction = ActionFactory.SAVE.create(window);
    	register(m_saveAction);

    	m_saveAsAction = ActionFactory.SAVE_AS.create(window);
    	register(m_saveAsAction);

    	m_showViewMenuMgr = new MenuManager("Show View", "showView");
    	m_showViewItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);  	    
    }

    protected void fillMenuBar(IMenuManager menuBar) {
   	    MenuManager fileMenu   = new MenuManager("&File",   IWorkbenchActionConstants.M_FILE);
    	MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
   	    MenuManager helpMenu   = new MenuManager("&Help",   IWorkbenchActionConstants.M_HELP);
    	
   	    menuBar.add(fileMenu);
    	menuBar.add(windowMenu);   	    
   	    menuBar.add(helpMenu);

   	    fileMenu.add(m_openAction);
   	    fileMenu.add(m_saveAction);
   	    fileMenu.add(m_saveAsAction);
   	    fileMenu.add(m_exitAction);
   	    
   	    helpMenu.add(m_aboutAction);
   	    
    	m_showViewMenuMgr.add(m_showViewItem);
    	windowMenu.add(m_showViewMenuMgr);   	    
    }
}
