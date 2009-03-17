package com.xmos.xta.rcp.actions.control;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.icons.Icons;

public class ActionCancel extends Action implements IPropertyChangeListener {

	public ActionCancel() {
		XtaNotifier.getInstance().addPropertyChangeListener(this);
		setImageDescriptor(Icons.getIconDescriptor("cancel"));
		setToolTipText("Cancel analysis");
		setEnabled(false);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_BEGIN)) {			
	        setEnabled(true);
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
	        setEnabled(false);
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_ERROR)) {
	        setEnabled(false);
			
		}
	}

	public void run() {
		XtaEngine.getEngine().setAbort(true);
	}
}
