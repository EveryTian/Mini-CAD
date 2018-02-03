package cad.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import cad.CadShapesCtrler;
import cad.shape.CadCircle;
import cad.shape.CadLine;
import cad.shape.CadRect;
import cad.shape.CadShape;
import cad.shape.CadText;

class DrawingPadPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private CadShapesCtrler cadShapesCtrler;
	private boolean isChangesSaved = true;
	// The fileChooser is used when open and save file from dialog.
	private JFileChooser fileChooser = new JFileChooser();
	private JPanel canvasPanel = new JPanel() {
		private static final long serialVersionUID = 1L;
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D)g;
			cadShapesCtrler.drawAllShapes(g2d);
		}
	};
	private CadShape curShape;
	private Color curColor = Color.BLACK;
	private ToolType toolType = ToolType.SELECT;
	
	private int pressedX, currentX, pressedY, currentY;
	private boolean isLeftButtonPressed = false; // Ensure the continuity of left button press.
	
	public DrawingPadPanel(CadShapesCtrler cadShapesCtrler) {
		// Initialize fileChooser:
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription(){
				return "Mini CAD File";
			}
			@Override
			public boolean accept(File file){
				return file.getName().endsWith(".cad");
			}
		});
		// Initialize drawing canvas and drawing pad:
		this.cadShapesCtrler = cadShapesCtrler;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		canvasPanel.setBackground(Color.WHITE);
		canvasPanel.setBounds(0, 0, screenSize.width * 2, screenSize.height * 2);
		setBackground(Color.GRAY);
		setLayout(null);
		add(canvasPanel);
		setPreferredSize(new Dimension(canvasPanel.getWidth(), canvasPanel.getHeight()));
		// Add event listener:
		canvasPanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (curShape == null) {
					return;
				}
				boolean shapeHasChanged;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_MINUS:
				case KeyEvent.VK_S:
					shapeHasChanged = curShape.decreaseSize();
					break;
				case KeyEvent.VK_EQUALS:
				case KeyEvent.VK_W:
					shapeHasChanged = curShape.increaseSize();
					break;
				case KeyEvent.VK_OPEN_BRACKET:
				case KeyEvent.VK_A:
					shapeHasChanged = curShape.decreaseThickness();
					break;
				case KeyEvent.VK_CLOSE_BRACKET:
				case KeyEvent.VK_D:
					shapeHasChanged = curShape.increaseThickness();
					break;
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
				case KeyEvent.VK_G:
					removeCadShape(curShape);
					curShape = null;
					shapeHasChanged = true;
					break;
				default:
					return;
				}
				if (shapeHasChanged) {
					repaint();
					isChangesSaved = false;
				}
			}
		});
		canvasPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) { // Not left mouse button.
					return;
				}
				isLeftButtonPressed = true;
				ToolType toolType = getToolType();
				if (toolType == ToolType.SELECT) {
					curShape = getCadShapeByCoord(e.getX(), e.getY());
					if (curShape == null) {
						return;
					}
					pressedX = currentX = e.getX();
					pressedY = currentY = e.getY();
				} else {
					pressedX = currentX = e.getX();
					pressedY = currentY = e.getY();
					switch (toolType) {
					case CIRCLE:
						curShape = new CadCircle(pressedX, pressedY, curColor);
						break;
					case LINE:
						curShape = new CadLine(pressedX, pressedY, curColor);
						break;
					case RECT:
						curShape = new CadRect(pressedX, pressedY, curColor);
						break;
					case TEXT:
						String text = JOptionPane.showInputDialog("Please input the text:");
						if (text != null && !text.isEmpty()) {
							curShape = new CadText(text, pressedX, pressedY, curColor);
							break;
						} 
						return;
					default:
						return;
					}
					addCadShape(curShape);
					repaint();
					isChangesSaved = false;
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					isLeftButtonPressed = false;
				}
			}
		});
		canvasPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (!isLeftButtonPressed) {
					return;
				}
				ToolType toolType = getToolType();
				if (toolType == ToolType.SELECT) {
					if (curShape == null) {
						return;
					}
					currentX = e.getX();
					currentY = e.getY();
					curShape.move(currentX - pressedX, currentY - pressedY);
					pressedX = currentX;
					pressedY = currentY;
				} else {
					switch (toolType) {
					case CIRCLE:
					case LINE:
					case RECT:
						currentX = e.getX();
						currentY = e.getY();
						curShape.setShapeWithPos(pressedX, pressedY, currentX, currentY);
						break;
					default:
						return;
					}
				}
				repaint();
				isChangesSaved = false;
			}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				canvasPanel.requestFocus();
			}
		});
	}
	
	void setToolType(ToolType toolType) {
		this.toolType = toolType;
	}
	
	private ToolType getToolType() {
		return toolType;
	}
	
	public void addCadShape(CadShape cadShape) {
		cadShapesCtrler.add(cadShape);
	}
	
	public void removeCadShape(CadShape cadShape) {
		cadShapesCtrler.remove(cadShape);
	}
	
	public void removeAddCadShape() {
		cadShapesCtrler.clear();
	}
	
	public int getCadShapeNum() {
		return cadShapesCtrler.getCadShapesNum();
	}
	
	public CadShape getCadShapeByCoord(int x, int y) {
		return cadShapesCtrler.getCadShapeByCoord(x, y);
	}
	
	public boolean getIsChangesSaved() {
		return isChangesSaved;
	}
	
	public void newFile() {
		if (!isChangesSaved) {
			int choice = JOptionPane.showConfirmDialog(null, "File has been modifided, save changes?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (choice) {
			case JOptionPane.YES_OPTION:
				saveFile();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:
				return;
			}
		}
		cadShapesCtrler.clear();
		repaint();
		isChangesSaved = true;
	}
	
	public void readFile() {
		if (!isChangesSaved) {
			int choice = JOptionPane.showConfirmDialog(null, "File has been modifided, save changes?", "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (choice) {
			case JOptionPane.YES_OPTION:
				saveFile();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:
				return;
			}
		}
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			cadShapesCtrler.setFileName(fileChooser.getSelectedFile().getAbsolutePath());
			try {
				if (!cadShapesCtrler.read()) {
					JOptionPane.showConfirmDialog(null, "Cannot read the file in Mini CAD format.", "Read File Error", JOptionPane.CLOSED_OPTION);
					return;
				}
			} catch (FileNotFoundException e) {
				JOptionPane.showConfirmDialog(null, "Cannot find the file.", "Read File Error", JOptionPane.CLOSED_OPTION);
				return;
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(null, "Cannot read the file in Mini CAD format.", "Read File Error", JOptionPane.CLOSED_OPTION);
				return;
			} catch (Exception e) {
				return;
			}
			repaint();
			isChangesSaved = true;
		}
	}
	
	public void saveFile() {
		if (!cadShapesCtrler.getIsFileNameSet()) {
			saveAsFile();
		} else {
			try {
				cadShapesCtrler.save();
			} catch (IOException e) {  
				JOptionPane.showConfirmDialog(null, "Cannot save the file.", "Save File Error", JOptionPane.CLOSED_OPTION);
				return;
			} catch (Exception e) {
				return;
			}
			isChangesSaved = true;
		}
	}
	
	public void saveAsFile() {
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			cadShapesCtrler.setFileName(fileChooser.getSelectedFile().getAbsolutePath());
			saveFile();
		}
	}
	
	public void exit() {
		if (!isChangesSaved) {
			int choice = JOptionPane.showConfirmDialog(null, "File has been modifided, save changes?","Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (choice) {
			case JOptionPane.YES_OPTION:
				saveFile();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:
				return;
			}
		}
		System.exit(0);
	}

	public void setColor(Color color) {
		if (color == null || curColor.equals(color)) {
			return;
		}
		curColor = color;
		if (this.curShape != null) {
			curShape.setColor(curColor);
			repaint();
		}
	}
	
}
