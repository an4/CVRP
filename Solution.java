import java.util.*;

public class Solution{
  final static int DIMENSION = Data.DIMENSION;
  /**
   * PMX - Partially matched crossover
   */
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
    Integer[] temp = x.getGenes();
    swapPosition(temp[a], temp[b], temp);
    return new Chromosome(temp);
  }

  /**
   * Get initial population.
   * int n - size of population
   */
  static Chromosome[] getInitialPopulation(int n) {
    Chromosome[] population = new Chromosome[n];
    for(int i=0 ; i<n; i++) {
      population[i] = new Chromosome();
    }

    Arrays.sort(population, new Comparator<Chromosome>() {
        @Override
        public int compare(Chromosome c1, Chromosome c2) {
            return c1.getDistance().compareTo(c2.getDistance());
        }
    });

    return population;
  }

  /**
   * Get average fitness level.
   */
  static double getAvgFitness(Chromosome[] population) {
    double sum = 0.0;
    for(int i=0; i<population.length; i++) {
      sum += population[i].getFitness();
    }
    return (double)sum/population.length;
  }

  /**
   * Return fittest chromosome from the given population.
   */
  static Chromosome getFittest(Chromosome[] population) {
    return population[0];
  }

  static Chromosome[] getNextGeneration(Chromosome[] initial, int n) {
    List<Chromosome> list = new ArrayList<Chromosome>();
    Random random = new Random();

    /* Crossover */


    /* Mutation */

    return list.toArray(new Chromosome[list.size()]);
  }

  public static void main(String[] args) {
    int size = 25;
    Chromosome[] population = getInitialPopulation(size);
    for(Chromosome x: population) {
      System.out.println(x.getDistance());
      System.out.println(x.getFitness());
      // System.out.println(Arrays.toString(x.getGenes()));
    }
  }
}
