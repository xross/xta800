package com.xmos.xta.rcp.views.ConsoleView;

import java.util.*;

public class TabCompletionHandler {
    public void complete(final TextReader reader, final List<String> candidates, final int pos) {    	
        String buffer = reader.getCurrentBuffer();

    	if (candidates.size() == 1) {
            String value = candidates.get(0).toString();
            if (!value.equals(buffer)) {
                reader.insertText(value, pos);
            }            
        } else if (candidates.size() > 1) {
            printCandidates(reader, candidates, buffer);                
            String value = getUnambiguousCompletions(candidates);
            reader.insertText(value, pos);
        }
    }

    public static final void printCandidates(final TextReader reader, 
    		Collection<String> candidates, String currentBuffer) {
    	        
        // Copy the values and make them distinct, without otherwise
        // affecting the ordering. Only do it if the sizes differ.
        Set<String> distinct = new HashSet<String>(candidates);
        if (distinct.size() != candidates.size()) {
            Collection<String> copy = new ArrayList<String>();

            for (String candidate : candidates) {
                if (!(copy.contains(candidate))) {
                    copy.add(candidate);
                }
            }
            candidates = copy;
        }
        reader.insertCompletions(candidates, currentBuffer);
    }

    private final String getUnambiguousCompletions(final List<String> candidates) {
        if ((candidates == null) || (candidates.size() == 0)) {
            return null;
        }

        String[] strings = (String[])candidates.toArray(new String[candidates.size()]);
        String first = strings[0];
        StringBuffer candidate = new StringBuffer();

        for (int i = 0; i < first.length(); i++) {
            if (startsWith(first.substring(0, i + 1), strings)) {
                candidate.append(first.charAt(i));
            } else {
                break;
            }
        }
        return candidate.toString();
    }

    private final boolean startsWith(final String starts, final String[] candidates) {
        for (int i = 0; i < candidates.length; i++) {
            if (!candidates[i].startsWith(starts)) {
                return false;
            }
        }
        return true;
    }
}
