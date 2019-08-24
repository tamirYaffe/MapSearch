package Search;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class PureHeuristicSearch  extends ASearch
{
	// Define lists here ...

	private PriorityQueue<ASearchNode> openList;
	private HashMap<IProblemState,ASearchNode> closedList;

	@Override
	public String getSolverName() 
	{
		return "PHS";
	}

	@Override
	public ASearchNode createSearchRoot(IProblemState problemState)
	{
		return new HeuristicSearchNode(problemState);
	}

	@Override
	public void initLists()
	{
		closedList=new HashMap<>();
		openList= new PriorityQueue<>(Comparator.comparingDouble(ASearchNode::getH));
	}

	@Override
	public ASearchNode getOpen(ASearchNode node)
	{
		if (isOpen(node))
			return node;
		return null;
	}

	@Override
	public boolean isOpen(ASearchNode node)
	{
		return !openList.isEmpty() && openList.contains(node);
	}

	@Override
	public boolean isClosed(ASearchNode node)
	{
		return closedList.containsKey(node.currentProblemState);
	}

	@Override
	public void addToOpen(ASearchNode node)
	{
		openList.add(node);
	}

	@Override
	public void addToClosed(ASearchNode node)
	{
		closedList.put(node.currentProblemState,node);
	}

	@Override
	public int openSize()
	{
		return openList.size();
	}

	@Override
	public ASearchNode getBest()
	{
		return openList.poll();
	}

}