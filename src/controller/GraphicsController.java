package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.Board;
import model.Node;
import view.Layout;
import view.UserInterface;

/**
 * The GraphicsController class communicates with the user and reacts to input by calling
 * appropriate methods in the model and view packages.
 */
public class GraphicsController extends MouseAdapter implements Runnable, WindowListener {

  private static AtomicInteger wins = new AtomicInteger(0);
  private static UpdateTimeTask counter;
  private static Timer time;
  private static final int SCREEN_SIZE;

  static ArrayList<GraphicsController> games;

  ArrayList<JButton> grid = new ArrayList<JButton>();

  private JButton newGame;
  private JTextPane clock;

  private final boolean debug;
  private final int width;
  private final int height;

  private final int rows = 15;
  private final int cols = 15;

  private Board board;
  private UserInterface userInterface;
  private Logic engine;

  /*
   * Statements in LOSS_MESSAGES and WIN_MESSAGES are taken from quotes from
   * a computer game Stronghold Crusader by Firefly Studios that
   * I found of particular interest
   * I claim no credit for the words contained therein, nor am I
   * affiliated with or claim endorsement from Firefly Studios
   */
  private static final String[] LOSS_MESSAGES =
      new String[] {
        "My plans worked perfectly. You are no match for me!",
        "Lost again, have we?\nMaybe you are not up to the job, hm?",
        "Am I victorious? Oh, yes, I am! Hehehehehe!",
        "It's over my friend, let's face it. You've lost!",
        "This is too easy! Fight harder!",
        "As it must be, another has fallen before me",
        "One more bites the dust, er... what! Hahaha",
        "Oh dear. Well, that wasn't very clever of you!",
        "Fiddlesticks! Lost again, dammit"
      };

  private static final String[] WIN_MESSAGES =
      new String[] {
        "Oh, just chalked up another one, hahahah!",
        "Maybe I have underestimated you a little?",
        "You are mighty in battle. But are you just as well?",
        "Good work, my Lord.\nWe are royally impressed at your accomplishment!",
        "The world has gone crazy! I-I-I I should have destroyed you!",
      };

  static {

    /* Set window height and width based on device graphics settings */
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final GraphicsDevice gd = ge.getDefaultScreenDevice();

    if (gd.getDisplayMode().getWidth() > gd.getDisplayMode().getHeight()) {
      SCREEN_SIZE = (int) gd.getDisplayMode().getHeight();
    } else {
      SCREEN_SIZE = (int) gd.getDisplayMode().getWidth();
    }

    games = new ArrayList<GraphicsController>();
    counter = new UpdateTimeTask();
    time = new Timer();
    time.scheduleAtFixedRate(counter, 1000, 1000);
  }

  /**
   * Instantiate a GraphicsController and configure the window size based on the current displays'
   * settings.
   */
  GraphicsController(boolean debug) {

    this.debug = debug;

    width = (int) (SCREEN_SIZE / 25 * cols);
    height = (int) (SCREEN_SIZE / 25 * rows);

    board = new Board(width, height, rows, cols);

    engine = new Logic();
    engine.setControl(this);
    engine.setBoard(board);

    games.add(this);
  }

  /** Initialize the buttons for the mine grid. */
  private void initButtons() {
    newGame = new JButton();
    newGame.setBounds(width / 2 - 25, 0, 50, 50);
    try {
      final BufferedImage im = ImageIO.read(new File("assets//smiley.png"));
      newGame.setIcon(new ImageIcon(im));
      newGame.addMouseListener(this);
      newGame.setName("new");
    } catch (IOException e) {
      e.printStackTrace();
    }
    userInterface.add(newGame, "new game");

    for (int y = 0; y < height; y += height / rows) {
      for (int x = 0; x < width; x += width / cols) {
        final JButton sq = new JButton();
        sq.setBounds(x, y + 50, width / cols, height / rows);
        sq.setVisible(true);
        sq.addMouseListener(this);
        grid.add(sq);
        userInterface.add(sq, "mine");
      }
    }
  }

  /** Initialize the stopwatch timer that tracks time spent. */
  private void initTimer() {
    clock = new JTextPane();
    clock.setEditable(false);
    clock.setBackground(Color.BLACK);
    clock.setForeground(Color.WHITE);
    clock.setFont(new Font("TimesNewRoman", Font.BOLD, 20));
    clock.setBounds(3 * width / 4, 10, 40, 30);
    userInterface.add(clock, "Timer");
  }

  /** Initialize the content window. */
  private void initWindow() {
    // Create new JFrame and set state to visible
    final JFrame window = new JFrame();
    window.setSize(width+20, height);
    window.setMinimumSize(new Dimension( +20, height));
    window.setBackground(Color.DARK_GRAY);
    window.add(this.userInterface);
    window.addWindowListener(this);
    window.setLocationRelativeTo(null);
    window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    window.setVisible(true);

    try {
      // Set cross-platform Java L&F
      UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
      SwingUtilities.updateComponentTreeUI(window);
      window.pack();
    } catch (Exception e) {
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  /** Start the game timer. */
  private static synchronized void startTime() {
    time = new Timer();
    counter = new UpdateTimeTask();
    time.scheduleAtFixedRate(counter, 1000, 1000);
    for (GraphicsController control : games) {
      control.clock.repaint();
    }
  }

  /** Pause the timer. */
  static void freezeTime() {
    time.cancel();
    time.purge();
    counter.cancel();
    time = null;
  }

  /**
   * Set the time on the display.
   *
   * @param time - current time
   */
  private void setTime(String time) {
    clock.setText(time);
    clock.repaint();
  }

  /** Reset the game (board and timer). */
  private static void reset() {
    for (GraphicsController control : games) {
      control.board.minesFound.clear();
      control.board.mines.clear();
      control.board = new Board(control.width, control.height, control.rows, control.cols);
      for (int i = 0; i < control.grid.size(); i++) {
        control.grid.get(i).setIcon(null);
        control.grid.get(i).setEnabled(true);
        control.grid.get(i).setVisible(true);
      }
      control.engine.setBoard(control.board);
      control.userInterface.setBoard(control.board);
      control.clock.setText("300");
      control.clock.repaint();
      control.userInterface.repaint();
    }
    counter.reset();
    startTime();
  }

  /**
   * Toggle the marking of a possible mine on the board.
   *
   * @param source the square that was clicked
   */
  private void mark(int source) {
    if (!board.minesFound.contains(grid.get(source))) {
      grid.get(source).setEnabled(false);
      grid.get(source)
          .setIcon(
              new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Stop24.gif")));
      grid.get(source).updateUI();
      board.minesFound.add(grid.get(source));
    } else {
      board.minesFound.remove(grid.get(source));
      grid.get(source).setIcon(null);
      grid.get(source).setEnabled(true);
    }
  }

  /** Return size of grid. */
  int size() {
    return rows * cols;
  }

  /**
   * Determine if mouse was clicked while over top a node
   *
   * <p>Calls {@link controller.Logic#reveal(int)} to process event Triggers update of the user
   * interface.
   *
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    /*
     * Map the x and y coordinate of the click to the node Ignore if there
     * is no node Otherwise process the move
     */

    try {
      if (newGame.getName().equals(((JButton) e.getSource()).getName())) {
        reset();
      }
    } catch (ClassCastException ex) {
      return;
    }

    final int position = grid.indexOf(e.getSource());
    if (position == -1) {
      return;
    }

    if (e.getButton() == MouseEvent.BUTTON1
        && board.minesFound.indexOf(e.getSource()) == -1
        && ((JButton) e.getSource()).isEnabled()) {
      engine.reveal(position);
    } else if (e.getButton() == MouseEvent.BUTTON3) {
      mark(position);
    }
    userInterface.update();
  }

  static class UpdateTimeTask extends TimerTask {
    private final AtomicInteger start = new AtomicInteger(300);
    final AtomicInteger count = new AtomicInteger(300);

    public void run() {
      String time = "";
      if (count.intValue() < 10) {
        time = "00" + count.toString();
      } else if (count.intValue() < 100) {
        time = "0" + count.toString();
      } else if (count.intValue() > 999) {
        time = "999";
      } else {
        time = count.toString();
      }
      for (GraphicsController control : games) {
        control.setTime(time);
        control.checkWin();
        if (count.intValue() < 300 && (start.intValue() - count.intValue()) % 15 == 0) {
          control.addMine();
          control.userInterface.update();
        }
        control.checkWin();
      }
      count.decrementAndGet();
    }

    synchronized void reset() {
      count.set(start.intValue());
    }
  }

  @Override
  public void run() {
    userInterface = new view.UserInterface(board, SCREEN_SIZE, rows, cols);
    final Layout layout = new Layout(width, height + 50);
    userInterface.setLayout(layout);
    if (!debug) {
      initButtons();
      initTimer();
      initWindow();
    } else {
      userInterface.writeScore();
      System.exit(0);
    }
  }

  boolean checkMine(int i) {
    return board.getNode(i).isMine();
  }

  private void checkWin() {
    final List<JButton> rem =
        grid.stream()
            .filter(b -> !board.minesFound.contains(grid.get(grid.indexOf(b))) && b.isVisible())
            .collect(Collectors.toList());

    if (rem.size() == board.numMines() - board.minesFound.size()) {
      for (JButton b : board.minesFound) {
        if (!board.mines.contains(grid.indexOf(b))) {
          return;
        }
      }
      System.out.println(board.numMines());
      System.out.println(board.minesFound.size());
      GraphicsController.wins.incrementAndGet();
    }
    if (GraphicsController.wins.intValue() == GraphicsController.games.size()) {
      GraphicsController.wins.set(0);
      GraphicsController.freezeTime();

      for (GraphicsController control : GraphicsController.games) {
        control.engine.reveal();
      }
      GraphicsController.showWin();
    }
  }

  private void addMine() {
    final List<JButton> hidden =
        grid.stream()
            .filter(b -> !board.mines.contains(grid.indexOf(b)) && b.isVisible())
            .collect(Collectors.toList());
    final Random rand = new Random();
    final int index = rand.nextInt(hidden.size());
    final int sq = grid.indexOf(hidden.get(index));
    board.getNode(sq).setMine();
    board.mines.add(sq);
    board.addMine();

    // Update adjacent mine count
    for (Node pos : board.getNode(sq).getAdjacent()) {
      pos.addAdj();
    }
  }

  static void showLoss() {
    final int index = (new Random()).nextInt(LOSS_MESSAGES.length);
    final int action =
        JOptionPane.showConfirmDialog(
            null,
            LOSS_MESSAGES[index] + "\n\nTry again?",
            "You've lost!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null);
    if (action == JOptionPane.NO_OPTION) {
      System.exit(0);
    } else if (action == JOptionPane.YES_OPTION) {
      GraphicsController.reset();
    }
  }

  private static void showWin() {
    final int index = (new Random()).nextInt(WIN_MESSAGES.length);
    final int action =
        JOptionPane.showConfirmDialog(
            null,
            WIN_MESSAGES[index] + "\n\nPlay again?",
            "You win!!!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null);
    if (action == JOptionPane.NO_OPTION) {
      System.exit(0);
    } else if (action == JOptionPane.YES_OPTION) {
      GraphicsController.reset();
    }
  }

  public void setDebug() {
    userInterface.debug = true;
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    final int action =
        JOptionPane.showConfirmDialog(
            null, "Are you sure you want to exit?", "Confirm exit", JOptionPane.YES_NO_OPTION);
    if (action == JOptionPane.YES_OPTION) {
      System.exit(0);
    }
  }

  @Override
  public void windowDeactivated(WindowEvent arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowDeiconified(WindowEvent arg0) {
  }

  @Override
  public void windowIconified(WindowEvent arg0) {
  }

  @Override
  public void windowOpened(WindowEvent arg0) {
  }
}
