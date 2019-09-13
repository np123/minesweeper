package controller;

import static javax.swing.SwingUtilities.invokeLater;

import java.util.LinkedList;
import java.util.List;

/**
 * The GameManager class is responsible for instantiating the
 * GraphicsController class.
 * @see GraphicsController
 *
 */
public class GameManager {

  private static final int MAX_WINDOWS = 2;

  /**  Entry point for application. */
  public static void main(String[] args) {

    boolean debug = false;

    if (args != null && args.length > 0 && args[0].equals("-debug")) {
      debug = true;
      System.out.println("debug enabled");
    }

    final List<Thread> threads = new LinkedList<Thread>();
    for (int i = 0; i < MAX_WINDOWS; i++) {
      final GraphicsController controller = new GraphicsController(debug);
      threads.add(new Thread(controller));
    }

    for (int i = 0; i < MAX_WINDOWS; i++) {
      invokeLater(threads.get(i));
    }

  }

}
