package com.xmos.xta.ui.views.binary.graphics;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsDevice;

public class GraphicsOptions  {
    
	private Group m_group;
	private List<Button> m_options;
	private Set<String> m_activeOptions;
	
	public GraphicsOptions(Composite parent, final GraphicsView view, GraphicsDevice device) {
		m_activeOptions = new TreeSet<String>();
		m_options= new LinkedList<Button>();
		
		m_group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		m_group.setText("Options");
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.wrap = true;
		layout.pack = false;
		m_group.setLayout(layout);

		if (device == null) {
			return;
		}

		m_activeOptions = new TreeSet<String>(); 
		device.getInfoNames(m_activeOptions);
		GraphicsUtils.setSelectedOptions(m_activeOptions);

		for (String infoName : m_activeOptions) {
			final Button option = new Button(m_group, SWT.CHECK);
			option.setText(infoName);
			option.setSelection(true);
			m_options.add(option);
			
			option.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (option.getSelection()) {
						m_activeOptions.add(option.getText());
					} else {
						m_activeOptions.remove(option.getText());
					}
					GraphicsUtils.setSelectedOptions(m_activeOptions);
					view.refresh();
				}
			});
		}
	}

	public void dispose() {
		m_group.dispose();
		for (Button option : m_options) {
			option.dispose();
		}
	}
}
