package astar.ai.mdh.se;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node  implements Comparable<Node> {
	
	// Iteration step in which the node is reached
	private int step;
	// Node parent
	private Node parent;
	// Node list of children
	private List<Node> children;
	//Element that moved
	private int id;
	// Direction in which it moved on the x axis 
	private int direction;
	// Value/estimate to the goal position based on the Manhattan distance heuristics
	private int heuristicVal;  
	// The sum of the heuristic value and the path cost
	private int finalCost; 
	// State of the puzzle 
	private int [] puzzleState;
	
	/*
	 *  Constructor, initialization
	 */
	public Node(int step, int id, int x, int [] puzzleState, Node parent){
		// Initialization 
		this.children= new ArrayList<Node>();
		this.puzzleState= new int [9];
		
		this.step=step;
		this.id=id;
		this.direction=x;
		this.puzzleState= new int[9];
		for(int i=0;i<9;i++){
			this.puzzleState[i]=puzzleState[i];
		}
		this.parent=parent;
	}
	
	public Node(int step, int [] puzzleState){
		// Initialization 
		this.children= new ArrayList<Node>();
		this.puzzleState= new int [9];
		
		this.puzzleState=puzzleState;
		this.parent=null;
	}

	/*
	 *  Public get/set methods 
	 */
	
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getHeuristicVal() {
		return heuristicVal;
	}
	public void setHeuristicVal(int heuristicVal) {
		this.heuristicVal = heuristicVal;
		// The final cost is the number of steps to get to the node plus the heuristic value
		this.finalCost=heuristicVal+step;
	}
	public int getFinalCost() {
		return finalCost;
	}
	public void setFinalCost(int finalCost) {
		this.finalCost = finalCost;
	}
	public int[] getPuzzleState() {
		return puzzleState;
	}
	public void setPuzzleState(int[] puzzleState) {
		this.puzzleState = puzzleState;
	}
	
	/*
	*	Method to add a child node in the list
	*/
	
	public void addChildren(Node child){
		this.children.add(child);
	}

	@Override
	public int compareTo(Node arg0) {
		return this.finalCost-arg0.finalCost;
	}

	@Override
	public String toString() {
		String s="[";
		for(int i=0;i<puzzleState.length;i++){
			s+=puzzleState[i];
			if(i<puzzleState.length-1)
			s+=",";
		}
		s+="]";
		return "Node [step=" + step +", id=" + id + ", direction=" + direction
				+ ", heuristicVal=" + heuristicVal + ", finalCost=" + finalCost+", puzzleState="+s+"]";
	}

	/*
	 * Comparison of nodes is done using only the puzzleState attribute since there can be no repetition of 
	 * states in the open list and the closed list. HashSet is used for them, which in turn used the 
	 * hashcode and the equals method for comparison
	 */
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(puzzleState);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (!Arrays.equals(puzzleState, other.puzzleState))
			return false;
		return true;
	}
	
	
}
