package com.xmos.xta.rcp.views.InfoView;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.nodes.Node;

public class InfoView extends ViewPart implements IPropertyChangeListener,
		                                              ISelectionListener {
	public static final boolean DEBUG = false;
	public static final String ID = "com.xmos.xta.rcp.views.InfoView.Info";

	private UnknownsViewer m_unknowns;
	private SetTimesViewer m_setTimes;
	
	public InfoView() {
		if (DEBUG) System.out.printf("Info()\n");
		XtaNotifier.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Info: createPartControl\n");

		TabFolder folders = new TabFolder(parent, SWT.BORDER);
		m_unknowns = new UnknownsViewer(parent.getDisplay(), parent, getSite(), folders);
		m_setTimes = new SetTimesViewer(parent, getSite(), folders);

		m_unknowns.update();
		m_setTimes.update();

		getSite().getPage().addSelectionListener(this);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Info: setFocus\n");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Info: propertyChange: %s\n", event.getProperty());

		if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS) ||
			event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUDES)) {
			m_unknowns.update();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE) ||
				   event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) ||
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			m_unknowns.update();
			
		}  else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
			m_setTimes.update();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_unknowns.clear();
			m_setTimes.clear();
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("Info: selectionChanged: %s\n");

		if (sel.isEmpty())
			return;

		if (sel instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) sel).getFirstElement();
			if (object instanceof Node) {
				m_unknowns.updateHighlighting((Node)object);
			}
		}
	}
}
