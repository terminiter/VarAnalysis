package edu.iastate.varis.ui.hyperlinks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.jface.text.IRegion;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlDocumentVisitor;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.symex.position.Range;

/**
 * 
 * @author HUNG
 *
 */
public class HyperlinkManager {
	
	private static HyperlinkManager instance = new HyperlinkManager();

	private HashMap<File, LinkedList<Hyperlink>> hyperlinks = new HashMap<File, LinkedList<Hyperlink>>();
	
	public static HyperlinkManager getInstance() {
		return instance;
	}
	
	public void addHtmlDocument(HtmlDocument htmlDocument) {
		new HtmlVisitor().visitDocument(htmlDocument);
	}
	
	public void removeHtmlDocuments() {
		hyperlinks = new HashMap<File, LinkedList<Hyperlink>>();
	}
	
	public LinkedList<Hyperlink> getHyperlinks(File file) {
		if (hyperlinks.containsKey(file))
			return new LinkedList<Hyperlink>(hyperlinks.get(file));
		else
			return new LinkedList<Hyperlink>();
	}
	
	public ArrayList<Hyperlink> detectHyperlinks(File file, IRegion region) {
		ArrayList<Hyperlink> hyperlinks = new ArrayList<Hyperlink>();
		for (Hyperlink hyperlink : getHyperlinks(file)) {
			if (regionOverlapped(region, hyperlink.getHyperlinkRegion()))
				hyperlinks.add(hyperlink);
		}
		return hyperlinks;
	}
	
	private boolean regionOverlapped(IRegion region1, IRegion region2) {
		return (region1.getOffset() + region1.getLength() >= region2.getOffset()
				&& region1.getOffset() <= region2.getOffset() + region2.getLength());
	}
	
	class HtmlVisitor extends HtmlDocumentVisitor {
		
		public void visitElement(HtmlElement htmlElement) {
			super.visitElement(htmlElement);
			
			for (HCloseTag closeTag : htmlElement.getCloseTags())
				addHyperlink(htmlElement.getOpenTag().getLocation().getRanges().get(0), closeTag.getLocation().getRanges().get(0));
		}
		
		private void addHyperlink(Range fromLocation, Range toLocation) {
			if (!fromLocation.isUndefined() && !toLocation.isUndefined()) {
				if (!hyperlinks.containsKey(fromLocation.getFile()))
					hyperlinks.put(fromLocation.getFile(), new LinkedList<Hyperlink>());
				hyperlinks.get(fromLocation.getFile()).add(new Hyperlink(fromLocation, toLocation));
			}
		}
		
	}

}
