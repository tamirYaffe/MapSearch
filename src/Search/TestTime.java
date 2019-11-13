package Search;

public  class TestTime {
    public static int numOfNodes;
    public static double performMoveSumOfTime; //includes duplicates
    public static double graphCreationSumOfTime;
    public static double hSumOfTime;

    public static double calculateAveragePerformMoveTime(){return performMoveSumOfTime/numOfNodes;}
    public static double calculateAverageGraphCreationTime(){return graphCreationSumOfTime/numOfNodes;}
    public static double calculateAverageHTime(){return hSumOfTime/numOfNodes;}
}
