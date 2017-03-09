package controller;

import java.util.Timer;
import java.util.ArrayList;
import java.util.TimerTask;

import java.io.File;
import java.io.IOException;

import java.awt.Font;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;


/**
 * The GraphicsController class communicates with the user and
 * reacts to input by calling appropriate methods in the
 * model and view packages
 *
 */
public class GraphicsController extends MouseAdapter{	

	public static ArrayList<JButton> grid = new ArrayList<JButton>();
	public static ArrayList<JButton> mine = new ArrayList<JButton>();	
	private view.UserInterface UI;	
	public static JTextPane clock;
	private static Timer time;
	private JButton newGame;

	private final int width;
	private final int height;
	private final int rows = 15;
	private final int cols = 15;


	/**
	 * Instantiates a GraphicsController and configures the window size
	 * based on the current displays' settings
	 */
	public GraphicsController(){		

		//Sets window height and width based on device graphics settings
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		
		int size = 0;		
		if (gd.getDisplayMode().getWidth() > gd.getDisplayMode().getHeight()){
			size = (int) gd.getDisplayMode().getHeight();
		} else {
			size = (int) gd.getDisplayMode().getWidth();
		}

		width = (int) (size / 25 * cols);
		height = (int) (size / 25 * rows);


		new model.Board(width,height, rows, cols);

		UI = new view.UserInterface(size, rows, cols);
		view.Layout layout = new view.Layout(width, height+50);
		UI.setLayout(layout);

		initButtons();	
		initTimer();
		initWindow();

		time = new Timer();					
		time.scheduleAtFixedRate(new UpdateTimeTask(), 1000, 1000);		
	}

	/**
	 * Initializes all the buttons for the mine grid
	 */
	private void initButtons(){
		newGame = new JButton();
		newGame.setBounds(width/2-25, 0, 50, 50);
		try {
			BufferedImage im = ImageIO.read(new File("assets//smiley.png"));
			newGame.setIcon(new ImageIcon(im));
			newGame.addMouseListener(this);
			newGame.setName("new");
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		UI.add(newGame, "new game");

		for (int y = 0; y < height; y += height/rows){
			for (int x = 0; x < width; x += width/cols){
				JButton sq = new JButton();				
				sq.setBounds(x,y+50,width/cols,height/rows);
				sq.setVisible(true);	
				sq.addMouseListener(this);				
				grid.add(sq);
				UI.add(sq,"mine");				
			}
		}
	}

	/**
	 * Initializes the stopwatch timer that tracks time spent
	 */
	private void initTimer(){
		clock = new JTextPane(); 
		clock.setEditable(false);
		clock.setBackground(Color.BLACK);
		clock.setForeground(Color.WHITE);
		clock.setFont(new Font("TimesNewRoman", Font.BOLD, 20));		
		clock.setBounds(3*width/4, 10, 40, 30);
		UI.add(clock, "Timer");
	}

	/**
	 * Initializes the content window
	 */
	private void initWindow(){
		//Creates new JFrame and sets state to visible
		JFrame window = new JFrame();
		window.setSize(width, height);
		window.setBackground(Color.DARK_GRAY);
		window.add(this.UI);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

		try {
			// Set cross-platform Java L&F
			UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");			
			SwingUtilities.updateComponentTreeUI(window);			
			window.pack();			
		} catch (Exception e){
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e1) {				
				e1.printStackTrace();
			}			
		}
	}	

	
	public static void startTime(){
		time = new Timer();
		time.scheduleAtFixedRate(new UpdateTimeTask(), 1000, 1000);
		clock.repaint();
	}
	
	
	public static void freezeTime(){
		time.cancel();
		time.purge();
		time = null;
	}
	
	/**
	 * @param time sets the display time
	 */
	public static void setTime(String time){
		clock.setText(time);
		clock.repaint();
	}
	
	
	private void reset(){
		new model.Board(width, height, rows, cols);
		for (int i = 0; i < grid.size(); i++) {
			grid.get(i).setIcon(null);
			grid.get(i).setEnabled(true);
			grid.get(i).setVisible(true);
			mine.clear();
		}		
		UpdateTimeTask.reset();
		clock.setText("000");
		clock.repaint();
		startTime();
	}

	/**
	 * Toggles the marking of a possible mine on the board
	 * @param source the square that was clicked
	 */
	private void mark(int source){				
		if (!mine.contains(grid.get(source))){
			grid.get(source).setEnabled(false);
			grid.get(source).setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Stop24.gif")));
			grid.get(source).updateUI();
			mine.add(grid.get(source));	
		} else {			
			mine.remove(grid.get(source));
			grid.get(source).setIcon(null);
			grid.get(source).setEnabled(true);
		}		
	}
	
	/**
	 * Determines if mouse was clicked while overtop a node
	 * Calls {@link controller.Logic#reveal(int)} to process event
	 * Triggers update of the user interface
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		/* 
		 * Map the x and y coordinate of the click to the node
		 * Ignore if there is no node
		 * Otherwise process the move 
		 */

		try {
			if (newGame.getName().equals(((JButton)e.getSource()).getName())) reset();			
		} catch (ClassCastException ex){
			return;
		}

		int position = grid.indexOf(e.getSource());
		if (position == -1) return;

		if (e.getButton() == MouseEvent.BUTTON1 && mine.indexOf(e.getSource()) == -1 && ((JButton)e.getSource()).isEnabled()) Logic.reveal(position);
		else if (e.getButton() == MouseEvent.BUTTON3) mark(position);
		UI.update();		
	}	
}


class UpdateTimeTask extends TimerTask{
	public static Integer count = 0;

	public void run(){		
		String time = "";
		if (count < 10){
			time = "00" + count.toString();
		} else if (count < 100){
			time = "0" + count.toString();
		} else if (count > 999){
			time = "999";
		} else {
			time = count.toString();
		}
		GraphicsController.setTime(time);
		count++;
	}
	
	public static void reset(){
		count = 0;
	}	
}