import java.util.*;
import java.io.PrintWriter;

public class Solution{
  final static int DIMENSION = Data.DIMENSION;

  final static int CAPACITY = Data.CAPACITY;

  /* Simple solution for spliting the best TSP route into smaller routes fro CVRP */
  public static void getSimpleSolution(Integer[] best_route) {
    int capacity = CAPACITY;
    double cost = 0;
    StringBuilder br = new StringBuilder();
    ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> route = new ArrayList<Integer>();
    br.append("1->");
    route.add(0);
    int i=0;
    for(i=1; i<DIMENSION; i++) {
      if(capacity - Data.demand[best_route[i]] > 0) {
        capacity -= Data.demand[best_route[i]];
        cost += Data.EDM[best_route[i]][best_route[i-1]];
        br.append((best_route[i]+1) + "->");
        route.add(best_route[i]);
      } else {
        capacity = CAPACITY - Data.demand[best_route[i]];
        cost += Data.EDM[best_route[i-1]][0];
        cost += Data.EDM[0][best_route[i]];
        br.append("1\n");
        route.add(0);
        routes.add(route);
        route = new ArrayList<Integer>();
        route.add(0);
        route.add(best_route[i]);
        br.append("1->" + (best_route[i]+1) + "->");
      }
    }
    cost += Data.EDM[best_route[i-1]][0];
    br.append("1");
    route.add(0);
    routes.add(route);

    printSolution(br.toString(), cost);
  }

  public static double getRouteCost(ArrayList<Integer> route) {
    double cost = 0.0;
    for(int i=1; i<route.size(); i++) {
      cost += Data.EDM[route.get(i-1)][route.get(i)];
    }
    return cost;
  }

  public static void randomIntervalSolution(Population population) {
    Random random = new Random();
    Integer[] best_route = population.getChromosomes()[0].getGenes();
    ArrayList<ArrayList<Integer>> best_routes = null;
    double best_cost = Double.MAX_VALUE;

    for(int round=0; round<500; round++) {
      ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>();
      int capacity = CAPACITY;
      double cost = 0.0;
      int i=0;
      int last = 1;
      for(i=1; i<DIMENSION; i++) {
        if(capacity - Data.demand[best_route[i]] > 0) {
          capacity -= Data.demand[best_route[i]];
        } else {
          int rand = random.nextInt(i - last - 1) + last + 1;

          ArrayList<Integer> route = new ArrayList<Integer>();
          route.add(0);
          for(int k=last; k<rand; k++) {
            route.add(best_route[k]);
          }
          route.add(0);
          cost += getRouteCost(route);
          routes.add(route);

          last = rand;
          i = rand;
          capacity = CAPACITY - Data.demand[best_route[i]];
        }
      }
      ArrayList<Integer> route = new ArrayList<Integer>();
      route.add(0);
      for(int k=last; k<DIMENSION; k++) {
        route.add(best_route[k]);
      }
      route.add(0);
      cost += getRouteCost(route);
      routes.add(route);

      if(cost < best_cost) {
        best_cost = cost;
        best_routes = new ArrayList<ArrayList<Integer>>(routes);
      }
    }

    StringBuilder br = new StringBuilder();
    for(int i=0; i<best_routes.size(); i++) {
      boolean start = true;
      for(Integer x : best_routes.get(i)) {
        if(start) {
          br.append(x+1);
          start = false;
        } else {
          br.append("->" + (x+1));
        }
      }
      if(i < best_routes.size() - 1) {
        br.append("\n");
      }
    }

    printSolution(br.toString(), best_cost);
  }

  public static void printSolutionToFile(String routes, Double cost) {
    try {
      PrintWriter file = new PrintWriter("best-solution.txt", "UTF-8");
      file.println("login ad12461 52610");
      file.println("name Ana Dumitras");
      file.println("algorithm Genetic Algorithm with multiple crossover operators and multiple mutation techniques");

      file.println("cost " + cost);
      file.print(routes);
      file.close();
    }
    catch (Exception ex)
    {
      System.out.println("3rr0r");
    }
  }

  public static void printSolution(String routes, Double cost) {
    System.out.println("login ad12461 52610");
    System.out.println("name Ana Dumitras");
    System.out.println("algorithm Genetic Algorithm with multiple crossover operators and multiple mutation techniques");
    System.out.println("cost " + cost);
    System.out.println(routes);
  }

  public static void main(String[] args) {
    Integer[] best = SimpleGA.runGA();
    getSimpleSolution(best);
  }
}
