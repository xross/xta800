package com.xmos.xta.ui.views.binary.graphics;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.xmos.libs.configloader.ConfigLoader;
import com.xmos.libs.proginfoloader.ProgInfoEntry;
import com.xmos.libs.proginfoloader.ProgInfoLoader;
import com.xmos.libs.util.Pair;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.binary.TabbedView;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsCore;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsDevice;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsNode;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsObject;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPin;
import com.xmos.xta.ui.views.binary.graphics.tree.GraphicsTreeViewer;

public class GraphicsView implements TabbedView, IPropertyChangeListener {
    
	private Display m_display;

	private GraphicsOptions m_options;
	private GraphicsCanvas m_graphicsCanvas;
	private GraphicsTreeViewer m_treeViewer;
	private GraphicsDevice m_device;
	private GraphicsObject m_selectedObject;
	private static final int NUM_SIDES = 4;

	public GraphicsView(final Composite parent) {
		m_display = parent.getDisplay();
		parent.setLayout(new FillLayout());

	    SashForm form = new SashForm(parent, SWT.HORIZONTAL);
	    Composite left = new Composite(form, SWT.BORDER);
	    left.setLayout(new FillLayout());
	    m_treeViewer = new GraphicsTreeViewer(left);

	    SashForm right = new SashForm(form, SWT.VERTICAL);
	    Composite top = new Composite(right, SWT.BORDER);
	    top.setLayout(new FillLayout());
	    m_options = new GraphicsOptions(top, this);

	    Composite bottom = new Composite(right, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	    bottom.setLayout(new FillLayout());
	    m_graphicsCanvas = new GraphicsCanvas(bottom);

	    form.setWeights(new int[] {1, 3});
	    right.setWeights(new int[] {1, 4});

	    initialise();
		GuiEngine.addBroadcastListener(this);
		
		m_treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
				if (sel instanceof IStructuredSelection) {
					IStructuredSelection selection = ((IStructuredSelection)sel);
					if (selection.size() == 1) {
						m_selectedObject = (GraphicsObject) selection.getFirstElement();
						refresh();
					} 
		        }
			}
		});
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
		m_graphicsCanvas.setGraphicsObject(m_device, m_options.getSummary());
		m_options.setGraphicsObject(m_device);
		m_treeViewer.setGraphicsObject(m_device);
	}

	public void refresh() {
		m_graphicsCanvas.setGraphicsObject(m_selectedObject, m_options.getSummary());
	}

	private GraphicsObject buildGraphicsObject() {
		m_device = null;
		XeLoader xeLoader = GuiEngine.getEngine().getXeLoader();
		if (!xeLoader.isLoaded()) {
			return null;
		}
		
		ConfigLoader configLoader = xeLoader.getConfigLoader();
		m_device = new GraphicsDevice("Device");
		
		for (int nodeNumber : configLoader.getNodeNumbers()) {
			GraphicsNode node = new GraphicsNode(nodeNumber);
			m_device.addNode(nodeNumber, node);
			
			for (int coreNumber : configLoader.getCoreNumbers(nodeNumber)) {
				GraphicsCore core = new GraphicsCore(coreNumber);
				node.addCore(coreNumber, core);
			}					
		}
		
		Pair<Integer, Integer> fontMetrics = getFontMetrics();
		int averageCharWidth = fontMetrics.first;
		int fontHeight = fontMetrics.second;
		
		Collection<Integer> pinNumbers = configLoader.getPinNumbers();
		int pinsPerSide = (pinNumbers.size() + (NUM_SIDES - 1)) / NUM_SIDES;
		for (int pinNumber : pinNumbers) {
			int side = chooseSide(pinsPerSide, pinNumber);
			
			Map<String, String> attributes = configLoader.getPinAttributes(pinNumber);
			GraphicsPin pin = new GraphicsPin(m_display, averageCharWidth, fontHeight,
					side, pinNumber, attributes.get("name"));
			m_device.addPin(side, pin);
		}
		
		ProgInfoLoader progInfoLoader = xeLoader.getProgInfoLoader();
		if (progInfoLoader != null) {
			for (ProgInfoEntry info : progInfoLoader.getProgInfos()) {
				m_device.addInfo(m_display, info);
			}
		}
		return m_device;
	}

	private int chooseSide(int pinsPerSide, int pinNumber) {
		if (pinNumber < pinsPerSide) {
			return SWT.TOP;
		} else if (pinNumber < (pinsPerSide * 2)) {
			return SWT.RIGHT;
		} else if (pinNumber < (pinsPerSide * 3)) {
			return SWT.BOTTOM;
		} else {
			return SWT.LEFT;
		}
	}

	private Pair<Integer, Integer> getFontMetrics() {
		Image image = new Image(m_display, 1, 1);
        GC gc = new GC(image);
        int averageCharWidth = gc.getFontMetrics().getAverageCharWidth();
		int fontHeight = gc.getFontMetrics().getHeight();
        gc.dispose();
		
		return new Pair<Integer, Integer>(averageCharWidth, fontHeight);
	}
}
