package com.xmos.xta.ui.views.binary;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.ui.views.binary.disassembly.DisassemblyView;
import com.xmos.xta.ui.views.binary.info.InfoView;

public class BinaryView extends ViewPart {
	public static final String ID = "com.xmos.xta.ui.views.binary.Binary";
	
	private TabFolder m_folders;
	private List<TabbedView> m_views;

	public void createPartControl(Composite parent) {
		m_folders = new TabFolder(parent, SWT.BORDER);
		m_views = new ArrayList<TabbedView>();

		createTab("Disassembly", new controlBuilder() {
			public TabbedView create(Composite composite) {
				return new DisassemblyView(composite, getSite());
			}			
		});
		createTab("Info", new controlBuilder() {
			public TabbedView create(Composite composite) {
				return new InfoView(composite);
			}			
		});		
		m_folders.setSelection(0);
	}

	public void dispose() {
		super.dispose();
		for (TabbedView tabbedView : m_views) {
			tabbedView.dispose();
		}
	}

	public void setFocus() {
	}
	
	private void createTab(String name, controlBuilder builder) {
		TabItem tab = new TabItem(m_folders, SWT.NONE);
		tab.setText(name);
		Composite composite = new Composite(m_folders, SWT.NONE);
		composite.setLayout(new FillLayout());		
		tab.setControl(composite);	
		TabbedView tabbedView = builder.create(composite);
		m_views.add(tabbedView);
	}

	static private interface controlBuilder {
		TabbedView create(Composite composite);
	}
}
