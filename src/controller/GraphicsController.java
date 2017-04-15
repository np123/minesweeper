package controller;

import java.util.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

import java.awt.Font;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.UIManager;

import model.Node;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * The GraphicsController class communicates with the user and reacts to input
 * by calling appropriate methods in the model and view packages
 *
 */
public class GraphicsController extends MouseAdapter implements Runnable, WindowListener {

	private model.Board board;
	private view.UserInterface UI;
	private Logic engine;

	private static UpdateTimeTask counter;
	public static ArrayList<GraphicsController> games;

	public JTextPane clock;
	private static Timer time;
	private JButton newGame;

	private final int width;
	private final int height;
	private final int rows = 15;
	private final int cols = 15;
	private final int size = rows * cols;
	private static final int screenSize;

	public ArrayList<JButton> grid = new ArrayList<JButton>();	

	/*
	 * Statements in lossmsgs and winmsgs are taken from quotes from
	 * a computer game Stronghold Crusader by Firefly Studios that
	 * I found of particular interest
	 * I claim no credit for the words contained therein, nor am I
	 * affiliated with or claim endorsement from Firefly Studios 
	 */ 
	
	private static String[] lossmsgs = new String[]{"My plans worked perfectly. You are no match for me!", 
			"Lost again, have we?\nMaybe you are not up to the job, hm?",
			"Am I victorious? Oh, yes, I am! Hehehehehe!",
			"It's over my friend, let's face it. You've lost!",
			"This is too easy! Fight harder!",			
			"As it must be, another has fallen before me",
			"One more bites the dust, er... what! Hahaha",
			"Oh dear. Well, that wasn't very clever of you!",
			"Fiddlesticks! Lost again, dammit"
	};
	
	private static String[] winmsgs = new String[]{
			"Oh, just chalked up another one, hahahah!",
			"Maybe I have underestimated you a little?",
			"You are mighty in battle. But are you just as well?",
			"Good work, my Lord.\nWe are royally impressed at your accomplishment!",
			"The world has gone crazy! I-I-I I should have destroyed you!",		
	};
	
	
	static {

		// Sets window height and width based on device graphics settings
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		
		if (gd.getDisplayMode().getWidth() > gd.getDisplayMode().getHeight()) {
			screenSize = (int) gd.getDisplayMode().getHeight();
		} else {
			screenSize = (int) gd.getDisplayMode().getWidth();
		}

		games = new ArrayList<GraphicsController>();
		counter = new UpdateTimeTask();
		time = new Timer();
		time.scheduleAtFixedRate(counter, 1000, 1000);
	}

	/**
	 * Instantiates a GraphicsController and configures the window size based on
	 * the current displays' settings
	 */
	public GraphicsController() {

		width = (int) (screenSize / 25 * cols);
		height = (int) (screenSize / 25 * rows);

		board = new model.Board(width, height, rows, cols);
		
		engine = new Logic();
		engine.setControl(this);
		engine.setBoard(board);

		games.add(this);		
	}

	/**
	 * Initializes all the buttons for the mine grid
	 */
	private void initButtons() {
		newGame = new JButton();
		newGame.setBounds(width / 2 - 25, 0, 50, 50);
		try {
			BufferedImage im = ImageIO.read(new File("assets//smiley.png"));
			newGame.setIcon(new ImageIcon(im));
			newGame.addMouseListener(this);
			newGame.setName("new");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		UI.add(newGame, "new game");

		for (int y = 0; y < height; y += height / rows) {
			for (int x = 0; x < width; x += width / cols) {
				JButton sq = new JButton();
				sq.setBounds(x, y + 50, width / cols, height / rows);
				sq.setVisible(true);
				sq.addMouseListener(this);
				grid.add(sq);
				UI.add(sq, "mine");
			}
		}
	}

	/**
	 * Initializes the stopwatch timer that tracks time spent
	 */
	private void initTimer() {
		clock = new JTextPane();
		clock.setEditable(false);
		clock.setBackground(Color.BLACK);
		clock.setForeground(Color.WHITE);
		clock.setFont(new Font("TimesNewRoman", Font.BOLD, 20));
		clock.setBounds(3 * width / 4, 10, 40, 30);
		UI.add(clock, "Timer");
	}

	/**
	 * Initializes the content window
	 */
	private void initWindow() {
		// Creates new JFrame and sets state to visible
		JFrame window = new JFrame();
		window.setSize(width, height);
		window.setBackground(Color.DARK_GRAY);
		window.add(this.UI);
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


	public static void startTime(){
		time = new Timer();
		counter = new UpdateTimeTask();
		time.scheduleAtFixedRate(counter, 1000, 1000);
		for (GraphicsController control : games)
			control.clock.repaint();
	}	
	
	public static void freezeTime(){
		time.cancel();
		time.purge();
		time = null;
	}

	/**
	 * Sets the display time
	 * @param time	 
	 */
	public void setTime(String time) {
		clock.setText(time);
		clock.repaint();
	}

	/**
	 * Resets the game (board and timer)
	 */
	private static void reset() {
		for (GraphicsController control : games) {
			control.board.minesFound.clear();
			control.board.mines.clear();
			control.board = new model.Board(control.width, control.height, control.rows, control.cols);
			for (int i = 0; i < control.grid.size(); i++) {
				control.grid.get(i).setIcon(null);
				control.grid.get(i).setEnabled(true);
				control.grid.get(i).setVisible(true);				
			}
			control.engine.setBoard(control.board);
			control.UI.setBoard(control.board);
			control.clock.setText("300");
			control.clock.repaint();
			control.UI.repaint();
		}
		counter.reset();
		startTime();
	}

	/**
	 * Toggles the marking of a possible mine on the board
	 * 
	 * @param source the square that was clicked
	 */
	private void mark(int source) {
		if (!board.minesFound.contains(grid.get(source))) {
			grid.get(source).setEnabled(false);
			grid.get(source)
					.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Stop24.gif")));
			grid.get(source).updateUI();
			board.minesFound.add(grid.get(source));
		} else {
			board.minesFound.remove(grid.get(source));
			grid.get(source).setIcon(null);
			grid.get(source).setEnabled(true);
		}
	}

	public int size() {
		return size;
	}

	/**
	 * Determines if mouse was clicked while overtop a node Calls
	 * {@link controller.Logic#reveal(int)} to process event Triggers update of
	 * the user interface
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
			if (newGame.getName().equals(((JButton) e.getSource()).getName()))
				reset();
		} catch (ClassCastException ex) {
			return;
		}

		int position = grid.indexOf(e.getSource());
		if (position == -1)
			return;

		if (e.getButton() == MouseEvent.BUTTON1 && board.minesFound.indexOf(e.getSource()) == -1
				&& ((JButton) e.getSource()).isEnabled())
			engine.reveal(position);
		else if (e.getButton() == MouseEvent.BUTTON3)
			mark(position);
		UI.update();
	}

	static class UpdateTimeTask extends TimerTask {
		private final AtomicInteger start = new AtomicInteger(300);
		public AtomicInteger count = new AtomicInteger(300);

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
			for (GraphicsController control : games){
				control.setTime(time);
				if ((start.intValue() - count.intValue()) % 15 == 0) {
					control.addMine();
					control.UI.update();
				}
			}
			count.decrementAndGet();
		}

		public void reset() {
			count.set(start.intValue());
		}
	}

	@Override
	public void run() {
		UI = new view.UserInterface(board, screenSize, rows, cols);
		view.Layout layout = new view.Layout(width, height + 50);
		UI.setLayout(layout);
		initButtons();
		initTimer();
		initWindow();
	}
	
	public boolean checkMine(int i){
		return board.getNode(i).isMine();
	}
	
	private void addMine(){
		List<JButton> hidden = grid.stream().filter(b -> !board.mines.contains(grid.indexOf(b)) && b.isVisible()).collect(Collectors.toList());
		Random rand = new Random();
		int index = rand.nextInt(hidden.size());
		int sq = grid.indexOf(hidden.get(index));
		board.getNode(sq).setMine();
		board.mines.add(sq);
		
		// Update adjacent mine count
		for (Node pos: board.getNode(sq).getAdjacent()) pos.addAdj();
	}

	public static void showLoss(){
		int index = (new Random()).nextInt(lossmsgs.length);
		int action = JOptionPane.showConfirmDialog(
			    null,
			    lossmsgs[index] + "\n\nTry again?",
			    "",
			    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
			    null);
		if (action == JOptionPane.NO_OPTION){
			System.exit(0);
		} else if (action == JOptionPane.YES_OPTION){
			GraphicsController.reset();
		}
	}		
	
	public static void showWin(){
		int index = (new Random()).nextInt(winmsgs.length);
		int action = JOptionPane.showConfirmDialog(
			    null,
			    winmsgs[index] + "\n\nPlay again?",
			    "",
			    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (action == JOptionPane.NO_OPTION){
			System.exit(0);
		} else if (action == JOptionPane.YES_OPTION){
			GraphicsController.reset();
		}
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
		int action = JOptionPane.showConfirmDialog(
			    null,
			    "Are you sure you want to exit?",
			    "Confirm exit",
			    JOptionPane.YES_NO_OPTION);
		if (action == JOptionPane.YES_OPTION){
			System.exit(0);
		}
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}