package Search;

public interface IProblem
{
	public IProblemState		getProblemState();
	
	public IHeuristic			getProblemHeuristic();

	public boolean 				performMove(IProblemMove move);

}

