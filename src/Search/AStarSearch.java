package Search;

import java.util.HashMap;
import java.util.PriorityQueue;

public class AStarSearch   extends ASearch
{
	// Define lists here ...

	private PriorityQueue<ASearchNode> openList;
	private HashMap<IProblemState,ASearchNode> closedList;
	private HashMap<IProblemState,ASearchNode> openContainer;

	@Override
	public String getSolverName()
	{
		return "A*";
	}

	@Override
	public ASearchNode createSearchRoot(IProblemState problemState)
	{
		return new HeuristicSearchNode(problemState);
	}

	@Override
	public void initLists() {
		closedList=new HashMap<>();
		openContainer=new HashMap<>();
		openList= new PriorityQueue<>((o1, o2) -> {
			if (o1.getF() > o2.getF()) return 1;
			if (o1.getF() < o2.getF()) return -1;
			return Double.compare(o1.getH(), o2.getH());
		});
	}

	@Override
	public ASearchNode getOpen(ASearchNode node)
	{
//		if (isOpen(node))
//			return openContainer.get(node.currentProblemState);
//		return null;
		return openContainer.getOrDefault(node.currentProblemState,null);
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
		ASearchNode prevNode = openContainer.getOrDefault(node.currentProblemState,null);
		if (prevNode==null){
			openContainer.put(node.currentProblemState,node);
			openList.add(node);
		}
		else if(node.getG()<prevNode.getG()){
			prevNode.setG(node.getG());
//			openContainer.replace(node.currentProblemState,node);
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
		double currG = openContainer.get(res.currentProblemState).getG();
		while (res!=null && currG<res.getG()){
			res = openList.poll();
		}
		return res;
	}

}