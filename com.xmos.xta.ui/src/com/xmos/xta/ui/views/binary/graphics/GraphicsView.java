package com.xmos.xta.ui.views.binary.graphics;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.xmos.libs.configloader.ConfigLoader;
import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.proginfoloader.ProgInfoLoader;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.handlers.PieHandler;
import com.xmos.xta.ui.handlers.SummaryHandler;
import com.xmos.xta.ui.views.ControlMonitor;
import com.xmos.xta.ui.views.binary.BinaryView;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsCore;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsDevice;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsLink;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsNode;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsObject;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPin;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPort;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPortBit;
import com.xmos.xta.ui.views.binary.graphics.tree.GraphicsTreeViewer;

public class GraphicsView extends ControlMonitor implements IPropertyChangeListener {
    
	private Display m_display;

	private SashForm m_left;
	private GraphicsOptions m_options;
	private GraphicsCanvas m_graphicsCanvas;
	private GraphicsTreeViewer m_treeViewer;
	private GraphicsDevice m_device;
	private GraphicsObject m_selectedObject;
	private boolean m_summary;

	public GraphicsView(final Composite parent) {
		super(BinaryView.ID, true);
		m_display = parent.getDisplay();
		
		createLayout(parent);
	    initialiseSummary(true);
	    initialiseUtils();
	    initialise();
		GuiEngine.addBroadcastListener(this);
		
		m_treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				m_selectedObject = m_treeViewer.getSelectedObject();
				refresh();
			}
		});
	}

	private void createLayout(Composite parent) {
		parent.setLayout(new FillLayout());

	    SashForm form = new SashForm(parent, SWT.HORIZONTAL);
	    m_left = new SashForm(form, SWT.VERTICAL);
	    m_treeViewer = new GraphicsTreeViewer(m_left);

	    Composite bottom = new Composite(form, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	    bottom.setLayout(new FillLayout());
	    m_graphicsCanvas = new GraphicsCanvas(bottom);

	    form.setWeights(new int[] {1, 3});
	}

	private void initialiseUtils() {
	    GraphicsUtils.initialise(m_display);
	    GraphicsUtils.setChartType(GraphicsUtils.CHART_PIE);

		// Ensure the button state matches
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(PieHandler.COMMAND_ID);
		State state = command.getState(PieHandler.STATE_ID);
		state.setValue(true);
	}

	private void initialiseSummary(boolean initialValue) {
		m_summary = initialValue;
		
		// Ensure the button state matches
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(SummaryHandler.COMMAND_ID);
		State state = command.getState(SummaryHandler.STATE_ID);
		state.setValue(initialValue);
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
		m_selectedObject = buildGraphicsObject();
		
		if (m_options != null) {
		    m_options.dispose();
		}
	    m_options = new GraphicsOptions(m_left, this, m_device);
	    m_left.setWeights(new int[] {3, 1});
	    m_left.layout(true);
	    
		m_treeViewer.setRoot(m_device);
		refresh();
	}

	public void refresh() {
		m_graphicsCanvas.setGraphicsObject(m_selectedObject, m_summary);
	}

	private GraphicsObject buildGraphicsObject() {
		m_device = null;
		XeLoader xeLoader = GuiEngine.getEngine().getXeLoader();
		if (!xeLoader.isLoaded()) {
			return null;
		}
		
		XnLoader xnLoader = xeLoader.getXnLoader();
		ConfigLoader configLoader = xeLoader.getConfigLoader();
		m_device = new GraphicsDevice("Device");
		
		for (int nodeNumber : configLoader.getNodeNumbers()) {
			GraphicsNode node = new GraphicsNode(nodeNumber);
			m_device.addNode(nodeNumber, node);
			
			for (int coreNumber : configLoader.getCoreNumbers(nodeNumber)) {
				String coreName = xnLoader.getCoreName(nodeNumber, coreNumber);
				GraphicsCore core = new GraphicsCore(coreName, coreNumber);
				node.addCore(coreNumber, core);
			}					
		}
		
		Collection<Integer> pinNumbers = configLoader.getPinNumbers();
		for (int pinNumber : pinNumbers) {
			Map<String, String> attributes = configLoader.getPinAttributes(pinNumber);
			String pinName = attributes.get("name");
			GraphicsPin pin = new GraphicsPin(pinName);
			
			addPinPorts(configLoader, xnLoader, pin, pinNumber);
			addPinLink(configLoader, pin, pinNumber);
			
			m_device.addPin(pinName, pin);
		}

		// Add ports once the pins are created
		for (int nodeNumber : configLoader.getNodeNumbers()) {
			GraphicsNode node = m_device.getNode(nodeNumber);
			for (int coreNumber : configLoader.getCoreNumbers(nodeNumber)) {
				GraphicsCore core = node.getCore(coreNumber);
				addCorePorts(configLoader, xnLoader, core, nodeNumber, coreNumber);
			}					
		}

		ProgInfoLoader progInfoLoader = xeLoader.getProgInfoLoader();
		if (progInfoLoader != null) {
			for (ProgInfoEntry info : progInfoLoader.getProgInfos()) {
				m_device.addInfo(m_display, info);
			}
		}
		return m_device;
	}

	private void addCorePorts(ConfigLoader configLoader, XnLoader xnLoader, GraphicsCore core,
			int nodeNumber, int coreNumber) {
		List<String> portNames = new LinkedList<String>();
		portNames.addAll(configLoader.getPortNames(nodeNumber, coreNumber));
		Collections.sort(portNames);
		
		if (portNames.size() == 0) {
			return;
		}
		
		for (String portName : portNames) {
			Map<String, String> attributes = configLoader.getPortAttributes(nodeNumber, coreNumber, portName);
			if (attributes.keySet().isEmpty()) {
				continue;
			}
			
			try {
				Map<String, String> xnNames = xnLoader.getPortNames(nodeNumber, coreNumber);
				String xnName = xnNames.get(portName);
				
				GraphicsPort port = new GraphicsPort(m_device, portName, xnName, nodeNumber, coreNumber);
				core.addPort(portName, port);
				
				addPortPins(configLoader, port, portName, nodeNumber, coreNumber);
				
			} catch (NumberFormatException e) {}
		}
	}

	private void addPortPins(ConfigLoader configLoader, GraphicsPort port, String portName,
			int nodeNumber, int coreNumber) {
		
		int numPins = configLoader.getNumPortPins(nodeNumber, coreNumber, portName);
		
		for (int pinNum = 0; pinNum < numPins; pinNum++) {
			Map<String, String> attributes = configLoader.getPortPinAttributes(nodeNumber, coreNumber, portName, pinNum);
			if (attributes.keySet().isEmpty()) {
				continue;
			}
			
			GraphicsPin pin = new GraphicsPin(attributes.get("name"), pinNum);
			port.addPin(pin);
		}
	}

	private void addPinPorts(ConfigLoader configLoader, XnLoader xnLoader, GraphicsPin pin, int pinNumber) {
		Set<Integer> priorities = configLoader.getPinPortPriorities(pinNumber);
		for (int priority : priorities) {
			Map<String, String> attributes = configLoader.getPinPortBitAttributes(pinNumber, priority);
			if (attributes.keySet().isEmpty()) {
				continue;
			}
			
			try {
				String name = attributes.get("name");
				int node = Integer.parseInt(attributes.get("node"));
				int core = Integer.parseInt(attributes.get("core"));
				int bitNum = Integer.parseInt(attributes.get("bitNum"));
				
				Map<String, String> xnNames = xnLoader.getPortNames(node, core);
				String xnName = xnNames.get(name);
				
				GraphicsPortBit port = new GraphicsPortBit(name, xnName, node, core, bitNum);
				pin.addPortBit(port);
				
			} catch (NumberFormatException e) {}
		}
	}

	private void addPinLink(ConfigLoader configLoader, GraphicsPin pin, int pinNumber) {
		Map<String, String> attributes = configLoader.getPinLinkAttributes(pinNumber);
		if (attributes.keySet().isEmpty()) {
			return;
		}
		
		try {
			int node = Integer.parseInt(attributes.get("node"));
			int number = Integer.parseInt(attributes.get("number"));
			int bitNum = Integer.parseInt(attributes.get("bitNum"));
			String dir = "dir_" + attributes.get("dir");
			GraphicsLink link = new GraphicsLink(node, number, bitNum, dir);
			pin.addXLink(link);
			
		} catch (NumberFormatException e) {}
	}

	public void setPie(boolean enable) {
		if (enable) {
			GraphicsUtils.setChartType(GraphicsUtils.CHART_PIE);
		} else {
			GraphicsUtils.setChartType(GraphicsUtils.CHART_BAR);
		}
		refresh();
	}

	public void setSummary(boolean enable) {
		m_summary = enable;
		refresh();
	}

	public void exportJpg() {
		m_graphicsCanvas.exportJpg();
	}

	public void zoomFit() {
		m_graphicsCanvas.zoomFit();
	}
	
	public void zoomOut() {
		m_graphicsCanvas.zoomOut();
	}
	
	public void zoomIn() {
		m_graphicsCanvas.zoomIn();
	}
	
	public void zoomReset() {
		m_graphicsCanvas.zoomReset();
	}
}
