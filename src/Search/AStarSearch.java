package Search;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStarSearch   extends ASearch
{
	// Define lists here ...

	PriorityQueue<ASearchNode> openList;
	HashMap<String,ASearchNode> closedList;
	HashMap<String,ASearchNode> openContainer;

	@Override
	public String getSolverName()
	{
		return "A*";
	}

	@Override
	public ASearchNode createSearchRoot(IProblemState problemState)
	{
		ASearchNode newNode = new HeuristicSearchNode(problemState);
		return newNode;
	}

	@Override
	public void initLists()
	{
		closedList=new HashMap<>();
		openContainer=new HashMap<>();
		openList= new PriorityQueue<>(new Comparator<ASearchNode>() {
			@Override
			public int compare(ASearchNode o1, ASearchNode o2) {
				if (o1.getF() > o2.getF()) return 1;
				if (o1.getF() < o2.getF()) return -1;
				if (o1.getH() > o2.getH()) return 1;
				if (o1.getH() < o2.getH()) return -1;
				return 0;
			}
		});
	}

	@Override
	public ASearchNode getOpen(ASearchNode node)
	{
		if (isOpen(node))
			return openContainer.get(node._currentProblemState.toString());
		return null;
	}

	@Override
	public boolean isOpen(ASearchNode node)
	{
		return openContainer.containsKey(node._currentProblemState.toString());
	}

	@Override
	public boolean isClosed(ASearchNode node)
	{
		return closedList.containsKey(node._currentProblemState.toString());
	}

	@Override
	public void addToOpen(ASearchNode node)
	{
		if (!isOpen(node)){
			openContainer.put(node._currentProblemState.toString(),node);
			openList.add(node);
		}
		else if(node.getG()<openContainer.get(node._currentProblemState.toString()).getG()){
			openContainer.replace(node._currentProblemState.toString(),node);
			openList.add(node);
		}
	}

	@Override
	public void addToClosed(ASearchNode node)
	{
		closedList.put(node._currentProblemState.toString(),node);
	}

	@Override
	public int openSize()
	{
		return openList.size();
	}

	@Override
	public ASearchNode getBest()
	{
		ASearchNode res = openList.poll();
		while (res!=null && openContainer.get(res._currentProblemState.toString()).getG()<res.getG()){
			res = openList.poll();
		}
		return res;
	}

}