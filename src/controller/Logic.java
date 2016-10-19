package controller;

import java.util.ArrayList;
import java.util.LinkedList;

import model.Board;
import model.Node;

public class Logic {
	
	public static void reveal(int source){		
		if (Board.getNode(source).isMine()) {			
			GraphicsController.freezeTime();
			for (int i = 0; i < GraphicsController.grid.size(); i++){
				GraphicsController.grid.get(i).setEnabled(false);
			}
			reveal();
			return;
		}
		if (Board.getNode(source).score() == 0) reveal(bfs(Board.getNode(source)), source);		
		else GraphicsController.grid.get(source).setVisible(false);
	}
	
	private static void reveal(){
		for (int i = 0; i < GraphicsController.grid.size(); i++){
			if (Board.getNode(i).isMine()) GraphicsController.grid.get(i).setVisible(false);
		}
	}
	
	private static void reveal(Iterable<Node> clear, int source){		
		for (Node n : clear){				
			GraphicsController.grid.get(n.getPosition()).setVisible(false);			
		}
	}
	
	private static Iterable<Node> bfs(Node start){

		LinkedList<Node> visitedNodes = new LinkedList<Node>();					//Used as a queue to facilitate the breadth-first search 		
		ArrayList<Node> pathTaken = new ArrayList<Node>();						//Contains a list of all visited Nodes

		pathTaken.add(start);		
		visitedNodes.addFirst(start);		

		while (!visitedNodes.isEmpty()){														//Outer loop of breadth-first search - completes after path found or all vertices searched						
			Node adj = visitedNodes.removeFirst();												//Get the next Node to be visited
			for (Node next : adj.getAdjacent()){								//Go through list of adjacent Nodes and add them to the queue to be searched
				if (!GraphicsController.grid.get(next.getPosition()).isEnabled()) continue;		//Don't visit Nodes marked as mines
				if (!pathTaken.contains(next)) {												//Ignore previously visited Nodes					
					if (next.score() == 0 && !next.isMine()) visitedNodes.addLast(next);		//Explore all of the adjacent Nodes specified in the for loop before continuing to the next 'level'					
					if (!next.isMine()) pathTaken.add(next);
				}
			}			
		}
		return pathTaken;
	}
}