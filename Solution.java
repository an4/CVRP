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
    System.out.println("SSC: " + cost);
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
    System.out.println("Rand: " + best_cost);
  }

  /////////////////////////////////////////////////////////////////////////////

  public static void printSolution(String routes, Double cost) {
    try {
      PrintWriter file = new PrintWriter("best-solution.txt", "UTF-8");
      file.println("login ad12461 52610");
      file.println("name Ana Dumitras");
      file.println("algorithm Genetic Algorithm with crossover and mutation");

      file.println("cost " + cost);
      file.print(routes);
      file.close();
    }
    catch (Exception ex)
    {
      System.out.println("3rr0r");
    }
  }

  public static double getSimpleCost(Integer[] genes) {
    int i=0;
    int capacity = CAPACITY;
    double cost = 0.0;
    for(i=1; i<DIMENSION; i++) {
      if(capacity - Data.demand[genes[i]] > 0) {
        capacity -= Data.demand[genes[i]];
        cost += Data.EDM[genes[i]][genes[i-1]];
      } else {
        capacity = CAPACITY - Data.demand[genes[i]];
        cost += Data.EDM[genes[i-1]][0];
        cost += Data.EDM[0][genes[i]];
      }
    }
    cost += Data.EDM[genes[i-1]][0];
    return cost;
  }

  public static Integer[] chooseNextNeighbourSAHC(Integer[] genes) {
    Integer[] fittest = Arrays.copyOf(genes, genes.length);
    double cost = getSimpleCost(genes);
    for(int i=1; i<DIMENSION-1; i++) {
      for(int j=i+1; j<DIMENSION; j++) {
        Integer[] current = Arrays.copyOf(genes, genes.length);;
        SimpleGA.swapPosition(genes[i], genes[j], current);
        double new_cost = getSimpleCost(current);
        if(new_cost < cost) {
          cost = new_cost;
          fittest = Arrays.copyOf(current, current.length);
        }
      }
    }
    System.out.println(cost);
    return fittest;
  }

  public static Integer[] steepestAscentHillClimbing(Integer[] genes) {
    double cost = getSimpleCost(genes);
    double last_cost = 0.0;
    Integer[] next = null;
    while(last_cost != cost) {
      last_cost = cost;
      next = chooseNextNeighbourSAHC(genes);
      cost = getSimpleCost(next);
    }
    System.out.println("SAHC: " + cost);
    return next;
  }

  public static Integer[] chooseNextNeighbourSHC(Integer[] genes) {
    double cost = getSimpleCost(genes);
    for(int i=1; i<DIMENSION-1; i++) {
      for(int j=i+1; j<DIMENSION; j++) {
        Integer[] current = Arrays.copyOf(genes, genes.length);;
        SimpleGA.swapPosition(genes[i], genes[j], current);
        double new_cost = getSimpleCost(current);
        if(new_cost < cost) {
          return current;
        }
      }
    }
    return genes;
  }

  public static Integer[] simpleHillClimbing(Integer[] genes) {
    double cost = getSimpleCost(genes);
    double last_cost = 0.0;
    Integer[] next = null;
    while(last_cost != cost) {
      last_cost = cost;
      next = chooseNextNeighbourSHC(genes);
      cost = getSimpleCost(next);
    }
    System.out.println("SHC: " + cost);
    return next;
  }

/******************************************************************************/
  public static double getSmallRouteCost(ArrayList<Integer> route) {
    double cost = 0.0;
    for(int i=1; i<route.size(); i++) {
      cost += Data.EDM[route.get(i-1)][route.get(i)];
    }
    return cost;
  }

  public static void smallerRoutes(ArrayList<ArrayList<Integer>> routes) {
    int total_routes_number = routes.size();
    double cost[] = new double[total_routes_number];
    double total_cost = 0.0;
    for(int i=0; i<total_routes_number; i++) {
      cost[i] = getSmallRouteCost(routes.get(i));
      total_cost += cost[i];
    }

    System.out.println("C: " + total_cost);

    Random random = new Random();

    for(int i=0; i<total_routes_number-1; i++) {
      for(int j=i+1; j<total_routes_number; j++) {
        for(int k=0; k<500; k++) {
          int a = random.nextInt(routes.get(i).size()-2) + 1;
          int b = random.nextInt(routes.get(j).size()-2) + 1;
          int city_a = routes.get(i).get(a);
          int city_b = routes.get(j).get(b);

          ArrayList<Integer> temp_a = new ArrayList<Integer>(routes.get(i));
          ArrayList<Integer> temp_b = new ArrayList<Integer>(routes.get(j));

          temp_a.set(a, city_b);
          temp_b.set(b, city_a);

          double new_cost_a = getSmallRouteCost(temp_a);
          double new_cost_b = getSmallRouteCost(temp_b);

          if(new_cost_a + new_cost_b < cost[i] + cost[j] &&
          new_cost_a <= CAPACITY && new_cost_b <= CAPACITY) {
            routes.set(i, temp_a);
            routes.set(j, temp_b);
            cost[i] = new_cost_a;
            cost[j] = new_cost_b;
          }
        }
      }
    }

    total_cost = 0.0;
    for(int i=0; i<total_routes_number; i++) {
      total_cost += cost[i];
    }
    System.out.println("C: " + total_cost);

    StringBuilder br = new StringBuilder();
    for(int i=0; i<routes.size(); i++) {
      for(int j=0; j<routes.get(i).size(); j++) {
        if(j == routes.get(i).size() -1) {
          br.append((routes.get(i).get(j) + 1) + "\n");
        } else {
          br.append((routes.get(i).get(j) + 1) + "->");
        }
      }
    }
    printSolution(br.toString(), total_cost);
  }

/******************************************************************************/

  public static void main(String[] args) {
    // int size = 2000;
    // int generations = 4000;
    // int GA_rounds = 5;
    //
    // Integer[] best_genes = null;
    // double cost = Double.MAX_VALUE;
    //
    // for(int i=0; i<GA_rounds; i++) {
    //   Population population = new Population(size);
    //   for(int j=0; j<generations; j++) {
    //     // System.out.println(population.getMinDistance());
    //     population = getNextGeneration(population);
    //   }
    //
    //   Integer[] best = population.getChromosomes()[0].getGenes();
    //
    //   best = steepestAscentHillClimbing(best);
    //
    //   // System.out.println(population.getMinDistance());
    //   if(Chromosome.computeDistance(best) < cost) {
    //     cost = Chromosome.computeDistance(best);
    //     best_genes = best;
    //   }
    // }
    // System.out.println(cost);
    //
    // // Integer[] solution = steepestAscentHillClimbing(best_population.getChromosomes()[0].getGenes());
    //
    // // smallerRoutes(best_population.getChromosomes()[0].getGenes());
    //
    // getSimpleSolution(best_genes);
    //
    // // randomIntervalSolution(best_population);
    Population population = SimpleGA.runGA();

    getSimpleSolution(population.getChromosomes()[0].getGenes());
  }
}
