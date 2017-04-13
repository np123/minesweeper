package controller;

import java.util.ArrayList;
import java.util.LinkedList;

import model.Board;
import model.Node;

public class Logic {
		
	private Board board;
	private GraphicsController controller;
	
	public void setBoard(Board b){
		board = b;
	}
	
	public void setControl(GraphicsController control){
		controller = control;
	}
	
	public void addMine(){
		ArrayList<Integer> hidden = new ArrayList<Integer>();
		for (int i = 0; i < controller.size(); i++){
		    if (controller.grid.get(i).isEnabled() && board.getNode(i).isMine()){
		        hidden.add(i);
		    }
		}
	}
	
	public void reveal(int source){			
		if (board.getNode(source).isMine()) {			
			GraphicsController.freezeTime();
			for (GraphicsController control : GraphicsController.games){
				for (int i = 0; i < controller.size(); i++){
					control.grid.get(i).setEnabled(false);
				}
			}
			reveal();
			return;
		}
		if (board.getNode(source).score() == 0) reveal(bfs(board.getNode(source)), source);		
		else controller.grid.get(source).setVisible(false);
	}
	
	private void reveal(){
		for (int i = 0; i < controller.size(); i++){
			if (board.getNode(i).isMine()){
				for (GraphicsController control : GraphicsController.games){
					control.grid.get(i).setVisible(false);
				}
			}
		}
	}
	
	private void reveal(Iterable<Node> clear, int source){		
		for (Node n : clear){				
			controller.grid.get(n.getPosition()).setVisible(false);			
		}
	}
	
	private Iterable<Node> bfs(Node start){

		LinkedList<Node> visitedNodes = new LinkedList<Node>();					//Used as a queue to facilitate the breadth-first search 		
		ArrayList<Node> pathTaken = new ArrayList<Node>();						//Contains a list of all visited Nodes

		pathTaken.add(start);		
		visitedNodes.addFirst(start);		

		while (!visitedNodes.isEmpty()){														//Outer loop of breadth-first search - completes after path found or all vertices searched						
			Node adj = visitedNodes.removeFirst();												//Get the next Node to be visited
			for (Node next : adj.getAdjacent()){								//Go through list of adjacent Nodes and add them to the queue to be searched
				if (!controller.grid.get(next.getPosition()).isEnabled()) continue;		//Don't visit Nodes marked as mines
				if (!pathTaken.contains(next)) {												//Ignore previously visited Nodes					
					if (next.score() == 0 && !next.isMine()) visitedNodes.addLast(next);		//Explore all of the adjacent Nodes specified in the for loop before continuing to the next 'level'					
					if (!next.isMine()) pathTaken.add(next);
				}
			}			
		}
		return pathTaken;
	}
}