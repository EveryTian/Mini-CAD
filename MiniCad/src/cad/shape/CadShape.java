package cad.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;

public abstract class CadShape {
	
	final protected double increaseCoef = 1.05;
	final protected double decreaseCoef = 0.95;
	final protected int maxStroke = 20;

	private class Coord {
		
		private short x;
		private short y;
		
		private Coord(int x, int y) {
			this.x = (short) (x & 0xFFFF);
			this.y = (short) (y & 0xFFFF);
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || o.getClass() != getClass()) {
				return false;
			}
			Coord coordObject = (Coord)o;
			return coordObject.x == x && coordObject.y == y;
		}
		
		@Override
		public int hashCode() {
			return ((int) x << 16) | y;
		}
		
	}

	public enum ShapeType {
		UNDEFINED, LINE, CIRCLE, RECT, TEXT
	}
	
	private ShapeType shapeType = ShapeType.UNDEFINED;
	
	public ShapeType getShapeType() {
		return shapeType;
	}
	
	private Color color = Color.BLACK;
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		if (color != null) {
			this.color = color;	
		}
	}
	
	private int thickness = 3;
	
	public int getThickness() {
		return thickness;
	}
	
	public boolean increaseThickness() {
		if (thickness < maxStroke * 2) {
			++thickness;
			return true;
		}
		return false;
	}
	
	public boolean decreaseThickness() {
		if (thickness > 1) {
			--thickness;
			return true;
		}
		return false;
	}
	
	private HashSet<Coord> coordsSet = new HashSet<Coord>();
	
	public boolean containsCoord(int x, int y) {
		return coordsSet.contains(new Coord(x, y));
	}
	
	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		drawCadShape(g2d);
		coordsSet.clear();
		genCoordsSet();
		// For coordinates cover test:
		/*
		g2d.setColor(Color.CYAN);
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		for (Coord coord : coordsSet) {
			g2d.drawLine(coord.x, coord.y, coord.x, coord.y);
		}
		*/
		/*
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		drawCadShape(g2d);
		*/
	}
	
	abstract protected void drawCadShape(Graphics2D g2d);
	
	abstract protected void genCoordsSet();
	
	// When generateCoord, need to add coordinates to CoordSet:
	protected void addCoord(int x, int y) {
		coordsSet.add(new Coord(x, y));
	}
	
	// Move shape from (currentX, currentY) to (currentX + deltaX, currentY + deltaY):
	abstract public void move(int deltaX, int deltaY);
	
	// Used for changing the size of the shape:
	abstract public boolean increaseSize();
	abstract public boolean decreaseSize();
	
	// Used for creating shape by dragging:
	abstract public void setShapeWithPos(int x1, int y1, int x2, int y2);
	
	// Constructor for creating shape by user:
	protected CadShape(ShapeType shapeType, Color color) {
		this.shapeType = shapeType;
		this.color = color;
	}
	
	// Constructor for creating shape by system, namely, from file:
	protected CadShape(ShapeType shapeType, Color color, int thickness) {
		this.shapeType = shapeType;
		if (color != null) {
			this.color = color;
		}
		if (0 < thickness && thickness <= 2 * maxStroke) {
			this.thickness = thickness;
		}
	}
	
}
