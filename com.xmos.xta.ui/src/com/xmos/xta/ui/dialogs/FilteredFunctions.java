package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.xmos.xta.ui.GuiEngine;

public class FilteredFunctions extends Dialog {
    private FilteredTree m_signalsViewer;
    static private String filterString = "";
    
    public FilteredFunctions(Shell parent) {
    	super(parent);
    	open();
    }

    protected void configureShell(Shell newShell) {
    	super.configureShell(newShell);
    	XtaDialog.configureShell(newShell, "Function selector", 0, 0);
    }

    protected void createButtonsForButtonBar(Composite parent) {
    	super.createButtonsForButtonBar(parent);
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    protected Control createDialogArea(Composite parent) {
    	Composite container = (Composite) super.createDialogArea(parent);

	    m_signalsViewer = new FilteredTree(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, new PatternFilter());
	    TreeViewer viewer = m_signalsViewer.getViewer();
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	    viewer.setContentProvider(new FunctionsContentProvider());			
	    viewer.setLabelProvider(new FunctionsLabelProvider());

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				buttonPressed(IDialogConstants.OK_ID);
			}			
		});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
				if (sel instanceof IStructuredSelection) {
					if (((IStructuredSelection)sel).size() > 0) {
						enable(true);
					} else {
						enable(false);
					}
		        }
			}
		});
	    
	    viewer.setInput(GuiEngine.getEngine());
	    m_signalsViewer.getFilterControl().setText(filterString);
	    
    	return container;
    }

    protected void buttonPressed(int id) {
    	if (id == IDialogConstants.OK_ID) {
			for (Object object : ((IStructuredSelection)m_signalsViewer.getViewer().getSelection()).toArray()) {
				if (object instanceof String) {
					GuiEngine.runFunction((String)object);
				}
			}
			Text filterControl = m_signalsViewer.getFilterControl();
			if (filterControl != null) {
				filterString = filterControl.getText();
			}
    	}
    	super.buttonPressed(id);
    }
    
    void checkButtonStatus() {
    }

	private void enable(boolean enable) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(enable);
		}
	}
}

