package Search;

import java.util.List;

public interface IProblemState 
{	
	List<IProblemState> 	getNeighborStates();
	
	IProblem				getProblem();
	
	boolean 				isGoalState();
		
	IProblemMove			getStateLastMove();
	
	double	 			getStateLastMoveCost();
	
	IProblemState 		performMove(IProblemMove move);

}
