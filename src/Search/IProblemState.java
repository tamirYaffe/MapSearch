package Search;

import java.util.List;

public interface IProblemState 
{	
	public List<IProblemState> 	getNeighborStates();
	
	public IProblem				getProblem();
	
	public boolean 				isGoalState();
		
	public IProblemMove			getStateLastMove();
	
	public double	 			getStateLastMoveCost();
	
	public IProblemState 		performMove(IProblemMove move);

}
