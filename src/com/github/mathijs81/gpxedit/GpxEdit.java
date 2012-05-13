package com.github.mathijs81.gpxedit;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.w3c.dom.Document;

/**
 * Simple applet to load a GPX file, move and/or delete some waypoints and then save it again.
 * 
 * @author mathijs81
 */
public class GpxEdit extends JApplet {
	private static final long serialVersionUID = -910040490239527330L;

	private GpxView gpxView;
	private Document currentDoc = null;
	private JFileChooser chooser = new JFileChooser();

	public GpxEdit() {
		JPanel main = new JPanel(new BorderLayout());
		JButton loadButton = new JButton("Load GPX file.");
		main.add(loadButton, BorderLayout.NORTH);
		JButton saveButton = new JButton("Save GPX file");
		main.add(saveButton, BorderLayout.SOUTH);

		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadGpx();
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveGpx();
			}
		});

		gpxView = new GpxView();
		main.add(gpxView, BorderLayout.CENTER);
		add(main);
	}

	private void loadGpx() {
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			openFile(chooser.getSelectedFile());
		}
	}

	private void openFile(File f) {
		Document doc = null;
		try {
			doc = GpxFile.readFile(f);
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Error opening file: " + ioEx.getMessage());
			return;
		}

		currentDoc = doc;
		List<DataPoint> pointList = GpxFile.getList(doc);
		System.err.println("Read " + pointList.size() + " waypoints.");
		gpxView.setData(pointList);
	}

	private void saveFile(File f) {
		try {
			GpxFile.writeFile(currentDoc, f);
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Error saving file: " + ioEx.getMessage());
			return;
		}
	}

	private void saveGpx() {
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			saveFile(chooser.getSelectedFile());
		}
	}
}
