package model;

import java.util.ArrayList;

public class Node {
	
	private final int position;
	private int score = 0;
	private boolean mine = false;
	
	private ArrayList<Node> connections = new ArrayList<Node>();
	
	public Node(int position){
		this.position = position;
	}
	
	public boolean isMine(){
		return mine;
	}
	
	public void setMine(){
		mine = true;
		score++;
	}
	
	public int score(){
		return score;
	}
	
	public void addAdj(){
		score++;
	}
	
	public void addConnection(Node pos){
		connections.add(pos);
		if (pos.isMine()) score++;
	}
	
	public static boolean isConnected(Node first, Node second){
						
		if (first.connections.contains(second)) return true;
		return false;
	}
	
	public int getPosition(){
		return position;
	}
	
	public Iterable<Node> getAdjacent(){
		return this.connections;
	}
	
	@Override
	public boolean equals(Object other){
		Node second = (Node) other;
		return this.getPosition() == second.getPosition();
	}
}
