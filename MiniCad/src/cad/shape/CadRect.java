package cad.shape;

import java.awt.Color;
import java.awt.Graphics2D;

public class CadRect extends CadShape {
	
	private int posX;
	private int posY;
	private int width = 1;
	private int height = 1;
	
	public CadRect(int posX, int posY, Color color) {
		super(ShapeType.RECT, color);
		this.posX = posX;
		this.posY = posY;
	}
	
	public CadRect(int posX, int posY, int width, int height, Color color, int thickness) {
		super(ShapeType.RECT, color, thickness);
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
	protected void drawCadShape(Graphics2D g2d) {
		g2d.drawRect(posX, posY, width, height);
	}

	@Override
	protected void genCoordsSet() {
		int stroke = getThickness();
		stroke = stroke > 5 ? stroke / 2 : 2;
		stroke = stroke < maxStroke ? stroke : maxStroke;
		for (int m = -stroke; m <= stroke; m++) {
			for (int n = -stroke; n <= stroke; n++) {
				for (int i = posX + m; i <= posX + width + m; i++) {
					addCoord(i, posY + n);
					addCoord(i, posY + height + n);
				}
				for (int i = posY + n; i <= posY + height + n; i++) {
					addCoord(posX + m, i);
					addCoord(posX + width + m, i);
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
