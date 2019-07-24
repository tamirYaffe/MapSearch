package Search;

public class HeuristicSearchNode extends BlindSearchNode
{
	double		_h;
	IHeuristic 	_heuristic;
	
	public HeuristicSearchNode
	(
		IProblemState 	currentProblemState	
	) 
	{
		super(currentProblemState);
		_heuristic 	= currentProblemState.getProblem().getProblemHeuristic();
		_h			= _heuristic.getHeuristic(currentProblemState);
	}
	
	
	public HeuristicSearchNode
	(
		ASearchNode		prev,
		IProblemState 	currentProblemState,
		double 			g,
		IHeuristic 		heuristic
	) 
	{
		super(prev, currentProblemState, g);
		_heuristic 	= heuristic;
		_h			= _heuristic.getHeuristic(currentProblemState);
	}
	
	@Override
	public double getH()
	{
		return _h;
	}
	
	
	@Override
	public double getF()
	{
		return _g + _h;
	}
	
	
	@Override
	public ASearchNode createSearchNode
	(
		IProblemState 	currentProblemState
	) 
	{
		double 		g		= _g + currentProblemState.getStateLastMoveCost();
		ASearchNode newNode = new HeuristicSearchNode(this, currentProblemState, g, _heuristic);
		return newNode;
	}
}
