package cad.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class CadText extends CadShape {
	
	private int posX;
	private int posY;
	private String text = "";
	private int size = 20;
	
	private double width;
	private double height;

	public CadText(String text, int posX, int posY, Color color) {
		super(ShapeType.TEXT, color);
		this.posX = posX;
		this.posY = posY;
		this.text = text;
	}
	
	public CadText(String text, int posX, int posY, int size, Color color) {
		super(ShapeType.TEXT, color, 0);
		this.posX = posX;
		this.posY = posY;
		this.text = text;
		this.size = size;
	}
	
	public int getPosX() { 
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public String getText() {
		return text;
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	protected void drawCadShape(Graphics2D g2d) {
		Font font = new Font(null, Font.PLAIN, size);
		g2d.setFont(font);
		// Get width and height:
		FontRenderContext context = g2d.getFontRenderContext();
		Rectangle2D stringBounds = font.getStringBounds(text, context);
		height = stringBounds.getHeight() / 2;
		width = stringBounds.getWidth();
		g2d.drawString(text, posX, posY + (int) height);
	}

	@Override
	protected void genCoordsSet() {
		for (int x = posX; x <= posX + width; x++) {
			for (int y = posY; y <= posY + height; y++) {
				addCoord(x, y);
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
		size = (int) Math.ceil(size * increaseCoef);
		return true;
	}

	@Override
	public boolean decreaseSize() {
		int newSize = (int) (size * decreaseCoef);
		if (newSize > 0) {
			size = newSize;
			return true;
		}
		return false;
	}

	@Override
	public void setShapeWithPos(int x1, int y1, int x2, int y2) {
		// This Function shall not be called by the CadText class.
	}

}
