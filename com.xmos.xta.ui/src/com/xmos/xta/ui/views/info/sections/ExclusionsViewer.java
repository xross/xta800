package com.xmos.xta.ui.views.info.sections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.ui.GuiEngine;

public class ExclusionsViewer extends SectionsViewer {
	private Action m_removeExclusionAction;

	public ExclusionsViewer(Display display, Composite parent, 
			IWorkbenchPartSite site, TableViewer viewer) {
		super(display, parent, site, viewer);
		
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				if (selection.isEmpty()) {
					m_removeExclusionAction.setEnabled(false);
				} else {
					m_removeExclusionAction.setEnabled(true);
				}
			}			
		});
		
		getViewer().getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL) {
					removeSelection();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		// Create the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(false);
		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, getViewer());		
		createContextMenuActions(menuMgr);	
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	SectionsViewerContentProvider createContentProvider() {
		return new ExclusionsViewerContentProvider();
	}
	
	SectionsViewerLabelProvider createLabelProvider() {
		return new ExclusionsViewerLabelProvider();
	}

 	void createContextMenuActions(MenuManager menuMgr) {
		// Create the 'Set Instruction Time' action
 		m_removeExclusionAction = new Action() {
			public void run() {
				removeSelection();
			}
		}; 
		m_removeExclusionAction.setText("Remove");
		menuMgr.add(m_removeExclusionAction);
		m_removeExclusionAction.setEnabled(false);
	} 		

 	private void removeSelection() {
		ISelection selection = getViewer().getSelection(); 
		for (Object object : ((IStructuredSelection)selection).toList()) {
			Reference ref = (Reference)object;			
			GuiEngine.getEngine().removeExclusion(GuiEngine.s_guiCommandSrc, ref.getRef());
		}
	}

 	@Override
	protected long getPc(Object object) {
		return ((Reference)object).getPc();
	}
}
