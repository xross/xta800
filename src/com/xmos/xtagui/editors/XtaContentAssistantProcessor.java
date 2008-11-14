package com.xmos.xtagui.editors;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class XtaContentAssistantProcessor implements IContentAssistProcessor {
    public static final boolean DEBUG = true;

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		if (DEBUG) System.out.printf("XtaContentAssistantProcessor: computeCompletionProposals\n");		
		return null;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		if (DEBUG) System.out.printf("XtaContentAssistantProcessor: computeContextInformation\n");		
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		if (DEBUG) System.out.printf("XtaContentAssistantProcessor: getCompletionProposalAutoActivationCharacters\n");		
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		if (DEBUG) System.out.printf("XtaContentAssistantProcessor: getContextInformationAutoActivationCharacters\n");		
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		if (DEBUG) System.out.printf("XtaContentAssistantProcessor: getContextInformationValidator\n");		
		return null;
	}

	public String getErrorMessage() {
		if (DEBUG) System.out.printf("XtaContentAssistantProcessor: getErrorMessage\n");		
		return null;
	}
}
