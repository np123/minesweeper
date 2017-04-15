package controller;

/**
 * The GameManager class is responsible for instantiating the
 * GraphicsController class
 * @see GraphicsController
 *
 */
public class GameManager {

	public static void main(String[] args) {		
		GraphicsController game1 = new GraphicsController();
		//GraphicsController game2 = new GraphicsController();
		//GraphicsController game3 = new GraphicsController();
		Thread g1 = new Thread(game1);
		//Thread g2 = new Thread(game2);
		//Thread g3 = new Thread(game3);
		g1.start();
		//g2.start();
		//g3.start();
	}

}
