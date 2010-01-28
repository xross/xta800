package com.xmos.xta.ui.views.binary.info;

import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.xmos.libs.configloader.ConfigLoader;
import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.proginfoloader.ProgInfoLoader;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.ControlMonitor;
import com.xmos.xta.ui.views.binary.BinaryView;

public class InfoView extends ControlMonitor implements IPropertyChangeListener {
	private Text m_text;
	
	public InfoView(Composite parent) {
		super(BinaryView.ID, false);
		
		GuiEngine.addBroadcastListener(this);
		
		m_text = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		m_text.setEditable(false);

		initialise();
	}

	public void dispose() {
		GuiEngine.removeBroadcastListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			initialise();

		}
	}

	private void initialise() {
		m_text.setText("");
		XeLoader xeLoader = GuiEngine.getEngine().getXeLoader();
		if (!xeLoader.isLoaded()) {
			return;
		}

		m_text.append("XN INFORMATION\n");
		m_text.append("==============\n");
		XnLoader xnLoader = GuiEngine.getEngine().getXeLoader().getXnLoader();		    
		for (int nodeNumber : xnLoader.getNodeNumbers()) {
		    m_text.append("Node: " + nodeNumber + "\n");	
		    
			for (int coreNumber : xnLoader.getCoreNumbers(nodeNumber)) {
			    m_text.append("    Core: " + coreNumber + " (" + xnLoader.getCoreName(nodeNumber, coreNumber) + ")\n");	
			    
			    Map<String, String> portNames = xnLoader.getPortNames(nodeNumber, coreNumber);
			    for (String key : portNames.keySet()) {
			    	m_text.append("        Port: " + key + " (" + portNames.get(key) + ")\n");
			    }				
			}
		}
		m_text.append("\n");

		ProgInfoLoader progInfoLoader = GuiEngine.getEngine().getXeLoader().getProgInfoLoader();
		if (progInfoLoader != null) {
			m_text.append("PROGRAM INFORMATION\n");
			m_text.append("===================\n");
			for (ProgInfoEntry info : progInfoLoader.getProgInfos()) {
				m_text.append(String.format("Node: %d, core %d, %s usage, worst case: %d, best case: %d\n",
						info.getNode(), info.getCore(), info.getName(), info.getWorstCase(), info.getBestCase()));
			}
			m_text.append("\n");
		}
		
		m_text.append("CONFIG INFORMATION\n");
		m_text.append("==================\n");
		ConfigLoader configLoader = GuiEngine.getEngine().getXeLoader().getConfigLoader();	
		for (int nodeNumber : configLoader.getNodeNumbers()) {
			m_text.append(String.format("Node: %d", nodeNumber));
			for (String key : configLoader.getNodeAttributes(nodeNumber).keySet()) {
				m_text.append(String.format(" (%s, %s)", key, configLoader.getNodeAttributes(nodeNumber).get(key)));				
			}
			m_text.append("\n");
			
			for (int coreNumber : configLoader.getCoreNumbers(nodeNumber)) {
				m_text.append(String.format("    Core: %d", coreNumber));
				for (String key : configLoader.getCoreAttributes(nodeNumber, coreNumber).keySet()) {
					m_text.append(String.format(" (%s, %s)", key, configLoader.getCoreAttributes(nodeNumber, coreNumber).get(key)));				
				}
				m_text.append("\n");								
			}					
		}				
		for (int pinNumber : configLoader.getPinNumbers()) {
			m_text.append(String.format("Pin: %d", pinNumber));
			for (String key : configLoader.getPinAttributes(pinNumber).keySet()) {
				m_text.append(String.format(" (%s, %s)", key, configLoader.getPinAttributes(pinNumber).get(key)));				
			}
			m_text.append("\n");
		}				
		m_text.append("\n");			
	}
}
