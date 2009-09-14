package com.xmos.xta.gui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.xmos.xta.gui.GuiEngine;

public class FilteredFunctions extends XtaDialog {
    private FilteredTree m_signalsViewer;
    static private String filterString = "";
    
    public FilteredFunctions(Shell parent) {
    	super(parent);
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	configureShell(newShell, "Function selector", 0, 0);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    m_signalsViewer = new FilteredTree(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, new PatternFilter());
	    TreeViewer viewer = m_signalsViewer.getViewer();
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	    viewer.setContentProvider(new FunctionsContentProvider());			
	    viewer.setLabelProvider(new FunctionsLabelProvider());

		m_signalsViewer.getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				buttonPressed(IDialogConstants.OK_ID);
			}			
		});
	    
	    viewer.setInput(GuiEngine.getEngine().getFunctionNames());
	    m_signalsViewer.getFilterControl().setText(filterString);
	    
    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
			Object object = ((IStructuredSelection)m_signalsViewer.getViewer().getSelection()).getFirstElement();
			if (object instanceof String) {
				GuiEngine.runFunction((String)object);
			}
			Text filterControl = m_signalsViewer.getFilterControl();
			if (filterControl != null) {
				filterString = filterControl.getText();
			}
    	}
    	super.buttonPressed(id);
    }
}

