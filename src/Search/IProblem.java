package Search;

public interface IProblem
{
	IProblemState		getProblemState();
	
	IHeuristic			getProblemHeuristic();
}

