import java.util.Arrays;
import java.util.ArrayList;

public class HillClimber {
  final static int DIMENSION = Data.DIMENSION;
  final static int CAPACITY = Data.CAPACITY;

  public static double getSimpleCVRPCost(Integer[] genes) {
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

  public static double getSimpleTSPCost(Integer[] genes) {
    double cost = 0.0;
    int i = 0;
    for(i=1; i<DIMENSION; i++) {
      cost += Data.EDM[genes[i]][genes[i-1]];
    }
    cost += Data.EDM[genes[i-1]][0];
    return cost;
  }

  public static Integer[] chooseNextNeighbourSAHC(Integer[] genes) {
    Integer[] fittest = Arrays.copyOf(genes, genes.length);
    // double cost = getSimpleCVRPCost(genes);
    double cost = getSimpleTSPCost(genes);
    for(int i=1; i<DIMENSION-1; i++) {
      for(int j=i+1; j<DIMENSION; j++) {
        Integer[] current = Arrays.copyOf(genes, genes.length);
        SimpleGA.swapPosition(genes[i], genes[j], current);
        // double new_cost = getSimpleCVRPCost(current);
        double new_cost = getSimpleTSPCost(current);
        if(new_cost < cost) {
          cost = new_cost;
          fittest = Arrays.copyOf(current, current.length);
        }
      }
    }
    return fittest;
  }

  public static Integer[] steepestAscentHillClimbing(Integer[] genes) {
    // double cost = getSimpleCVRPCost(genes);
    double best_cost = getSimpleTSPCost(genes);
    Integer[] best_genes = Arrays.copyOf(genes, genes.length);
    Integer[] next = chooseNextNeighbourSAHC(best_genes);
    double new_cost = getSimpleTSPCost(next);
    while(new_cost < best_cost) {
      best_genes = Arrays.copyOf(next, next.length);
      best_cost = new_cost;
      next = chooseNextNeighbourSAHC(best_genes);
      new_cost = getSimpleTSPCost(next);
    }
    return best_genes;
  }

  public static Integer[] chooseNextNeighbourSHC(Integer[] genes) {
    // double cost = getSimpleCVRPCost(genes);
    double cost = getSimpleTSPCost(genes);
    for(int i=1; i<DIMENSION-1; i++) {
      for(int j=i+1; j<DIMENSION; j++) {
        Integer[] current = Arrays.copyOf(genes, genes.length);;
        SimpleGA.swapPosition(genes[i], genes[j], current);
        // double new_cost = getSimpleCVRPCost(current);
        double new_cost = getSimpleTSPCost(current);
        if(new_cost < cost) {
          return current;
        }
      }
    }
    return genes;
  }

  public static Integer[] simpleHillClimbing(Integer[] genes) {
    // double cost = getSimpleCVRPCost(genes);
    double cost = getSimpleTSPCost(genes);
    double last_cost = getSimpleTSPCost(genes);
    Integer[] next = null;
    while(last_cost != cost) {
      next = chooseNextNeighbourSHC(genes);
      cost = getSimpleCVRPCost(next);
    }
    return next;
  }

  public static void runHC() {
    Integer[] initial = Chromosome.getPermutation();
    System.out.println("B: " + getSimpleTSPCost(initial));
    initial = steepestAscentHillClimbing(initial);
    System.out.println("A: " + getSimpleTSPCost(initial));
  }

  public static Population  getPopulationOfLocalPeaks(int n) {
    ArrayList<Chromosome> list = new ArrayList<Chromosome>();
    for(int i=0; i<n; i++) {
      Integer[] random_genes = Chromosome.getPermutation();
      Integer[] optimized_genes = steepestAscentHillClimbing(random_genes);
      System.out.println(getSimpleTSPCost(optimized_genes));
      Chromosome new_chromosome = new Chromosome(optimized_genes);
      list.add(new_chromosome);
    }
    return new Population(list.toArray(new Chromosome[list.size()]), n);
  }
}
