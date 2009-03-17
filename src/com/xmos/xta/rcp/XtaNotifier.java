package com.xmos.xta.rcp;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class XtaNotifier implements Notifier {
	private static XtaNotifier m_displayer;
	private ArrayList<IPropertyChangeListener> m_listeners;
	private XtaRouteInfo m_routeInfo;

	private ISelection m_focusSelection;
	private Reference m_startPoint;
	private Reference m_endPoint;
	private Reference m_codePoint;

	private String m_selectedFunction;
	private String m_runMode;

	private String m_notifyArgument;
    private boolean m_isGuiCommand;

	private XtaNotifier() {
		m_listeners = new ArrayList<IPropertyChangeListener>();
		resetState();
	}
	
	public static XtaNotifier getInstance() {
		if (m_displayer == null) {
			m_displayer = new XtaNotifier();					
		}
		return m_displayer;		
	}
		
	public void notify(String message, String argument) {
		if (message.equals(NOTIFY_ERROR)) {
			if (m_isGuiCommand) {
				displayMessageBox("Error", "Error detected\n(See log for details)");
				m_isGuiCommand = false;
			}
			XtaLog.getInstance().add(XtaLog.Level.ERROR, argument);
			broadcastPropertyChange(new PropertyChangeEvent(this, Notifier.NOTIFY_ERROR, null, null));								
			
		} else if (message.equals(NOTIFY_WARNING)) {
			if (m_isGuiCommand) {
				displayMessageBox("Warning", "Warnings detected\n(See log for details)");
				m_isGuiCommand = false;
			}
			XtaLog.getInstance().add(XtaLog.Level.WARNING, argument);
			broadcastPropertyChange(new PropertyChangeEvent(this, Notifier.NOTIFY_WARNING, null, null));								
			
		} else if (message.equals(NOTIFY_INFO)) {
			XtaLog.getInstance().add(XtaLog.Level.INFO, argument);
			broadcastPropertyChange(new PropertyChangeEvent(this, Notifier.NOTIFY_INFO, null, null));								

		} else if (message.equals(NOTIFY_ANALYSIS_COMPLETE)) {
			createHierarchy();
			broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_ANALYSIS_COMPLETE, null, null));
			m_isGuiCommand = false;
			
		} else if (message.equals(NOTIFY_SET_START_POINT)) {
			notifySetStartPoint(ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF));
			
		} else if (message.equals(NOTIFY_SET_END_POINT)) {
			notifySetEndPoint(ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF));
			
		} else if (message.equals(NOTIFY_LOAD_EXECUTABLE)) {
			resetState();
			m_notifyArgument = argument;
			broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
			m_isGuiCommand = false;
			
		} else if (message.equals(NOTIFY_SELECT_FUNCTION)) {
			m_selectedFunction = argument;
			broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
			
		} else if (message.equals(NOTIFY_SET_RUN_MODE)) {
			m_runMode = argument;
			
		} else if (message.equals(NOTIFY_SET_GUI_COMMAND)) {
			m_isGuiCommand = true;
			
		} else if (message.equals(NOTIFY_UPDATE_EXCLUDES)) {
			m_notifyArgument = argument;
			if (m_routeInfo != null) {
				m_routeInfo.buildRouteNode();
				broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
			}			

		} else {
			m_notifyArgument = argument;
			broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));			
		}
	}

	public void addPropertyChangeListener(final IPropertyChangeListener listener) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {        	
				if (!m_listeners.contains(listener))
					m_listeners.add(listener);
			}});
	}

	public void removePropertyChangeListener(final IPropertyChangeListener listener) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {        	
				m_listeners.remove(listener);
			}});
	}
	
	public void notifySetFocus(ISelection selection) {
		m_focusSelection = selection;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_FOCUS, null, null));
	}	

	public void notifySetStartPoint(Reference startPoint) {
		if (startPoint != null) {
			Engine engine = XtaEngine.getEngine();
		    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
		}
    	m_startPoint = startPoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_START_POINT, null, null));
	}
		
	public void notifySetEndPoint(Reference endPoint) {
		if (endPoint != null) {
			Engine engine = XtaEngine.getEngine();
			endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
		}
		m_endPoint = endPoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_END_POINT, null, null));
	}

	public void notifyShow(Reference codePoint) {
		m_codePoint = codePoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SHOW_CODE_POINT, null, null));
	}

	public void broadcastPropertyChange(final PropertyChangeEvent event) {			
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				for (IPropertyChangeListener listener : m_listeners) {
					listener.propertyChange(event);
				}
			}
		});
	}
	
	public XtaRouteInfo getRouteInfo() {
		return m_routeInfo;
	}
	
	public ISelection getFocusSelection() {
		return m_focusSelection;
	}
	
	public Reference getStartPoint() {
		return m_startPoint;
	}
	
	public Reference getEndPoint() {
		return m_endPoint;
	}

	public Reference getCodePoint() {
		return m_codePoint;
	}
	
	public String getSelectedFunction() {
		return m_selectedFunction;
	}
	
	public String getRunMode() {
		return m_runMode;
	}
	
	public String getNotifyArgument() {
		return m_notifyArgument;
	}
	
	private void resetState() {
		m_routeInfo = null;
		m_startPoint = null;
		m_endPoint = null;
		m_codePoint = null;
		m_notifyArgument = null;
		m_selectedFunction = "";
		m_runMode = Notifier.RUN_MODE_FUNCTION;
	}

	private void createHierarchy() {		
		// TODO: This needs to work with multiple routes..		
		Engine engine = XtaEngine.getEngine();			
        m_routeInfo = new XtaRouteInfo(engine.getRoute(-1), engine.getDebugInfo(), 
        		engine.getMemoryImage(), engine.getSymbolTable());
	}

	private void displayMessageBox(final String title, final String message) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0, message, null);
		        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				ErrorDialog.openError(shell, title, null, status);
			}
		});
	}
}
