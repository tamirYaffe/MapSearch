package Search;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class UniformCostSearch   extends ASearch
{
	// Define lists here ...

	private PriorityQueue<ASearchNode> openList;
	private HashMap<IProblemState,ASearchNode> closedList;
	private HashMap<IProblemState,ASearchNode> openContainer;

	@Override
	public String getSolverName() 
	{
		return "UCS";
	}

	@Override
	public ASearchNode createSearchRoot(IProblemState problemState)
	{
		return new BlindSearchNode(problemState);
	}
	
	@Override
	public void initLists() 
	{
		closedList=new HashMap<>();
		openContainer=new HashMap<>();
		openList= new PriorityQueue<>(Comparator.comparingDouble(ASearchNode::getG));
	}

	@Override
	public ASearchNode getOpen(ASearchNode node)
	{
		if (isOpen(node))
			return openContainer.get(node.currentProblemState);
		return null;
	}

	@Override
	public boolean isOpen(ASearchNode node)
	{
		return openContainer.containsKey(node.currentProblemState);
	}
	
	@Override
	public boolean isClosed(ASearchNode node)
	{
		return closedList.containsKey(node.currentProblemState);
	}

	@Override
	public void addToOpen(ASearchNode node)
	{
		if (!isOpen(node)){
			openContainer.put(node.currentProblemState,node);
			openList.add(node);
		}
		else if(node.getG()<openContainer.get(node.currentProblemState).getG()){
			openContainer.replace(node.currentProblemState,node);
			openList.add(node);
		}
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
		ASearchNode res = openList.poll();
		while (res!=null && openContainer.get(res.currentProblemState).getG()<res.getG()){
			res = openList.poll();
		}
		return res;
	}

}
