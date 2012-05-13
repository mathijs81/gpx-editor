package com.github.mathijs81.gpxedit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;


public class GpxView extends JComponent {
	private static final long serialVersionUID = 3268873597675436446L;
	
	private static final int POINT_SIZE = 9; // pixels
	private static final int CLICK_MARGIN = 3; // pixels

	private List<DataPoint> data = Collections.emptyList();
	double minX = 0, maxX = 5, minY = 0, maxY = 5;

	int lastPressX = -1, lastPressY = -1;

	boolean draggingPoint = false;
	int dragPointIndex = -1;
	int highlightPoint = -1;

	public GpxView() {
		// Wheel listener for zooming
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				double zoom = Math.pow(1.1, arg0.getWheelRotation());

				double center = arg0.getX() * (maxX - minX) / getWidth() + minX;
				minX = (minX - center) * zoom + center;
				maxX = (maxX - center) * zoom + center;

				center = (getHeight() - arg0.getY()) * (maxY - minY)
						/ getHeight() + minY;
				minY = (minY - center) * zoom + center;
				maxY = (maxY - center) * zoom + center;
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (draggingPoint) {
					// Update XML
					data.get(dragPointIndex).updateNode();
				}
				draggingPoint = false;
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				lastPressX = arg0.getX();
				lastPressY = arg0.getY();

				if (arg0.getButton() == MouseEvent.BUTTON3) {
					// Delete this point
					for (int i = 0; i < data.size(); i++) {
						DataPoint point = data.get(i);
						if (Math.hypot(lastPressX - getDrawX(point.x),
								lastPressY - getDrawY(point.y)) < POINT_SIZE
								/ 2 + CLICK_MARGIN) {
							data.get(i).originalNode.getParentNode().removeChild(data.get(i).originalNode);
							data.remove(i);
							highlightPoint = -1;
							repaint();
							return;
						}
					}					
				} else {
					// Check if this is near a point
					for (int i = 0; i < data.size(); i++) {
						DataPoint point = data.get(i);
						if (Math.hypot(lastPressX - getDrawX(point.x),
								lastPressY - getDrawY(point.y)) < POINT_SIZE
								/ 2 + CLICK_MARGIN) {
							draggingPoint = true;
							dragPointIndex = i;
						}
					}
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {
				if (highlightPoint == -1
						|| Math.hypot(
								arg0.getX()
										- getDrawX(data.get(highlightPoint).x),
								arg0.getY()
										- getDrawY(data.get(highlightPoint).y)) >= POINT_SIZE
								/ 2 + CLICK_MARGIN) {
					highlightPoint = -1;
					// Check if this is near a point
					for (int i = 0; i < data.size(); i++) {
						DataPoint point = data.get(i);
						if (Math.hypot(arg0.getX() - getDrawX(point.x),
								arg0.getY() - getDrawY(point.y)) < 5.0) {
							highlightPoint = i;
						}
					}
					repaint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				double offsetX = -(arg0.getX() - lastPressX) * (maxX - minX)
						/ getWidth();
				double offsetY = (arg0.getY() - lastPressY) * (maxY - minY)
						/ getHeight();
				lastPressX = arg0.getX();
				lastPressY = arg0.getY();

				if (draggingPoint) {
					data.get(dragPointIndex).x -= offsetX;
					data.get(dragPointIndex).y -= offsetY;
				} else {
					minX += offsetX;
					maxX += offsetX;
					minY += offsetY;
					maxY += offsetY;
				}
				repaint();
			}
		});
	}

	public void setData(List<DataPoint> data) {
		this.data = data;
		rescale();
		repaint();
	}

	public void rescale() {
		if (data.size() > 0) {
			minX = maxX = data.get(0).x;
			minX = minY = data.get(0).y;
			for (DataPoint point : data) {
				minX = Math.min(minX, point.x);
				maxX = Math.max(maxX, point.x);
				minY = Math.min(minY, point.y);
				maxY = Math.max(maxY, point.y);
			}
		}
	}

	private int getDrawX(double x) {
		return (int) ((x - minX) / (maxX - minX) * getWidth() + 0.5);
	}

	private int getDrawY(double y) {
		return getHeight() - 1 -
				(int) ((y - minY) / (maxY - minY) * getHeight() + 0.5);
	}

	@Override
	protected void paintComponent(Graphics arg0) {
		super.paintChildren(arg0);

		Graphics2D g2d = (Graphics2D) arg0;
		g2d.setColor(Color.GREEN.darker().darker());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.WHITE);
		for (int i = 1; i < data.size(); i++) {
			g2d.drawLine(getDrawX(data.get(i - 1).x),
					getDrawY(data.get(i - 1).y), getDrawX(data.get(i).x),
					getDrawY(data.get(i).y));
		}

		int index = 0;
		for (DataPoint point : data) {
			if (index == highlightPoint) {
				g2d.setColor(Color.RED);
			} else {
				g2d.setColor(Color.BLUE);
			}
			g2d.fillOval(getDrawX(point.x) - POINT_SIZE / 2, getDrawY(point.y)
					- POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
			index++;
		}
	}

}
