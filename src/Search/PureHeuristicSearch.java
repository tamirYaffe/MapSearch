package Search;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class PureHeuristicSearch  extends ASearch
{
	// Define lists here ...

	PriorityQueue<ASearchNode> openList;
	HashMap<String,ASearchNode> closedList;

	@Override
	public String getSolverName() 
	{
		return "PHS";
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
		openList= new PriorityQueue<>(new Comparator<ASearchNode>() {
			@Override
			public int compare(ASearchNode o1, ASearchNode o2) {
				if (o1.getH() > o2.getH()) return 1;
				if (o1.getH() == o2.getH()) return 0;
				return -1;
			}
		});
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
		return closedList.containsKey(node._currentProblemState.toString());
	}

	@Override
	public void addToOpen(ASearchNode node)
	{
		openList.add(node);
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
		return openList.poll();
	}

}