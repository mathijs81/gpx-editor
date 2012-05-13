package com.github.mathijs81.gpxedit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class GpxFile {
	private static final String DATAPOINT_NODE_NAME = "trkpt";
	private static final String SEGMENT_NODE_NAME = "trkseg";
	private static final String TRACK_NODE_NAME = "trk";
	private static final String MAIN_NODE_NAME = "gpx";

	public static Document readFile(File f) throws IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		try {
			return builder.parse(f);
		} catch (SAXException se) {
			throw new IOException(se);
		}
	}
	
	public static void writeFile(Document doc, File f) throws IOException {
	    try {
	        Source source = new DOMSource(doc);
	        Result result = new StreamResult(f);

	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(source, result);
	    } catch (TransformerConfigurationException e) {
	    	throw new IOException(e);
	    } catch (TransformerException e) {
	    	throw new IOException(e);
	    }
	}
	
	public static List<DataPoint> getList(Document doc) {
		Node mainNode = doc.getChildNodes().item(0);
		// mainNode = "gpx"
		if (!mainNode.getNodeName().equals(MAIN_NODE_NAME)) {
			throw new IllegalStateException("First item in file should be <gpx>");
		}
		
		// Get first track "trk"
		for (int i = 0; i < mainNode.getChildNodes().getLength(); i++) {
			Node node = mainNode.getChildNodes().item(i);
			if (node.getNodeName().equals(TRACK_NODE_NAME)) {
				return getPoints(node);
			}
		}
		
		return null;
	}
	
	private static List<DataPoint> getPoints(Node trackNode) {
		List<DataPoint> points = new ArrayList<DataPoint>();
		
		// Find trksegs and add them
		for (int i = 0; i < trackNode.getChildNodes().getLength(); i++) {
			if (trackNode.getChildNodes().item(i).getNodeName().equals(SEGMENT_NODE_NAME)) {
				addSegment(trackNode.getChildNodes().item(i), points);
			}
		}
		return points;
	}
	
	private static void addSegment(Node trkseg, List<DataPoint> points) {
		for (int i = 0; i < trkseg.getChildNodes().getLength(); i++) {
			Node n = trkseg.getChildNodes().item(i);
			if (n.getNodeName().equals(DATAPOINT_NODE_NAME)) {
				DataPoint point = new DataPoint(n);
				points.add(point);
			}
		}
	}
}
