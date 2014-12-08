package edu.iastate.parsers.html.sax.nodes;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HOpenTag extends HtmlSaxNode {
	
	private String type;
	private ArrayList<HtmlAttribute> attributes = new ArrayList<HtmlAttribute>();
	
	/**
	 * Constructor
	 */
	public HOpenTag(String type, PositionRange location) {
		super(location);
		this.type = type.toLowerCase();
	}
	
	/*
	 * Set properties
	 */

	public void addAttribute(HtmlAttribute attribute) {
		attributes.add(attribute);
	}
	
	public void removeAllAttributes() {
		attributes = new ArrayList<HtmlAttribute>();
	}
	
	/*
	 * Get properties
	 */
	
	public String getType() {
		return type;
	}
	
	public ArrayList<HtmlAttribute> getAttributes() {
		return new ArrayList<HtmlAttribute>(attributes);
	}
	
	/*
	 * Other methods
	 */
	
	public HtmlAttribute getLastAttribute() {
		return attributes.get(attributes.size() - 1);
	}
	
	public HtmlAttribute getAttribute(String attributeName) {
		for (HtmlNode attribute : attributes) {
			if (((HtmlAttribute) attribute).getName().equals(attributeName.toLowerCase()))
				return (HtmlAttribute) attribute;
		}
		return null;
	}
	
	public HtmlAttributeValue getAttributeValue(String attributeName) {
		HtmlAttribute attribute = getAttribute(attributeName);
		if (attribute != null)
			return attribute.getAttributeValue();
		else
			return null;
	}
	
	public HOpenTag clone() {
		HOpenTag clonedOpenTag = new HOpenTag(type, location);
		for (HtmlAttribute attr : attributes)
			clonedOpenTag.addAttribute(attr.clone());
		return clonedOpenTag;
	}
	
	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		str.append("OpenTag: " + type);
		if (!attributes.isEmpty()) {
			for (HtmlAttribute attribute : attributes)
				str.append(", " + attribute.toDebugString());
		}
		return str.toString();
	}

}