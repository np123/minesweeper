package controller;

import javax.swing.SwingUtilities;

/**
 * The GameManager class is responsible for instantiating the
 * GraphicsController class
 * @see GraphicsController
 *
 */
public class GameManager {

	public static void main(String[] args) {		
			
		boolean debug = false;
		
		if (args != null && args.length > 0&& args[0].equals("-debug")){
			debug = true;
			System.out.println("debug enabled");
		}
		GraphicsController game1 = new GraphicsController(debug);		
		GraphicsController game2 = new GraphicsController(debug);
		//GraphicsController game3 = new GraphicsController(debug);
		
		Thread g1 = new Thread(game1);
		Thread g2 = new Thread(game2);
		//Thread g3 = new Thread(game3);
		SwingUtilities.invokeLater(g1);
		SwingUtilities.invokeLater(g2);

	}

}
