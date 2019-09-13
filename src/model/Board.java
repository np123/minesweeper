package model;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;

/**
 * The Board class contains the representation of the game board and also the logic for processing
 * moves.
 */
public class Board {

  // Grid for the game board
  private Node[] nodes;

  public ArrayList<JButton> minesFound = new ArrayList<JButton>();
  public ArrayList<Integer> mines;
  private int numMines;
  private int size;
  private int rows;
  private int cols;

  /**
   * Initializes a board using the specified dimension 1/8 of the board is allocated as mines.
   *
   * @param width - board width
   * @param height - board height
   * @param rows - number of rows
   * @param cols - number of columns
   */
  public Board(int width, int height, int rows, int cols) {

    mines = new ArrayList<Integer>();
    nodes = new Node[rows * cols];
    numMines = (rows * cols) / 8;
    this.rows = rows;
    this.cols = cols;

    for (int pos = 0; pos < rows * cols; pos++) {
      nodes[pos] = new Node(pos);
    }

    // Randomly generates mine locations until 1/8 of the board is mines
    while (mines.isEmpty() || mines.size() < numMines) {
      final int pos = new Random().nextInt(rows * cols);
      if (!mines.contains(pos)) {
        mines.add(pos);
        nodes[pos].setMine();
      }
    }
    setup(rows, cols);
  }

  public int rows() {
    return rows;
  }

  public int cols() {
    return cols;
  }

  public int size() {
    return rows * cols;
  }

  /**
   * Defines the board as a directed graph and adds edges.
   *
   * @param rows - number of rows
   * @param cols - number of columns
   */
  @SuppressWarnings("ControlFlowStatementWithoutBraces")
  private void setup(int rows, int cols) {
    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < cols; x++) {
        final int pos = y * cols + x;
        if (x < cols - 1) nodes[pos].addConnection(nodes[pos + 1]);
        if (x > 0) nodes[pos].addConnection(nodes[pos - 1]);
        if (y > 0) nodes[pos].addConnection(nodes[pos - cols]);
        if (y < rows - 1) nodes[pos].addConnection(nodes[pos + cols]);
        if (x < cols - 1 && y < rows - 1)
          nodes[pos].addConnection(nodes[pos + cols + 1]); // Down & right
        if (x < rows - 1 && y > 0) nodes[pos].addConnection(nodes[pos - cols + 1]); // Up & right
        if (x > 0 && y < cols - 1) nodes[pos].addConnection(nodes[pos + cols - 1]); // Down & left
        if (x > 0 && y > 0) nodes[pos].addConnection(nodes[pos - cols - 1]); // Up & left
      }
    }
  }

  /**
   * Accessor method for the nodes on the board.
   *
   * @param x - index of node
   * @return corresponding node
   */
  public Node getNode(int x) {
    return nodes[x];
  }

  public int numMines() {
    return numMines;
  }

  public void addMine() {
    numMines++;
  }
}
