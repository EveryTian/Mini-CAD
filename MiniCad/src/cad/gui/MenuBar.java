package cad.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	// File menu:
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem newMenuItem = new JMenuItem("New");
	private JMenuItem openMenuItem = new JMenuItem("Open");
	private JMenuItem saveMenuItem = new JMenuItem("Save");
	private JMenuItem saveAsMenuItem = new JMenuItem("Save As");
	private JMenuItem exitMenuItem = new JMenuItem("Exit");
	// Tool menu:
	private JMenu toolMenu = new JMenu("Tool");
	private JMenuItem lineMenuItem = new JMenuItem("Line");
	private JMenuItem circleMenuItem = new JMenuItem("Circle");
	private JMenuItem rectMenuItem = new JMenuItem("Rect");
	private JMenuItem textMenuItem = new JMenuItem("Text");
	private JMenuItem selectMenuItem = new JMenuItem("Select");
	// About menu:
	private JMenu aboutMenu = new JMenu("About");
	private JMenuItem helpMenuItem = new JMenuItem("Help");
	private JMenuItem aboutMenuItem = new JMenuItem("About");

	public MenuBar(DrawingPadPanel drawingPadPanel) {
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.newFile();
			}
		});
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.readFile();
			}
		});
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.saveFile();
			}
		});
		saveAsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.saveAsFile();
			}
		});
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.exit();
			}
		});
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		lineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.setToolType(ToolType.LINE);
			}
		});
		circleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.setToolType(ToolType.CIRCLE);
			}
		});
		rectMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.setToolType(ToolType.RECT);
			}
		});
		textMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.setToolType(ToolType.TEXT);
			}
		});
		selectMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.setToolType(ToolType.SELECT);
			}
		});
		lineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawingPadPanel.setToolType(ToolType.LINE);
			}
		});
		toolMenu.add(selectMenuItem);
		toolMenu.addSeparator();
		toolMenu.add(lineMenuItem);
		toolMenu.add(circleMenuItem);
		toolMenu.add(rectMenuItem);
		toolMenu.add(textMenuItem);
		helpMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String helpMessage = "Keyboard Usage:\n"
						+ "Increase Size: W & +\n"
						+ "Decrease Size: S & -\n"
						+ "Increase Thickness: D & ]\n"
						+ "Decrease Thickness: A & [\n"
						+ "Delete Shape: G & (Delete) & (Backspace)";
				JOptionPane.showMessageDialog(null, helpMessage, "About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String aboutMessage = "Mini CAD (for Java Application)\n"
						+ "By: REN Haotian (3150104714@zju.edu.cn)";
				JOptionPane.showMessageDialog(null, aboutMessage, "About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		aboutMenu.add(helpMenuItem);
		aboutMenu.add(aboutMenuItem);
		add(fileMenu);
		add(toolMenu);
		add(aboutMenu);
	}

}
