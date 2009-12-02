package com.xmos.xta.ui.views.visualisations.timedistribution;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.TimeDistributionBuckets;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.Views;
import com.xmos.xta.ui.views.visualisations.ControlMonitor;

public class TimeDistributionView implements IPropertyChangeListener, IMenuListener, ControlMonitor {
    public static final String ID = "com.xmos.xta.ui.views.timedistribution.TimeDistribution";
    public static final boolean DEBUG = false;

    private static final int TIMELINE_Y = 30;
    private static final int SIDE_BORDER_X = 10;
    private static final int DIVISIONS = 10;

    private Canvas m_canvas;
    private List<Node> m_selectedNodes;
    private Composite m_parent;
    private Display m_display;
    private Shell m_toolTip;
	private TimeDistributionBuckets m_tdb;
	private Node m_treeNode;
	private String m_name;

	public TimeDistributionView(Composite parent) {
		m_name = "TimeDistributionView";
		if (DEBUG) System.out.println(m_name + "()");			
		GuiEngine.addBroadcastListener(this);
	    
	    m_selectedNodes = new ArrayList<Node>();
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

	    final Composite composite = new Composite(parent, SWT.BORDER); 	    
	    composite.setLayout(new FillLayout()); 

	    m_canvas = new Canvas(composite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    composite.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				m_canvas.redraw();
			}
			public void controlMoved(ControlEvent e) {
				m_canvas.redraw();
			}
	    });	    

	    composite.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	m_canvas.redraw();
		    }
	    });

	    m_canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawCanvas(e.gc);
		    }
	    });	

	    setupMouseListeners();
	    createContextMenus();
		
		initialiseTree();
		refresh();
	}
		
	public void dispose() {
		GuiEngine.removeBroadcastListener(this);
		m_canvas.dispose();
	}

	public void setFocus() {
		if (DEBUG) System.out.println(m_name + ": setFocus");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.println(m_name + ": propertyChange: " + event.getProperty());			

		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	initialiseTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
				   event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			clearView();
			refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) || 
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTree();
			refresh();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_UPDATE_FOLDING)) {
			refresh();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			updateSelection(GuiEngine.getSelectedNodes());
			refresh();
		}
	}
	
	private void refresh() {
		if (m_treeNode != null) {
			m_tdb = m_treeNode.getTimeSpread();
		} else {
			m_tdb = null;
		}
		m_canvas.redraw();
	}

	private void updateSelection(List<Node> newNodes) {
		if (newNodes != null) {
			if (newNodes.isEmpty()) {
				m_treeNode = null;
			} else {
				m_treeNode = newNodes.get(0);
			}
		}
	}

	public void menuAboutToShow(IMenuManager manager) {
    	if (m_toolTip != null  && !m_toolTip.isDisposed()) {
			m_toolTip.dispose();
    	}
    	Views.createContextMenu(manager, m_selectedNodes, m_parent.getShell());
	}

	public String getName() {
		return m_name;
	}
	
	private void clearView() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
			m_toolTip.dispose ();
		}
		m_treeNode = null;
		refresh();
	}
	
	private void createContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		contextMenu = menuMgr.createContextMenu(m_canvas);
		m_canvas.setMenu(contextMenu);
	}
	
	private void setupMouseListeners() {
		m_toolTip = null;

		Listener canvasListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {

				case SWT.MouseDown:
                	break;
                	
                case SWT.MouseUp:
                	break;
				}
			}
		};

		m_canvas.addListener(SWT.MouseMove, canvasListener);
		m_canvas.addListener(SWT.MouseUp, canvasListener);
	}

    protected void initialiseTree() {
		if (DEBUG) System.out.println(m_name + ": initialiseTree");
		updateSelection(GuiEngine.getSelectedNodes());
	}

	protected void drawCanvas(GC gc) {
		if (DEBUG) System.out.println(m_name + ": drawCanvas");
		gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle(0, 0, m_canvas.getClientArea().width,m_canvas.getClientArea().height );
		
		if (m_tdb != null){
			double currentX=SIDE_BORDER_X;
			double maxBucket = m_tdb.getMaxBucket();
			double stepX = ((double)(m_canvas.getClientArea().width- 2*SIDE_BORDER_X))/(double)TimeDistributionBuckets.BUCKET_COUNT;
			int barWidth = (int)stepX;
			if(stepX < 1.0) barWidth = 1;
				
				
			for(int i=0;i<TimeDistributionBuckets.BUCKET_COUNT;i++){
				int x = (int)((255.0/(double)TimeDistributionBuckets.BUCKET_COUNT)*(double)i);
				Color c = new Color(m_display, x, 255-x, 0);
				gc.setBackground(c);
				//gc.setBackground(m_display.getSystemColor(SWT.COLOR_DARK_BLUE));
				int height = m_canvas.getClientArea().height - TIMELINE_Y;
				double currentY = (m_tdb.getBucket(i)/maxBucket) * height;
				if(currentY > 0 && currentY < 1) currentY =1.0;
				gc.fillRectangle((int)currentX, height + 2 - (int)currentY, barWidth,(int)currentY );
				currentX += stepX;
			}
			
			//then draw the time line on
			gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
			gc.drawLine(SIDE_BORDER_X-4, m_canvas.getClientArea().height - TIMELINE_Y + TIMELINE_Y/8,
					m_canvas.getClientArea().width- SIDE_BORDER_X, m_canvas.getClientArea().height - TIMELINE_Y + TIMELINE_Y/8);
			gc.drawLine(SIDE_BORDER_X-4,4, SIDE_BORDER_X-4, m_canvas.getClientArea().height - TIMELINE_Y + TIMELINE_Y/8);
			
			long lowestBucket = m_tdb.getLowestBucketCycles();
			long highestBucket = m_tdb.getHighestBucketCycles();
			//long lowestBucket = (long) m_treeNode.getAttributes().getTimeTaken(Case.BEST, new Status(), null);
			//long highestBucket = (long) m_treeNode.getAttributes().getTimeTaken(Case.WORST, new Status(), null);
			
			double bucketInc = (double)(highestBucket -lowestBucket) / DIVISIONS; 
			currentX=SIDE_BORDER_X;
			stepX = ((double)(m_canvas.getClientArea().width- 2*SIDE_BORDER_X))/DIVISIONS;
			for(int i=0;i<=DIVISIONS; i++){
				gc.drawLine((int)currentX, m_canvas.getClientArea().height - TIMELINE_Y,
						(int)currentX, m_canvas.getClientArea().height - TIMELINE_Y + TIMELINE_Y/4 );
				if(i == DIVISIONS){
					gc.drawString("" + round((lowestBucket + bucketInc*i)), 
							(int)currentX, m_canvas.getClientArea().height - TIMELINE_Y + TIMELINE_Y/4 + 3);
				} else {
					gc.drawString("" + round((lowestBucket + bucketInc*i)), (int)currentX, m_canvas.getClientArea().height - TIMELINE_Y + TIMELINE_Y/4 + 3);
					
				}
				currentX +=stepX;
			}
		}
    }
	
	double round(double d) {
    	DecimalFormat twoDForm = new DecimalFormat("#.#");
	    return Double.valueOf(twoDForm.format(d));
    }
	
	public void setExportJpgEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_EXPORT_JPG_ENABLED, "");
	}

	public void setZoomFitEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_FIT_DISABLED, "");
	}

	public void setZoomInEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_IN_DISABLED, "");
	}

	public void setZoomOutEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_OUT_DISABLED, "");
	}

	public void setZoomResetEnabled() {
		GuiEngine.broadcast(Broadcaster.BROADCAST_ZOOM_RESET_DISABLED, "");
	}
	
	public void exportJpg() {
	}
	
	public void zoomFit() {		
	}
	
	public void zoomOut() {		
	}
	
	public void zoomIn() {
	}
	
	public void zoomReset() {		
	}	
}
