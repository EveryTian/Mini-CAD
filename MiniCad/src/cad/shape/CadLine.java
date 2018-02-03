package cad.shape;

import java.awt.Color;
import java.awt.Graphics2D;

public class CadLine extends CadShape {
	
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	// Store initialized information:
	private boolean kExist; // Slope not exists when k is INF.
	private double k; // Slope.
	private double centerX;
	private double centerY;
	
	// Used for slope correction:
	final private int minDeflectionCorrectionInterval = 3; // Shall not correct slope continuously.
	private int increaseDeflectionNum = 0;
	private int decreaseDeflectionNum = 0;

	public CadLine(int pressedX, int pressedY, Color color) {
		super(ShapeType.LINE, color);
		x1 = x2 = pressedX;
		y1 = y2 = pressedY;
		kExist = x1 != x2;
		if (kExist) {
			k = (double) (y1 - y2) / (x1 - x2);
		}
		centerX = (x1 + x2) / 2.0;
		centerY = (y1 + y2) / 2.0;
	}
	
	public CadLine(int x1, int y1, int x2, int y2, Color color, int thickness) {
		super(ShapeType.LINE, color, thickness);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		kExist = x1 != x2;
		if (kExist) {
			k = (double) (y1 - y2) / (x1 - x2);
		}
		centerX = (x1 + x2) / 2.0;
		centerY = (y1 + y2) / 2.0;
	}
	
	public int getX1() { 
		return x1;
	}
	
	public int getY1() {
		return y1;
	}
	
	public int getX2() { 
		return x2;
	}
	
	public int getY2() {
		return y2;
	}

	@Override
	protected void drawCadShape(Graphics2D g2d) {
		g2d.drawLine(x1, y1, x2, y2);
	}

	@Override
	protected void genCoordsSet() {
		int stroke = getThickness();
		stroke = stroke > 5 ? stroke / 2 : 2;
		stroke = stroke < maxStroke ? stroke : maxStroke;
		if (x1 == x2) { // Slope is infinity.
			int yMin = Math.min(y1, y2) - stroke;
			int yMax = Math.max(y1, y2) + stroke;
			for (int x = x1 - stroke; x <= x1 + stroke; ++x) {
				for (int y = yMin; y <= yMax; ++y) {
					addCoord(x, y);
				}
			}
		} else if (y1 == y2) { // Slope is 0.
			int xMin = Math.min(x1, x2) - stroke;
			int xMax = Math.max(x1, x2) + stroke;
			for (int y = y1 - stroke; y <= y1 + stroke; ++y) {
				for (int x = xMin; x <= xMax; ++x) {
					addCoord(x, y);
				}
			}
		} else {
			double k = (double) (y2 - y1) / (x2 - x1); // k is slope.
			// Guarantee coverage in two condition:
			if (-1 < k && k <= 1) {
				int xMin, xMax, yBegin;
				if (x1 < x2) {
					xMin = x1 - stroke;
					xMax = x2 + stroke;
					yBegin = (int) (y1 - stroke * k);
				} else {
					xMin = x2 - stroke;
					xMax = x1 + stroke;
					yBegin = (int) (y2 - stroke * k);
				}
				for (int x = xMin; x <= xMax; ++x) {
					double yCenter = yBegin + (x - xMin) * k;
					for (int y = (int) yCenter - stroke; y <= (int) Math.ceil(yCenter) + stroke; ++y) {
						addCoord(x, y);
					}
				}
			} else {
				int yMin, yMax, xBegin;
				if (y1 < y2) {
					yMin = y1 - stroke;
					yMax = y2 + stroke;
					xBegin = (int) (x1 - stroke / k);
				} else {
					yMin = y2 - stroke;
					yMax = y1 + stroke;
					xBegin = (int) (x2 - stroke / k);
				}
				for (int y = yMin; y <= yMax; ++y) {
					double xCenter = xBegin + (y - yMin) / k;
					for (int x = (int) xCenter - stroke; x <= (int) Math.ceil(xCenter) + stroke; ++x) {
						addCoord(x, y);
					}
				}
			}
		}
	}

	@Override
	public void move(int deltaX, int deltaY) {
		x1 += deltaX;
		y1 += deltaY;
		x2 += deltaX;
		y2 += deltaY;
		centerX += deltaX;
		centerY += deltaY;
	}

	@Override
	public boolean increaseSize() {
		int halfHeightIncreaseSize = (int) Math.ceil((increaseCoef - 1) * (y2 - y1) / 2);
		int halfWidthIncreaseSize = (int) Math.ceil((increaseCoef - 1) * (x2 - x1) / 2);
		if (halfHeightIncreaseSize == 0 && halfWidthIncreaseSize == 0) {
			++halfHeightIncreaseSize;
			++halfWidthIncreaseSize;
		}
		x1 -= halfWidthIncreaseSize;
		x2 += halfWidthIncreaseSize;
		y1 -= halfHeightIncreaseSize;
		y2 += halfHeightIncreaseSize;
		// Correct slope:
		if (kExist) {
			++increaseDeflectionNum;
			double angle = Math.atan(k);
			double length = Math.sqrt((y1 - y2) * (y1 - y2) + (x1 - x2) * (x1 - x2));
			if (increaseDeflectionNum >= minDeflectionCorrectionInterval
					&& (x1 == x2 || Math.abs(angle - Math.atan((y1 - y2) / (x1 - x2))) > getDeflectionCorrectionAngle((int) length))) {
				increaseDeflectionNum = 0;
				double halfLength = length / 2;
				double halfWidth = halfLength * Math.cos(angle);
				double halfHeight = halfLength * Math.sin(angle);
				double newX1 = (int) (centerX - halfWidth);
				double newX2 = (int) (centerX + halfWidth);
				double newY1 = (int) (centerY - halfHeight);
				double newY2 = (int) (centerY + halfHeight);
				if (newX1 < newX2) {
					x1 = (int) newX1;
					x2 = (int) Math.ceil(newX2);
				} else {
					x1 = (int) Math.ceil(newX1);
					x2 = (int) newX2;
				}
				if (newY1 < newY2) {
					y1 = (int) newY1;
					y2 = (int) Math.ceil(newY2);
				} else {
					y1 = (int) Math.ceil(newY1);
					y2 = (int) newY2;
				}
			}
		}
		return true;
	}

	@Override
	public boolean decreaseSize() {
		int halfHeightDecreaseSize = (int) Math.ceil((1 - decreaseCoef) * (y2 - y1) / 2);
		int halfWidthDecreaseSize = (int) Math.ceil((1 - decreaseCoef) * (x2 - x1) / 2);
		if (halfHeightDecreaseSize == 0 && halfWidthDecreaseSize == 0) {
			return false;
		}
		x1 += halfWidthDecreaseSize;
		x2 -= halfWidthDecreaseSize;
		y1 += halfHeightDecreaseSize;
		y2 -= halfHeightDecreaseSize;
		// Correct slope:
		if (kExist) {
			++decreaseDeflectionNum;
			double angle = Math.atan(k);
			double length =  Math.sqrt((y1 - y2) * (y1 - y2) + (x1 - x2) * (x1 - x2));
			if (decreaseDeflectionNum >= minDeflectionCorrectionInterval
					&& (x1 == x2 || Math.abs(angle - Math.atan((y1 - y2) / (x1 - x2))) > getDeflectionCorrectionAngle((int) length))) {
				decreaseDeflectionNum = 0;
				double halfLength = length / 2;
				double halfWidth = halfLength * Math.cos(angle);
				double halfHeight = halfLength * Math.sin(angle);
				double newX1 = (int) (centerX - halfWidth);
				double newX2 = (int) (centerX + halfWidth);
				double newY1 = (int) (centerY - halfHeight);
				double newY2 = (int) (centerY + halfHeight);
				if (newX1 > newX2) {
					x1 = (int) newX1;
					x2 = (int) Math.ceil(newX2);
				} else {
					x1 = (int) Math.ceil(newX1);
					x2 = (int) newX2;
				}
				if (newY1 > newY2) {
					y1 = (int) newY1;
					y2 = (int) Math.ceil(newY2);
				} else {
					y1 = (int) Math.ceil(newY1);
					y2 = (int) newY2;
				}
			}
		}
		return true;
	}

	private double getDeflectionCorrectionAngle(int length) {
		return 18 * Math.pow(Math.E, -0.005 * length) * 2 * Math.PI / 360;
	}

	
	@Override
	public void setShapeWithPos(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		kExist = x1 != x2;
		if (kExist) {
			k = (double) (y1 - y2) / (x1 - x2);
		}
		centerX = (x1 + x2) / 2.0;
		centerY = (y1 + y2) / 2.0;
	}

}
