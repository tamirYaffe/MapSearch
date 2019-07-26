package Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class ASearch
{

	public int closed;

	public List<IProblemMove> solve
	(
		IProblem problem
	) 
	{
		IProblemState 		problemState	= problem.getProblemState();
		ASearchNode			goal			= abstractSearch(problemState);
		List<IProblemMove> 	solution		= goalNodeToSolutionPath(goal);
//		System.out.println("list size: "+openSize());
		return solution;
	}
	
	private	ASearchNode	abstractSearch
	(
		IProblemState problemState
	)
	{
		initLists();
		ASearchNode Vs 		= createSearchRoot(problemState);
		System.out.println("Root.H: "+ Vs.getH());
		ASearchNode current	= null;
		addToOpen(Vs);
		closed=0;

		while (openSize() > 0)
		{
			current = getBest();
			if (current.isGoal())
				return current;
			List<ASearchNode> neighbors = current.getNeighbors();
			for (ASearchNode Vn : neighbors)
			{
				if (isClosed(Vn)) {

					continue;
				}
				
				if (!isOpen(Vn) || getOpen(Vn).getG() > Vn.getG())
					addToOpen(Vn);
			}
			addToClosed(current);
//			System.out.println(current._currentProblemState);
			closed++;
		}
		return null;
	}
	
	private List<IProblemMove> goalNodeToSolutionPath
	(
		ASearchNode goal
	)
	{
		if (goal == null)
			return null;
		ASearchNode 		currentNode		= goal;
		List<IProblemMove> 	solutionPath 	= new ArrayList<>();
		while (currentNode._prev != null)
		{
			solutionPath.add(currentNode.getLastMove());
			currentNode = currentNode._prev;
		}
		Collections.reverse(solutionPath);
		return solutionPath;
	}
	
	abstract public String				getSolverName();
		
	abstract public	void 				initLists();
	
	abstract public	ASearchNode			getOpen(ASearchNode node);
		
	abstract public	boolean				isOpen(ASearchNode node);
	
	abstract public	boolean				isClosed(ASearchNode node);
	
	abstract public	ASearchNode 		createSearchRoot(IProblemState problemState);
	
	abstract public	void 				addToOpen(ASearchNode node);
	
	abstract public	void			addToClosed(ASearchNode node);
	
	abstract public	int 			openSize();
	
	abstract public	ASearchNode		getBest();
	

}
