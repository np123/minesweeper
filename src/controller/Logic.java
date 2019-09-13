package controller;

import java.util.ArrayList;
import java.util.LinkedList;

import model.Board;
import model.Node;

public class Logic {

  private Board board;
  private GraphicsController controller;

  void setBoard(Board b) {
    board = b;
  }

  void setControl(GraphicsController control) {
    controller = control;
  }

  void reveal(int source) {
    if (board.getNode(source).isMine()) {
      GraphicsController.freezeTime();
      for (GraphicsController control : GraphicsController.games) {
        for (int i = 0; i < controller.size(); i++) {
          control.grid.get(i).setEnabled(false);
        }
      }
      reveal();
      GraphicsController.showLoss();
      return;
    }
    if (board.getNode(source).score() == 0) reveal(bfs(board.getNode(source)), source);
    else controller.grid.get(source).setVisible(false);
  }

  void reveal() {
    for (GraphicsController control : GraphicsController.games) {
      for (int i = 0; i < control.size(); i++) {
        if (control.checkMine(i)) {
          control.grid.get(i).setVisible(false);
        }
      }
    }
  }

  private void reveal(Iterable<Node> clear, int source) {
    for (Node n : clear) {
      controller.grid.get(n.getPosition()).setVisible(false);
    }
  }

  private Iterable<Node> bfs(Node start) {

    // Queue to facilitate breadth-first search of grid
    final LinkedList<Node> visitedNodes = new LinkedList<Node>();

    // List of all visited Nodes
    final ArrayList<Node> pathTaken = new ArrayList<Node>();

    pathTaken.add(start);
    visitedNodes.addFirst(start);

    // Outer loop of breadth-first search - completes after path found or all vertices searched
    while (!visitedNodes.isEmpty()) {
      final Node adj = visitedNodes.removeFirst(); // Get the next Node to be visited

      // Go through list of adjacent Nodes and add them to the queue to be searched
      for (Node next : adj.getAdjacent()) {
        if (!controller.grid.get(next.getPosition()).isEnabled()) {
          continue; // Don't visit Nodes marked as mines
        }
        if (!pathTaken.contains(next)) { // Ignore previously visited Nodes
          if (next.score() == 0 && !next.isMine())
            visitedNodes.addLast(next);
          if (!next.isMine()) pathTaken.add(next);
        }
      }
    }
    return pathTaken;
  }
}
