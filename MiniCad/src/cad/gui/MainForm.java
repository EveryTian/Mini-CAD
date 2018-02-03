package cad.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import cad.CadShapesCtrler;

public class MainForm extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private CadShapesCtrler cadShapesCtrler = new CadShapesCtrler();
	private DrawingPadPanel drawingPadPanel = new DrawingPadPanel(cadShapesCtrler);
	private ToolBar toolBar = new ToolBar(drawingPadPanel);
	private MenuBar menuBar = new MenuBar(drawingPadPanel);
	
	public MainForm() {
		setTitle("Mini CAD (By: Ren Haotian)");
		// Register close event:
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!drawingPadPanel.getIsChangesSaved()) {
					int choice = JOptionPane.showConfirmDialog(null, "File has been modifided, save changes?","Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
					switch (choice) {
					case JOptionPane.YES_OPTION:
						drawingPadPanel.saveFile();
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
		});
		Dimension srceenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, srceenSize.width * 4 / 5, srceenSize.height * 4 / 5);
		// Add GUI components:
		setJMenuBar(menuBar);
		add(toolBar, BorderLayout.SOUTH);
		add(new JScrollPane(drawingPadPanel)); // Add a scroll bar for drawing pad.
		setResizable(true);
		setVisible(true);
	}
	

}
