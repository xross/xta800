package com.xmos.xta.gui;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

public class GuiEngine {
	private static Engine m_engine;
	private static GuiNotifier m_notifier;	
	private static GuiBroadcaster m_broadcaster;
	private static GuiLog m_log;
	private static GuiRouteInfo m_routeInfo;
	private static ISelection m_focus;
	private static Reference m_startPoint;
	private static Reference m_endPoint;
	private static Reference m_codePoint;
	private static String m_selectedFunction;
	private static String m_runMode;

	public static void initialise() {
        m_engine = new Engine(new Config());
        m_notifier = new GuiNotifier();
        m_broadcaster = new GuiBroadcaster();        
        m_routeInfo = new GuiRouteInfo(m_engine);

        m_engine.setNotifier(m_notifier);
	}
	
	public static Engine getEngine() {
		assert(m_engine != null);
		return m_engine;		
	}

	public static Notifier getNotifier() {
		assert(m_notifier != null);
		return m_notifier;		
	}
	
	public static GuiLog getLog() {
		assert(m_log != null);
		return m_log;		
	}	
	
	public static void addBroadcastListener(final IPropertyChangeListener listener) {
		m_broadcaster.addListener(listener);
	}

	public static void removeBroadcastListener(final IPropertyChangeListener listener) {
		m_broadcaster.removeListener(listener);
	}
	
	public static void broadcast(String message, String argument) {
		m_broadcaster.broadcast(message, argument);
	}
	
	public static GuiRouteInfo getRouteInfo() {
		return m_routeInfo;
	}
	
	public static ISelection getFocus() {
		return m_focus;
	}
	
	public static Reference getStartPoint() {
		return m_startPoint;
	}
	
	public static Reference getEndPoint() {
		return m_endPoint;
	}

	public static Reference getCodePoint() {
		return m_codePoint;
	}
	
	public static String getSelectedFunction() {
		return m_selectedFunction;
	}
	
	public static String getRunMode() {
		return m_runMode;
	}
	
	public static void setStartPoint(Reference startPoint) {
		if (startPoint != null) {
			Engine engine = GuiEngine.getEngine();
		    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
		}
    	m_startPoint = startPoint;
	}

	public static void setEndPoint(Reference endPoint) {
		if (endPoint != null) {
			Engine engine = GuiEngine.getEngine();
			endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
		}
		m_endPoint = endPoint;
	}
	
	public static void setFocus(ISelection selection) {
		m_focus = selection;
	}	

	public static void setCodePoint(Reference codePoint) {
		m_codePoint = codePoint;
	}
	
	public static void updateGuiState(String message, String argument) {
		if (message.equals(Notifier.NOTIFY_ERROR)) {
			m_log.add(GuiLog.Level.ERROR, argument);
			
		} else if (message.equals(Notifier.NOTIFY_WARNING)) {
			m_log.add(GuiLog.Level.WARNING, argument);
			
		} else if (message.equals(Notifier.NOTIFY_INFO)) {
			m_log.add(GuiLog.Level.INFO, argument);
			
		} else if (message.equals(Notifier.NOTIFY_SET_START_POINT)) {
			Reference startPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			if (startPoint != null) {
				Engine engine = GuiEngine.getEngine();
			    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
			}
	    	m_startPoint = startPoint;

		} else if (message.equals(Notifier.NOTIFY_SET_END_POINT)) {
			Reference endPoint = ReferenceFactory.createReference(argument, ReferenceClass.ENDPOINT_REF);			
			if (endPoint != null) {
				Engine engine = GuiEngine.getEngine();
				endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
			}
			m_endPoint = endPoint;
			
		} else if (message.equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			resetGuiState(true);
			
		} else if (message.equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			resetGuiState(false);
			
		} else if (message.equals(Notifier.NOTIFY_SELECT_FUNCTION)) {
			m_selectedFunction = argument;
			
		} else if (message.equals(Notifier.NOTIFY_SELECT_ROUTE) ||
				   message.equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			m_routeInfo.update();
			
		} else if (message.equals(Notifier.NOTIFY_SET_RUN_MODE)) {
			m_runMode = argument;
						
		} else if (message.equals(Notifier.NOTIFY_UPDATE_EXCLUDES)) {
			m_routeInfo.update();
		}
	}
	
	private static void resetGuiState(boolean isLoad) {
		m_codePoint = null;
		m_startPoint = null;
		m_endPoint = null;
		m_selectedFunction = "";
		m_runMode = Notifier.RUN_MODE_FUNCTION;

		if (isLoad) {
			m_routeInfo.resetState();
		}
	}	
}
