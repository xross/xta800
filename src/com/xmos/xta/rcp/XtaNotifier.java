package com.xmos.xta.rcp;

import java.util.ArrayList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

public class XtaNotifier implements Notifier {
	private static XtaNotifier m_displayer;
	private ArrayList<IPropertyChangeListener> m_listeners;
	private XtaRouteInfo m_routeInfo;

	private ISelection m_focusSelection;
	private CodeReference m_startPoint;
	private CodeReference m_endPoint;
	private CodeReference m_codePoint;
	private String m_notifyArgument;

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
			XtaLog.getInstance().add(XtaLog.Level.ERROR, argument);
			broadcastPropertyChange(new PropertyChangeEvent(this, Notifier.NOTIFY_ERROR, null, null));								
			
		} else if (message.equals(NOTIFY_WARNING)) {
			XtaLog.getInstance().add(XtaLog.Level.WARNING, argument);
			broadcastPropertyChange(new PropertyChangeEvent(this, Notifier.NOTIFY_WARNING, null, null));								
			
		} else if (message.equals(NOTIFY_INFO)) {
			XtaLog.getInstance().add(XtaLog.Level.INFO, argument);
			broadcastPropertyChange(new PropertyChangeEvent(this, Notifier.NOTIFY_INFO, null, null));								

		} else if (message.equals(NOTIFY_ANALYSIS_COMPLETE)) {
			createHierarchy();
			broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_ANALYSIS_COMPLETE, null, null));
			
		} else if (message.equals(NOTIFY_SET_START_POINT)) {
			notifySetStartPoint(CodeReferenceFactory.createCodeReference(argument));
			
		} else if (message.equals(NOTIFY_SET_END_POINT)) {
			notifySetEndPoint(CodeReferenceFactory.createCodeReference(argument));
			
		} else if (message.equals(NOTIFY_LOAD_EXECUTABLE)) {
			resetState();
			m_notifyArgument = argument;
			broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
			
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

	public void notifySetStartPoint(CodeReference startPoint) {
		if (startPoint != null) {
			Engine engine = XtaEngine.getEngine();
		    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
		}
    	m_startPoint = startPoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_START_POINT, null, null));
	}
		
	public void notifySetEndPoint(CodeReference endPoint) {
		if (endPoint != null) {
			Engine engine = XtaEngine.getEngine();
			endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
		}
		m_endPoint = endPoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_END_POINT, null, null));
	}

	public void notifyShow(CodeReference codePoint) {
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
	
	public CodeReference getStartPoint() {
		return m_startPoint;
	}
	
	public CodeReference getEndPoint() {
		return m_endPoint;
	}

	public CodeReference getCodePoint() {
		return m_codePoint;
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
	}

	private void createHierarchy() {
		Engine engine = XtaEngine.getEngine();		
        m_routeInfo = new XtaRouteInfo(engine.getRoute(), engine.getDebugInfo(), 
        		engine.getMemoryImage(), engine.getSymbolTable(), null);
	}
}
