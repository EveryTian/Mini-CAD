package cad;


import javax.swing.SwingUtilities;

import cad.gui.MainForm;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				new MainForm();
			}
		});
	}

}
