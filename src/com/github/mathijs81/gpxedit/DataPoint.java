package com.github.mathijs81.gpxedit;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * One waypoint on a GPS track. Includes a reference to the original DOM node.
 *
 * @author mathijs81
 */
public class DataPoint {
	private static final String Y_ATTRIBUTE_NAME = "lat";
	private static final String X_ATTRIBUTE_NAME = "lon";	
	
	public Node originalNode;
	public double x, y;
	
	public DataPoint(Node n) {
		originalNode = n;
		x = Double.parseDouble(n.getAttributes().getNamedItem(X_ATTRIBUTE_NAME).getNodeValue());
		y = Double.parseDouble(n.getAttributes().getNamedItem(Y_ATTRIBUTE_NAME).getNodeValue());
	}
	
	public void updateNode() {
		Attr attribute = originalNode.getOwnerDocument().createAttribute(X_ATTRIBUTE_NAME);
		attribute.setValue(Double.toString(x));
		originalNode.getAttributes().setNamedItem(attribute);
		
		attribute = originalNode.getOwnerDocument().createAttribute(Y_ATTRIBUTE_NAME);
		attribute.setValue(Double.toString(y));
		originalNode.getAttributes().setNamedItem(attribute);
	}
}