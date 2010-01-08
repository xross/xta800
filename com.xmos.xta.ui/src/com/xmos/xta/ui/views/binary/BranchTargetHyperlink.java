package com.xmos.xta.ui.views.binary;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

class BranchTargetHyperlink implements IHyperlink {
    public static final boolean DEBUG = false;

    private String m_pcString;
	private IRegion m_region;

	public BranchTargetHyperlink(IRegion region, String pcString) {
		if (DEBUG) System.out.printf("BranchTargetHyperlink()\n");
		m_region = region;
		m_pcString = pcString;
	}

	public IRegion getHyperlinkRegion() {
		if (DEBUG) System.out.printf("BranchTargetHyperlink: getHyperlinkRegion\n");
		return m_region;
	}

	public void open() {
		if (DEBUG) System.out.printf("BranchTargetHyperlink: open\n");
		Reference ref = ReferenceFactory.createReference(m_pcString, ReferenceClass.LABEL_REF);
		
		Engine engine = GuiEngine.getEngine();
		boolean resolved = ref.resolve(engine);
		if (resolved) {
			GuiEngine.setCodePoint(ref);
			GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
		}
	}

	public String getTypeLabel() {
		if (DEBUG) System.out.printf("BranchTargetHyperlink: getTypeLabel\n");
		return null;
	}

	public String getHyperlinkText() {
		if (DEBUG) System.out.printf("BranchTargetHyperlink: getHyperlinkText\n");
		return null;
	}
}
