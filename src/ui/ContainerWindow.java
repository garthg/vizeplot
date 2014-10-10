package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;

public class ContainerWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2724688013454868227L;

	public FileLoadPanel fileLoadPanel = new FileLoadPanel();

	public ContainerWindow() {
		super("Vize");
		
		addWindowListener(VisView.windowFocusListener);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setPreferredSize(new Dimension(500,400));
		
		setMenuBar(new MainMenuBar());
		
		Container content = getContentPane();
		content.add(fileLoadPanel,BorderLayout.NORTH);
		content.add(new DisplayRowColumnPanel(),BorderLayout.CENTER);
		content.add(new DisplayOpenerPanel(),BorderLayout.SOUTH);
		
		setLocation(20, 50);
		
		pack();
		setVisible(true);
	}
	
	public String toString() {
		return "Vize container window";
	}

}
