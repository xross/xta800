package com.xmos.xta.ui.views.binary.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsDevice;

public class GraphicsOptions  {
    
	private boolean m_summary;
	
	public GraphicsOptions(Composite parent, final GraphicsView view) {
		m_summary = true;
		
		Group graphOptions = createGroup(parent, "Chart type", 1, 1);
		String selectedType = GraphicsUtils.getSelectedChartType();
		for (String type : GraphicsUtils.getChartTypes()) {
			final Button option = new Button(graphOptions, SWT.RADIO);
			option.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			option.setText(type);
			option.setSelection(type.equals(selectedType));
			option.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (option.getSelection()) {
						GraphicsUtils.setChartType(option.getText());
						view.refresh();
					}
				}
			});
		}

		Group showOptions = createGroup(parent, "Show", 1, 1);
		final Button thisButton = new Button(showOptions, SWT.RADIO);
		thisButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		thisButton.setText("Summary");
		thisButton.setSelection(m_summary);

		Button allButton = new Button(showOptions, SWT.RADIO);
		allButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		allButton.setText("Details");

		SelectionAdapter showAdaptor = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_summary = thisButton.getSelection();
				view.refresh();
			}
		};
		thisButton.addSelectionListener(showAdaptor);
		allButton.addSelectionListener(showAdaptor);


		Group infoOptions = createGroup(parent, "Information", 2, 1);
		Button option1 = new Button(infoOptions, SWT.CHECK);
		option1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		option1.setText("option1");

		Button option2 = new Button(infoOptions, SWT.CHECK);
		option2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		option2.setText("option2");
	}

    Group createGroup(Composite parent, String description, int numColumns, int span) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);	    
	    if (description != null) {
	        group.setText(description);
	    }
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
	    return group;
	}

	public void setGraphicsObject(GraphicsDevice m_device) {
		
	}

	public boolean getSummary() {
		return m_summary;
	}  
}
