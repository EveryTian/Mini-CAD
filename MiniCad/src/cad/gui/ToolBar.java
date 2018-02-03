package cad.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JToolBar;

class ToolBar extends JToolBar {

	private class ColorsPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		final private Color[] colorsArray = {
					Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY,
					Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, 
					Color.PINK, Color.RED, Color.WHITE, Color.YELLOW
				};
		final private int colorsNum = colorsArray.length;

		private JButton[] colorButtons = new JButton[colorsNum];
		private ActionListener buttonsActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = ((JButton) e.getSource()).getBackground(); // Take the clicked color out.
				currentColorPanel.setBackground(color); // show color.
				drawingPadPanel.setColor(color);  // Set current color of drawingPadPanel.
			}
		};
		private JPanel currentColorPanel = new JPanel(); // Used for showing current color.
		private JButton moreColorsButton = new JButton("More Colors");

		ColorsPanel() {
			setLayout(null);
			for (int i = 0; i < colorsNum; ++i) {
				colorButtons[i] = new JButton();
				colorButtons[i].setBackground(colorsArray[i]);
				colorButtons[i].setBounds(31 * i, 0, 30, 30);
				colorButtons[i].addActionListener(buttonsActionListener);
				add(colorButtons[i]);
			}
			moreColorsButton.setBounds(31 * colorsNum + 5, 15, 15, 15);
			add(moreColorsButton);
			currentColorPanel.setBackground(Color.BLACK);
			currentColorPanel.setBounds(31 * colorsNum + 35, 0, 30, 30);
			add(currentColorPanel);
			moreColorsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Color color = JColorChooser.showDialog(moreColorsButton, "More Colors",
							currentColorPanel.getBackground());
					if (color != null) {
						currentColorPanel.setBackground(color); // show color.
						drawingPadPanel.setColor(color); // Set current color of drawingPadPanel.
					}
				}
			});
		}

	}

	private static final long serialVersionUID = 1L;

	final private String[] buttonsString = { "Line", "Circle", "Rect", "Text", "Select" };
	final private int buttonsNum = buttonsString.length;

	private DrawingPadPanel drawingPadPanel;
	private JButton[] buttons = new JButton[buttonsNum];
	private ActionListener buttonsActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Change curToolType by each action event:
			switch (e.getActionCommand()) {
			case "Line":
				setCurToolType(ToolType.LINE);
				break;
			case "Circle":
				setCurToolType(ToolType.CIRCLE);
				break;
			case "Rect":
				setCurToolType(ToolType.RECT);
				break;
			case "Text":
				setCurToolType(ToolType.TEXT);
				break;
			case "Select":
				setCurToolType(ToolType.SELECT);
				break;
			default:
				break;
			}
		}
	};

	public ToolBar(DrawingPadPanel drawingPadPanel) {
		this.drawingPadPanel = drawingPadPanel;
		for (int i = 0; i < buttonsNum; ++i) {
			buttons[i] = new JButton(buttonsString[i]);
			// Add action listener for each button:
			buttons[i].addActionListener(buttonsActionListener);
			add(buttons[i]);
		}
		addSeparator();
		add(new ColorsPanel());
	}

	public void setCurToolType(ToolType toolType) {
		drawingPadPanel.setToolType(toolType);
	}

}
