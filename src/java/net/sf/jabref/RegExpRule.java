/*
Copyright (C) 2003 Nathan Dunn, Morten O. Alver

All programs in this directory and
subdirectories are published under the GNU General Public License as
described below.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
USA

Further information about the GNU GPL is available at:
http://www.gnu.org/copyleft/gpl.ja.html

*/
package net.sf.jabref;

import java.util.*;
import java.util.Enumeration ; 
import java.util.regex.Pattern;

public class RegExpRule implements SearchRule{

    final boolean m_caseSensitiveSearch;
    final boolean m_searchAll;
    final boolean m_searchReq;
    final boolean m_searchOpt;
    final boolean m_searchGen;

    public RegExpRule(boolean caseSensitive, boolean searchAll, boolean searchReq, boolean searchOpt, boolean searchGen) {
        m_caseSensitiveSearch = caseSensitive;

        // 2005.03.29, trying to remove field category searches, to simplify
        // search usability.
        m_searchAll = true;
        //m_searchAll = searchAll;
        // ---------------------------------------------------

        m_searchReq = searchReq;
        m_searchOpt = searchOpt;
        m_searchGen = searchGen;
    }

    public int applyRule(Map searchStrings,BibtexEntry bibtexEntry) {

        int score =0 ; 
        Iterator e = searchStrings.values().iterator(); 

        String searchString = (String) e.next() ; 
        if(!searchString.matches("\\.\\*")){
            searchString = ".*"+searchString+".*" ; 
        }
        String tempString = null ; 

	int flags = 0;
	if (!m_caseSensitiveSearch)
	    flags = Pattern.CASE_INSENSITIVE; // testing
	Pattern pattern = Pattern.compile(searchString, flags);


	if (m_searchAll) {
	    Object[] fields = bibtexEntry.getAllFields();
	    score += searchFields(fields, bibtexEntry, pattern);
	} else {
	    if (m_searchReq) {
		String[] requiredField = bibtexEntry.getRequiredFields() ;
		score += searchFields(requiredField, bibtexEntry, pattern);
	    }
	    if (m_searchOpt) {
		String[] optionalField = bibtexEntry.getOptionalFields() ;
		score += searchFields(optionalField, bibtexEntry, pattern);
	    }
	    if (m_searchGen) {
		String[] generalField = bibtexEntry.getGeneralFields() ;
		score += searchFields(generalField, bibtexEntry, pattern);
	    }
	}

        return score ; 
    }

	protected int searchFields(Object[] fields, BibtexEntry bibtexEntry, Pattern pattern) {
	    int score = 0;
	    if (fields != null) {
		for(int i = 0 ; i < fields.length ; i++){
		    try {
			if (pattern.matcher
			    (String.valueOf(bibtexEntry.getField(fields[i].toString()))).matches()) {
			    score++;
			    //Util.pr(String.valueOf(bibtexEntry.getField(fields[i].toString())));
			}
		    }
			
		    catch(Throwable t ){
			System.err.println("Searching error: "+t) ; 
		    }
		}  
	    }
	    return score;
	}

}

