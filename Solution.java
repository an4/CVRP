import java.util.*;
import java.io.PrintWriter;

public class Solution{
  final static int DIMENSION = Data.DIMENSION;

  final static int CAPACITY = Data.CAPACITY;

  final static double MUTATION_RATE = 0.05;

  static void swapPosition(int gene1, int gene2, Integer[] baby) {
    int pos1 = 0;
    int pos2 = 0;
    for(int i=0; i<baby.length; i++) {
      if(baby[i] == gene1) {
        pos1 = i;
      }
      if(baby[i] == gene2) {
        pos2 = i;
      }
    }
    baby[pos1] = gene2;
    baby[pos2] = gene1;
  }

  /**
   * PMX - Partially matched crossover
   */
  static Integer[][] PMX(Integer[] mum, Integer[] dad, int begin, int end) {
    Integer[][] baby = new Integer[2][DIMENSION];
    baby[0] = Arrays.copyOf(mum, mum.length);
    baby[1] = Arrays.copyOf(dad, dad.length);
    for(int pos=begin; pos<end; pos++) {
      int gene1 = mum[pos];
      int gene2 = dad[pos];
      swapPosition(gene1, gene2, baby[0]);
      swapPosition(gene1, gene2, baby[1]);
    }
    return baby;
  }

  /* Crossover*/
  static Chromosome[] pmxCrossover(Chromosome mum, Chromosome dad) {
    Random random = new Random();
    int begin = random.nextInt(DIMENSION-2) + 1;
    int end = random.nextInt(DIMENSION-1-begin) + begin;
    Integer[][] baby = PMX(mum.getGenes(), dad.getGenes(), begin, end);
    Chromosome[] babies = new Chromosome[2];
    babies[0] = new Chromosome(baby[0]);
    babies[1] = new Chromosome(baby[1]);
    return babies;
  }

  static Chromosome crossover(Chromosome parent1, Chromosome parent2) {
    Random random = new Random();
    int begin = random.nextInt(DIMENSION-2) + 1;
    int end = random.nextInt(DIMENSION-1-begin) + begin;
    Integer[] mum = parent1.getGenes();
    Integer[] dad = parent2.getGenes();
    /* Keep track of added cities */
    int[] added = new int[DIMENSION];
    Integer[] baby = new Integer[DIMENSION];
    /* Select a subset from the first parent */
    for(int i=begin; i<end; i++) {
      baby[i] = mum[i];
      added[baby[i]] = 1;
    }
    /* Set start point */
    baby[0] = 0;
    /* Get genes from the other parent */
    int j =1;
    for(int i=1; i<dad.length; i++) {
      if(j == begin) {
        j = end;
      }
      if(added[dad[i]] == 0) {
        baby[j++] = dad[i];
        added[dad[i]] = 1;
      }
    }
    return new Chromosome(baby);
  }

  /*  Swap Mutation */
  static Chromosome swapMutation(Chromosome x) {
    Random random = new Random();
    int a = random.nextInt(DIMENSION-1) + 1;
    int b = random.nextInt(DIMENSION-1) + 1;
    Integer[] genes = x.getGenes();
    int temp_gene = genes[a];
    genes[a] = genes[b];
    genes[b] = temp_gene;
    return new Chromosome(genes);
  }

  /* Check if mutation should be applied. */
  static boolean shouldMutate() {
    if(Math.random() < MUTATION_RATE) {
      return true;
    }
    return false;
  }

  /*
   * Tournament selection
   * n - population size
   * Generate k numbers between 0 and n and return the lowest.
   * Returns the position of the parent in the population array.
   */
  static int getParentTournament(int n, int k) {
    Random random = new Random();
    int min = Integer.MAX_VALUE;
    for(int i=0; i<k; i++) {
      int r = random.nextInt(n-1);
      if(r < min) {
        min = r;
      }
    }
    return min;
  }

  /*
   * Roulette Wheel selection
   */
  static int getParentRouletteWheel(Integer[] wheel) {
    Random random = new Random();
    int random_index = random.nextInt(wheel.length - 1);
    return wheel[random_index];
  }

  static Population getNextGeneration(Population population) {
    Chromosome[] initial = population.getChromosomes();
    int size = initial.length;
    List<Chromosome> list = new ArrayList<Chromosome>();
    Random random = new Random();
    int k = 5;

    /* Crossover = Roulette Wheel Selection + Crossover */
    for(int i=0; i<2*size; i++) {
      int p1 = getParentRouletteWheel(population.getRouletteWheel());
      int p2 = getParentRouletteWheel(population.getRouletteWheel());
      while(p1 == p2) {
        p2 = getParentRouletteWheel(population.getRouletteWheel());
      }
      Chromosome parent1 = initial[p1];
      Chromosome parent2 = initial[p2];
      list.add(crossover(parent1, parent2));
    }

    // /* Crossover = Tournament Selection + Crossover */
    // for(int i=0; i<2*size; i++) {
    //   Chromosome parent1 = initial[getParentTournament(size, k)];
    //   Chromosome parent2 = initial[getParentTournament(size, k)];
    //   list.add(crossover(parent1, parent2));
    // }

    /* Mutation */
    for(int i=0; i<list.size(); i++) {
      if(shouldMutate()) {
        Chromosome mutated_chromosome = swapMutation(list.get(i));
        list.set(i, mutated_chromosome);
      }
    }

    return new Population(list.toArray(new Chromosome[list.size()]), size);
  }

  /* Simple solution for spliting the best TSP route into smaller routes fro CVRP */
  public static void getSimpleSolution(Population population) {
    Integer[] best_route = population.getChromosomes()[0].getGenes();
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
        swapPosition(genes[i], genes[j], current);
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
        swapPosition(genes[i], genes[j], current);
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
    int size = 2000;
    int generations = 4000;
    int GA_rounds = 5;

    Population best_population = null;
    double cost = Double.MAX_VALUE;

    for(int i=0; i<GA_rounds; i++) {
      Population population = new Population(size);
      for(int j=0; j<generations; j++) {
        // System.out.println(population.getMinDistance());
        population = getNextGeneration(population);
      }
      // System.out.println(population.getMinDistance());
      if(population.getMinDistance() < cost) {
        cost = population.getMinDistance();
        best_population = population;
      }
    }
    System.out.println(best_population.getMinDistance());

    // smallerRoutes(best_population.getChromosomes()[0].getGenes());

    getSimpleSolution(best_population);

    // randomIntervalSolution(best_population);
  }
}
