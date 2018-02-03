package cad.shape;

import java.awt.Color;
import java.awt.Graphics2D;

public class CadCircle extends CadShape {
	
	private int posX;
	private int posY;
	private int width = 1;
	private int height = 1;
	
	public CadCircle(int posX, int posY, Color color) {
		super(ShapeType.CIRCLE, color);
		this.posX = posX;
		this.posY = posY;
	}
	
	public CadCircle(int posX, int posY, int width, int height, Color color, int thickness) {
		super(ShapeType.CIRCLE, color, thickness);
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
	}
	
	public int getPosX() { 
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	public void drawCadShape(Graphics2D g) {
		g.drawOval(posX, posY, width, height);
	}

	@Override
	protected void genCoordsSet() {
		int stroke = getThickness();
		stroke = stroke > 5 ? stroke / 2 : 2;
		stroke = stroke < maxStroke ? stroke : maxStroke;
		double a = (double) width / 2; // Minor axis length on horizontal direction.
		double b = (double) height / 2; // Minor axis length on vertical direction.
		double centerX = posX + a;
		double centerY = posY + b;
		double angleStep = 120 * stroke / Math.max(a, b);
		for (double angle = 0; angle < 360; angle += angleStep) {
			// coordDeltaX & coordDeltaY mean the distance from center.
			for (int coordDeltaX = -stroke; coordDeltaX <= stroke; ++coordDeltaX) {
				for (int coordDeltaY = -stroke; coordDeltaY <= stroke; ++coordDeltaY) {
					int x = (int) (centerX + coordDeltaX + a * Math.cos((double) angle / 360 * Math.PI * 2));
					int y = (int) (centerY + coordDeltaY + b * Math.sin((double) angle / 360 * Math.PI * 2));
					addCoord(x, y); // add current calculated coordinate into coordinates set.
				}
			}
		}
	}

	@Override
	public void move(int deltaX, int deltaY) {
		posX += deltaX;
		posY += deltaY;
	}

	@Override
	public boolean increaseSize() {
		width = (int) Math.ceil(width * increaseCoef);
		height = (int) Math.ceil(height * increaseCoef);
		return true;
	}

	@Override
	public boolean decreaseSize() {
		int newWidth = (int) (width * decreaseCoef);
		int newHeight = (int) (height * decreaseCoef);
		// Cannot change the length of any side to 0:
		if (newWidth > 0 && newHeight > 0) {
			width = newWidth;
			height = newHeight;
			return true;
		}
		return false;
	}

	@Override
	public void setShapeWithPos(int x1, int y1, int x2, int y2) {
		posX = Math.min(x1, x2);
		posY = Math.min(y1, y2);
		width = Math.abs(x1 - x2);
		height = Math.abs(y1 - y2);
	}

}
