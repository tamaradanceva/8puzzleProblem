package astar.ai.mdh.se;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class EightPuzzleSolver {

	// Size of the puzzle
	private int size =3;
	
	/*
	 * Edge-adjacency like matrix to determine neighbors, 
	 * Dimensions: [possible positions=size^2] x [4={left, right, top, down}]  
	 * ex. if [4,3]=1 : position 4 has neighbor on top
	 * ex. if [7,1]=0 : position 7 does not have neighbor on the left
	*/
	private int [][] neighbors={{0,1,0,1},
								{1,1,0,1},
								{1,0,0,1},
								{0,1,1,1},
								{1,1,1,1},
								{1,0,1,1},
								{0,1,1,0},
								{1,1,1,0},
								{1,0,1,0}};
	
	/*
	 * Matrix containing the estimates to reach the goal using the Manhattan method 
	 * For simplification a floor value is used 
	 * Dimensions: [size] x [size]
	 */
	private int [][] manhattanEstimate;
	
	// open list nodes- candidates to inspect
	private HashSet<Node> openList;
	
	// closed list nodes - HashSet of expanded nodes 
	private HashSet<Node> closedList;
	
	// Initial state of the puzzle, 0 is blank space, 1-8 puzzle pieces
	private int [] initialState={1,3,2,4,6,5,7,8,0};
	//private int [] initialState={7,8,2,6,1,4,5,3,0};
	
	// Current state of the puzzle
	private int [] currentState;
	
	// Goal state 0,1,2,3,4,5,6,7,8
	private int [] goalState= {0,1,2,3,4,5,6,7,8};
	
	// Number of steps
	private int numOfSteps=0;
	
	//Indicates the solution has been found 
	private boolean end=false;
	
	//Indicates that there are no more nodes in the open list
	private boolean noNodes=false;
	
	
	/*
	 * Constructor , initialization
	 */
	public EightPuzzleSolver(){
		
		this.openList= new HashSet<Node>();
		this.closedList= new HashSet<Node>();
		this.manhattanEstimate=this.calculateManhattan();
		
	//	this.initialState= new int [size*size];
		
		boolean solvable=false;
		
		// Check if the initial state is solvale, generate a new state until it is
		while(solvable==false){
		// Create a random sequence that is used as an initial state
		ArrayList<Integer> s= new ArrayList<Integer>();
		for(int i=0;i<size*size;i++){
			s.add(i);
		}
		
		Collections.shuffle(s);
		
		
		for(int i=0;i<size*size;i++){
			this.initialState[i]=s.get(i);
		}
		int res=calculateNumberOfInversions(initialState);
		
		// If the number of inversion is even, it is solvable (only for odd puzzle sizes)
		if(res%2==0){
			solvable=true;
		}
		}
	}
	
	public void printInitialState(){
		System.out.print("Initial state: ");
		for(int i=0;i<size*size;i++){
			System.out.print(this.initialState[i]+" ");
		}
		System.out.print("\n");
	}
	
	public void solve(){
		this.currentState=this.initialState;
		
		//Create root node and add it to the open list
		int [] rps= new int[size*size];
		for(int i=0;i<size*size;i++){
			rps[i]=initialState[i];
		}
		Node n=new Node(0,rps);
		openList.add(n);
		
		//int debugC=0;
		//while(debugC<2){
		while(end==false){
			//debugC++;
			//System.out.println("SIZEEE:"+openList.size());
			Node node=Collections.min(openList);
			int [] ps=node.getPuzzleState();
			for(int i=0;i<currentState.length;i++){
				currentState[i]=ps[i];
			}
			// Handling the sate when all options have been explored and the solution has not been found
			// Uncomment the check if the puzzle is solvable
			if(node==null){
				end=true;
				noNodes=true;
			}
		//	System.out.println("IN WHILE");
			generatePossibleSteps(node);
			
		}
		if(noNodes==true){
			System.out.println("A solution has not been found for the generated initial state.");
		}
		
		
	}
	
	public void generatePossibleSteps(Node n){
		
		//System.out.println("expanded node:"+n.toString());
		
		// Increment number of steps
		this.numOfSteps=n.getStep()+1;
		//System.out.println("Num of steps:"+numOfSteps);
		
		//Find the index of the blank space
		int indexBlankSpace=0;
		for(int i=0;i<size*size;i++){
			if(currentState[i]==0){
				indexBlankSpace=i;
				break;
			}
		}
		//System.out.println("Index blank space:"+indexBlankSpace);
		
		int [] puzzleState= new int [size*size];
		
		
		//Possible steps , replace the blank space with some of its neighbors
		
		if(neighbors[indexBlankSpace][0]==1){
			//The node has a left neighbor
			//System.out.println("Has left neighbor");
			
			
			for(int i=0;i<size*size;i++){
				puzzleState[i]=currentState[i];
			}
			
			// Switch corresponding elements (blank space with left neighbor) to get the new state from the parent state 
			puzzleState[indexBlankSpace]=puzzleState[indexBlankSpace-1];
			puzzleState[indexBlankSpace-1]=0;
			
			//Check if the new puzzle state has already been added to the open list
			if(checkLoop(puzzleState,n)==false){
			//System.out.println("Checkloop is false for left neighbor");
			
			// If it is not repeated created the new node, calculate the heuristic value and add it to the open list
			Node n1= new Node(numOfSteps,indexBlankSpace,-1,puzzleState,n);
			n1.setHeuristicVal(getHeuristics(puzzleState));
			//System.out.println("Node 1 left neighbor"+n1.toString());
			n.addChildren(n1);
			
			// Check if it new state is the goal state, if yes modify end and trace the path back to the initial state by using the parent attribute
			if(checkEnd(puzzleState)==true){
				//System.out.println("End !!!!!!!!!!!!!!");
				end=true;
				tracePath(n1);
			}
			}
			
		}
		if(neighbors[indexBlankSpace][1]==1){
			
			//The node has a right neighbor
			//System.out.println("Has right neighbor");
			
			 puzzleState= new int [size*size];
			for(int i=0;i<size*size;i++){
				puzzleState[i]=currentState[i];
			}
			
			// Switch corresponding elements (blank space with right neighbor) to get the new state from the parent state 
			puzzleState[indexBlankSpace]=puzzleState[indexBlankSpace+1];
			puzzleState[indexBlankSpace+1]=0;
			
			//Check if the new puzzle state has already been added to the open list
			if(checkLoop(puzzleState,n)==false){
			//System.out.println("Checkloop is false for right neighbor");
				
			// If it is not repeated created the new node, calculate the heuristic value and add it to the open list
			Node n2= new Node(numOfSteps,indexBlankSpace,+1,puzzleState,n);
			n2.setHeuristicVal(getHeuristics(puzzleState));
			//System.out.println("Node 2 right neighbor"+n2.toString());
			n.addChildren(n2);
			
			// Check if it new state is the goal state, if yes modify end and trace the path back to the initial state by using the parent attribute
			if(checkEnd(puzzleState)==true){
				//System.out.println("End !!!!!!!!!!!!!!");
				end=true;
				tracePath(n2);
			}
			}
		}
		if(neighbors[indexBlankSpace][2]==1){
			//The node has a top neighbor
			//System.out.println("Has top neighbor");
			
			puzzleState= new int [size*size];
			for(int i=0;i<size*size;i++){
				puzzleState[i]=currentState[i];
			}
			
			// Switch corresponding elements (blank space with top neighbor) to get the new state from the parent state 
			puzzleState[indexBlankSpace]=puzzleState[indexBlankSpace-size];
			puzzleState[indexBlankSpace-size]=0;
			
			//Check if the new puzzle state has already been added to the open list
			if(checkLoop(puzzleState,n)==false){
			// If it is not repeated created the new node, calculate the heuristic value and add it to the open list
			
			//System.out.println("Checkloop is false for top neighbor");
			Node n3= new Node(numOfSteps,indexBlankSpace,-size,puzzleState,n);
			n3.setHeuristicVal(getHeuristics(puzzleState));
			//System.out.println("Node 3 top neighbor"+n3.toString());
			n.addChildren(n3);
			
			// Check if it new state is the goal state, if yes modify end and trace the path back to the initial state by using the parent attribute
			if(checkEnd(puzzleState)==true){
				//System.out.println("End !!!!!!!!!!!!!!");
				end=true;
				tracePath(n3);
			}
			}
		}
		if(neighbors[indexBlankSpace][3]==1){
			//The node has a down neighbor
			//System.out.println("Has down neighbor");
			
			puzzleState= new int [size*size];
			for(int i=0;i<size*size;i++){
				puzzleState[i]=currentState[i];
			}
			// Switch corresponding elements (blank space with down neighbor) to get the new state from the parent state 
			puzzleState[indexBlankSpace]=puzzleState[indexBlankSpace+size];
			puzzleState[indexBlankSpace+size]=0;
			
			//Check if the new puzzle state has already been added to the open list
			if(checkLoop(puzzleState,n)==false){
			// If it is not repeated created the new node, calculate the heuristic value and add it to the open list
				
			//System.out.println("Checkloop is false for down neighbor");
			Node n4= new Node(numOfSteps,indexBlankSpace,+size,puzzleState,n);
			n4.setHeuristicVal(getHeuristics(puzzleState));
			//System.out.println("Node 4 down neighbor"+n4.toString());
			n.addChildren(n4);
			
			// Check if it new state is the goal state, if yes modify end and trace the path back to the initial state by using the parent attribute
			if(checkEnd(puzzleState)==true){
				//System.out.println("End !!!!!!!!!!!!!!");
				end=true;
				tracePath(n4);
			}
			}
		}
		
		this.openList.remove(n);
		this.closedList.add(n);
		
		
		// Inserts the new nodes in the hash set
		for (Node chNode: n.getChildren()){
			openList.add(chNode);
		}	
	
	}
	
	/*
	 *  The manhattan distance is equal to the sum of the distances between each of the nodes' position and their goal position
	 * This values for each node are retrieved from the matrix containing the estimates created during the initialization 
	 * An estimate how far is the state passed as an argument to from the goal state
	*/
	public int getHeuristics(int [] state){
		int manhattanH=0;
	
		for(int i=0;i<size*size;i++){
			manhattanH+=this.manhattanEstimate[state[i]][i];
		}
		
		return manhattanH;
	}
	
	/*
	 * Check if the state passed as an argument to the method has already been added in the open list or the closed list
	 * Since the hash and the equal method in Node only consider the state when comparing, we can easily 
	 * check if a state is repeat by using the contains method.
	 * The first check is added to optimize the performance. I have observed that in every iteration one possible state is 
	 * the same as that parent of that node's parent state. So it is highly likely that this check will yield true
	 * comparing to the other checks (the open list and the closed list)
	 */
	public boolean checkLoop(int [] state, Node parent){
		//System.out.println("CheckLoop for state:"+Arrays.toString(state)+", parent array:"+Arrays.toString(parent.getPuzzleState()));
		boolean f=true;
		boolean found=true;
		if (numOfSteps>=2){
		Node node=parent.getParent();
		//System.out.println("Chk loop grandpa:"+"step- "+node.getStep()+", "+Arrays.toString(node.getPuzzleState()));
		int [] chkstate=node.getPuzzleState();
		for(int i=0;i<size*size;i++){
			if(chkstate[i]!=state[i]){
				f=false;
				break;
			}
		}
		if(f==true) return true;
		
		//System.out.println("CHECK OPEN LIST");
		Node n= new Node(0,state);
		if(openList.contains(n)){
			found=true;
		}
		else {
			found=false;
		}
		
		if(found==true) return true;
		//System.out.println("CHECK CLOSED LIST");
		if(closedList.contains(n)){
			found=true;
		}
		else {
			found=false;
		}
		
		}
		else {
			found=false;
		}
		return found;
		
	}
	
	/*
	 * Function that compares if the state given as an argument is same as the goal state
	 */
	public boolean checkEnd(int [] state){
		for(int i=0;i<size*size;i++){
			if(state[i]!=goalState[i]){
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Function that traces the path from the last step to the first one using the parent propeerty
	 */
	public void tracePath(Node n){
		System.out.println("Number of steps: "+ numOfSteps);
		System.out.println("Step "+n.getStep()+": index= "+n.getId()+" ,direction= "+n.getDirection()+" ,hVal="+n.getHeuristicVal()+" ,fVal="+n.getFinalCost());
		Node parent=n.getParent();
		while(parent.getParent()!=null){
			System.out.println("Step "+parent.getStep()+": index= "+parent.getId()+" ,direction= "+parent.getDirection()+" ,hVal="+parent.getHeuristicVal()+" ,fVal="+parent.getFinalCost());
			parent=parent.getParent();
		}
		
	}
	
	/*
	 * ONLY FOR ODD PUZZLE SIZE 
	 * In order the puzzle to bee solvable:
	 * The number of inversions of the  initial state has to be even
	 */
	public int calculateNumberOfInversions(int [] initstate){
		int sum=0;
		for(int i=0;i<size*size;i++){
			
			int smallerThanMe=0;
			for(int j=0;j<i;j++){
				if(initstate[j]!=0){
				if(initstate[j]<initstate[i]){
					smallerThanMe++;
				}
				}
			}
			
			sum+=(initstate[i]-smallerThanMe);
		}	
		return sum;
	}
	
	/*
	 * This method calculates the Manhattan distance in advance for all of the possible combinations
	 * (each block to each other block) 
	 */
	public int [][] calculateManhattan(){
		int [][] matrix=new int [size*size][size*size];
		for(int i=0;i<size*size;i++){
			int x1=i/size;
			int y1=i%size;
			for(int j=0;j<size*size;j++){
				int x2= j/size; 
				int y2= j%size;
				matrix[i][j]=Math.abs(x1-x2)+Math.abs(y1-y2);
			}
		}
		return matrix;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EightPuzzleSolver puzzle= new EightPuzzleSolver();
		puzzle.printInitialState();
		
		long startTime = System.nanoTime();
		puzzle.solve();
		long endTime = System.nanoTime();
		
		double difference = (endTime - startTime)/1e6;
		System.out.println("Time elapsed in milliseconds: "+ difference);
	}

}
