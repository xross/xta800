package com.xmos.xta.rcp;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.xmos.xta.rcp.actions.ActionCore;
import com.xmos.xta.rcp.actions.ActionFunction;
import com.xmos.xta.rcp.actions.ActionLoad;
import com.xmos.xta.rcp.actions.ActionNode;
import com.xmos.xta.rcp.actions.ActionRun;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction m_aboutAction;
    private IAction m_loadAction;
    private IAction m_nodeAction;
    private IAction m_coreAction;
    private IAction m_functionAction;
    private IAction m_runAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
  	    m_aboutAction = ActionFactory.ABOUT.create(window);
    	register(m_aboutAction);
    	
    	m_loadAction = new ActionLoad();
    	m_loadAction.setId("com.xmos.xta.rcp.actions.control.load");
    	register(m_loadAction);

    	m_nodeAction = new ActionNode();
    	m_nodeAction.setId("com.xmos.xta.rcp.actions.control.node");
    	register(m_nodeAction);

    	m_coreAction = new ActionCore();
    	m_coreAction.setId("com.xmos.xta.rcp.actions.control.core");
    	register(m_coreAction);

    	m_functionAction = new ActionFunction();
    	m_functionAction.setId("com.xmos.xta.rcp.actions.control.function");
    	register(m_functionAction);

    	m_runAction = new ActionRun();
    	m_runAction.setId("com.xmos.xta.rcp.actions.control.run");
    	register(m_runAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
   	    MenuManager helpMenu = new MenuManager("Help", null);
   	    menuBar.add(helpMenu);
   	    helpMenu.add(m_aboutAction);
    }

    protected void fillCoolBar(ICoolBarManager coolBar) {
    	IToolBarManager toolBar = new ToolBarManager(coolBar.getStyle());

    	ActionContributionItem loadActionItem = new ActionContributionItem(m_loadAction);
    	toolBar.add(loadActionItem);

    	ActionContributionItem nodeActionItem = new ActionContributionItem(m_nodeAction);
    	toolBar.add(nodeActionItem);

    	ActionContributionItem coreActionItem = new ActionContributionItem(m_coreAction);
    	toolBar.add(coreActionItem);

    	ActionContributionItem functionActionItem = new ActionContributionItem(m_functionAction);
    	toolBar.add(functionActionItem);

    	ActionContributionItem runActionItem = new ActionContributionItem(m_runAction);
    	toolBar.add(runActionItem);

    	coolBar.add(toolBar);
    }
}
