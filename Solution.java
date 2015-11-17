import java.util.*;

public class Solution{
  final static int DIMENSION = Data.DIMENSION;

  final static double MUTATION_RATE = 0.015;

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
    Integer[] temp = x.getGenes();
    swapPosition(temp[a], temp[b], temp);
    return new Chromosome(temp);
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
   * Generate k numbers between 0 and n and return the highest.
   * Returns the position of the parent in the population array.
   */
  static int getParent(int n, int k) {
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

  static Population getNextGeneration(Population population) {
    Chromosome[] initial = population.getChromosomes();
    int size = initial.length;
    List<Chromosome> list = new ArrayList<Chromosome>();
    Random random = new Random();
    int k = 5;

    /* Crossover */
    for(int i=0; i<size/2; i++) {
      Chromosome parent1 = initial[getParent(size, k)];
      Chromosome parent2 = initial[getParent(size, k)];
      Chromosome[] baby = pmxCrossover(parent1, parent2);
      list.add(baby[0]);
      list.add(baby[1]);
    }

    // /* Crossover */
    // for(int i=0; i<size/2; i++) {
    //   Chromosome parent1 = initial[getParent(size, k)];
    //   Chromosome parent2 = initial[getParent(size, k)];
    //   list.add(crossover(parent1, parent2));
    // }

    /* Mutation */
    for(int i=0; i<size; i++) {
      if(shouldMutate()) {
        Chromosome mutated_chromosome = swapMutation(list.get(i));
        list.set(i, mutated_chromosome);
      }
    }

    return new Population(list.toArray(new Chromosome[list.size()]));
  }

  public static void main(String[] args) {
    int size = 5000;
    Population population = new Population(size);
    for(int i=0; i<1000; i++) {
      System.out.println(population.getMinDistance());
      population = getNextGeneration(population);
    }
  }
}
