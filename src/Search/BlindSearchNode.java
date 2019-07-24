package Search;

public class BlindSearchNode extends ASearchNode
{
	double	_g;

	public BlindSearchNode
	(
		IProblemState 	currentProblemState
	) 
	{
		_prev					= null;
		_currentProblemState 	= currentProblemState;
		_g 						= 0;
	}
	
	public BlindSearchNode
	(
		ASearchNode		prev,
		IProblemState 	currentProblemState,
		double 			g
	) 
	{
		_prev					= prev;
		_currentProblemState 	= currentProblemState;
		_g 						= g;
	}
	
	@Override
	public double getH()
	{
		return 0;
	}
	
	@Override
	public double getG()
	{
		return _g;
	}
	
	@Override
	public double getF() 
	{
		return _g;
	}

	@Override
	public ASearchNode createSearchNode
	(
		IProblemState 	currentProblemState
	) 
	{
		double 		g		= _g + currentProblemState.getStateLastMoveCost();
		ASearchNode newNode = new BlindSearchNode(this, currentProblemState, g);
		return newNode;
	}
	
	@Override
	public boolean equals
	(
		Object obj
	)
	{
		if (obj instanceof BlindSearchNode)
		{
			BlindSearchNode otherNode = (BlindSearchNode)obj;
			if (_currentProblemState.equals(otherNode._currentProblemState))
				return true;
		}
		return false;
	}

}
