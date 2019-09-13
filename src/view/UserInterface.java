package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;

import javax.swing.JPanel;

import model.Board;

public class UserInterface extends JPanel {

  public boolean guessMade = false;
  private Board board;
  public boolean debug = false;

  private final int windowHeight;
  private final int windowWidth;

  private final int boardWidth;
  private final int boardHeight;

  private final int startWidth;
  private final int startHeight;

  private final int rows;
  private final int cols;

  /** Defines the visual layout of the game. */
  public UserInterface(Board board, int size, int row, int col) {

    this.board = board;
    rows = row;
    cols = col;

    windowWidth = (int) (size / 25 * cols);
    windowHeight = (int) (size / 25 * rows);

    boardWidth = 4 * windowWidth / 5;
    boardHeight = windowHeight;

    startWidth = 0;
    startHeight = 50;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public void update() {
    super.repaint();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawBackground(g);
    drawBoard(g);
    drawGrid(g);
    drawState(g);
  }

  private void drawBackground(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillRect(0, startHeight, windowWidth, windowHeight);
  }

  private void drawBoard(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillRect(startWidth, startHeight, boardWidth, boardHeight);
  }

  private void drawState(Graphics g) {
    final Integer rem = board.mines.size() - board.minesFound.size();
    g.setColor(Color.BLACK);
    g.setFont(new Font("TimesNewRoman", Font.BOLD, 15));
    g.drawString("Mines Remaining: " + rem.toString(), 10, 30);
  }

  private void drawGrid(Graphics g) {
    g.setColor(Color.GRAY);
    int count = 0;
    for (int y = startHeight; y < windowHeight + startHeight; y += windowHeight / rows) {
      for (int x = 0; x < windowWidth; x += windowWidth / cols) {
        g.setColor(Color.GRAY.brighter());
        g.fillRect(x, y, windowWidth / cols, windowHeight / rows);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, windowWidth / cols, windowHeight / rows);

        final int score = board.getNode(count).score();

        switch (score) {
          case 1:
            g.setColor(Color.BLUE);
            break;
          case 2:
            g.setColor(Color.GREEN.darker());
            break;
          case 3:
            g.setColor(Color.RED);
            break;
          case 4:
            g.setColor(Color.PINK.darker().darker().darker());
            break;
          case 5:
            g.setColor(Color.MAGENTA.darker().darker());
            break;
          default:
            break;
        }
        final Font f = new Font("TimesRoman", Font.BOLD, 15);
        g.setFont(f);
        if (score > 0 && !board.getNode(count).isMine()) {
          g.drawString(
              score + "",
              x + (int) (windowWidth / (2.5 * cols)),
              y + (int) (windowHeight / (1.5 * rows)));
        }
        g.setColor(Color.BLACK);
        if (board.getNode(count).isMine()) {
          g.drawString(
              "X", x + (int) (windowWidth / (3 * cols)), y + (int) (windowHeight / (1.5 * rows)));
        }
        count++;
      }
    }
    if (debug == true) {
      writeScore();
    }
  }

  /** Write score to text file. */
  public void writeScore() {

    try {
      String fileName = "score.txt";
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));

      for (int i = 0; i < board.rows(); i++) {
        for (int j = 0; j < board.cols(); j++) {
          if (board.getNode(i * cols + j).isMine()) {
            bufferedWriter.write("X");
          } else {
            bufferedWriter.write(String.valueOf(board.getNode(i * cols + j).score()));
          }
          if (j < board.cols() - 1) {
            bufferedWriter.write(',');
          }
        }
        bufferedWriter.newLine();
      }
      bufferedWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
