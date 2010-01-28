package com.xmos.xta.ui.views.binary.graphics;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsDevice;

public class GraphicsOptions  {
    
	private List m_options;
	private Set<String> m_activeOptions;
	
	public GraphicsOptions(Composite parent, final GraphicsView view) {
		m_activeOptions = new TreeSet<String>();
		
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Options");
		group.setLayout(new FillLayout());
		m_options = new List(group, SWT.MULTI);
		
		m_options.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_activeOptions.clear();
				for (String option : m_options.getSelection()) {
					m_activeOptions.add(option);
				}
				GraphicsUtils.setSelectedOptions(m_activeOptions);
				view.refresh();
			}
		});
	}

	public void setGraphicsObject(GraphicsDevice device) {
		m_options.removeAll();
		m_activeOptions.clear();
		if (device == null) {
			return;
		}

		m_activeOptions = new TreeSet<String>(); 
		device.getInfoNames(m_activeOptions);
		for (String infoName : m_activeOptions) {
			m_options.add(infoName);
		}
		
		// Set everything as selected
		String selected[] = new String[m_activeOptions.size()];
		m_activeOptions.toArray(selected);
		m_options.setSelection(selected);
		GraphicsUtils.setSelectedOptions(m_activeOptions);
	}

	public Set<String> getActiveOptions() {
		return m_activeOptions;
	}
}
