import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SimpleGA {
  final static int DIMENSION = Data.DIMENSION;

  final static int CAPACITY = Data.CAPACITY;

  final static double MUTATION_RATE = 0.25;

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

  /* PMX Crossover*/
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

  static Chromosome orderedCrossover(Chromosome parent1, Chromosome parent2) {
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
    int j = end;
    for(int i=1; i<dad.length; i++) {
      if(j == dad.length) {
        j = 1;
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

  static Chromosome insertMutation(Chromosome x) {
    Random random = new Random();
    int pos = random.nextInt(DIMENSION-1) + 1;
    int gene = random.nextInt(DIMENSION-1) + 1;
    Integer[] genes = x.getGenes();
    int gene_pos = 0;
    for(int i=1; i<DIMENSION; i++) {
      if(genes[i] == gene) {
        gene_pos = i;
        break;
      }
    }
    if(gene_pos < pos) {
      for(int i=gene_pos; i<pos; i++) {
        genes[i] = genes[i+1];
      }
      genes[pos] = gene;
    } else {
      for(int i=gene_pos; i>pos; i--) {
        genes[i] = genes[i-1];
      }
      genes[pos] = gene;
    }
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

  /* Get next generation using Roulette Wheel Selection and crossover */
  static Population getNextGenerationRW(Population population) {
    Chromosome[] initial = population.getChromosomes();
    int size = initial.length;
    List<Chromosome> list = new ArrayList<Chromosome>();

    /* Crossover = Roulette Wheel Selection + Crossover */
    for(int i=0; i<2*size; i++) {
      int p1 = getParentRouletteWheel(population.getRouletteWheel());
      int p2 = getParentRouletteWheel(population.getRouletteWheel());
      while(p1 == p2) {
        p2 = getParentRouletteWheel(population.getRouletteWheel());
      }
      Chromosome parent1 = initial[p1];
      Chromosome parent2 = initial[p2];

      list.add(orderedCrossover(parent1, parent2));
      list.add(crossover(parent1, parent2));

      Chromosome[] babies = pmxCrossover(parent1, parent2);
      list.add(babies[0]);
      list.add(babies[1]);
    }

    /* Mutation */
    for(int i=0; i<list.size(); i++) {
      if(shouldMutate()) {
        Chromosome mutated_chromosome = insertMutation(list.get(i));
        list.set(i, mutated_chromosome);
      }
      if(shouldMutate()) {
        Chromosome mutated_chromosome = swapMutation(list.get(i));
        list.set(i, mutated_chromosome);
      }
    }

    int count = 0;
    for(Chromosome x: initial) {
      if(count >= 50) {
        break;
      }
      list.add(x);
      count++;
    }

    return new Population(list.toArray(new Chromosome[list.size()]), size);
  }

  /* Get next generation using Tournament Selection and crossover */
  static Population getNextGenerationTS(Population population) {
    Chromosome[] initial = population.getChromosomes();
    int size = initial.length;
    List<Chromosome> list = new ArrayList<Chromosome>();
    int k = 1;

    /* Crossover = Tournament Selection + Crossover */
    for(int i=0; i<size; i++) {
      Chromosome parent1 = initial[getParentTournament(size, k)];
      Chromosome parent2 = initial[getParentTournament(size, k)];
      list.add(crossover(parent1, parent2));

      // Chromosome[] babies = pmxCrossover(parent1, parent2);
      // list.add(babies[0]);
      // list.add(babies[1]);
    }

    /* Mutation */
    for(int i=0; i<list.size(); i++) {
      if(shouldMutate()) {
        Chromosome mutated_chromosome = insertMutation(list.get(i));
        list.set(i, mutated_chromosome);
      }
      if(shouldMutate()) {
        Chromosome mutated_chromosome = swapMutation(list.get(i));
        list.set(i, mutated_chromosome);
      }
    }

    // int count = 0;
    // for(Chromosome x: initial) {
    //   if(count >= 100) {
    //     break;
    //   }
    //   list.add(x);
    //   count++;
    // }

    return new Population(list.toArray(new Chromosome[list.size()]), size);
  }

  /* */
  public static Integer[] runGA() {
    int size = 5000;
    int generations = 3000;
    int GA_rounds = 4;

    Population best = null;
    double cost = Double.MAX_VALUE;

    /* Roulette Wheel Selection */
    for(int i=0; i<GA_rounds; i++) {
      Population population = new Population(size);
      int similar = 0;
      double last_cost = population.getMinDistance();
      for(int j=0; j<generations; j++) {
        population = getNextGenerationRW(population);
        if(last_cost == population.getMinDistance()) {
          similar++;
        } else {
          last_cost = population.getMinDistance();
          similar = 0;
        }
        if(similar == 20) {
          break;
        }
      }
      if(population.getMinDistance() < cost) {
        cost = population.getMinDistance();
        best = population;
      }
    }

    // /* Tournament Selection */
    // for(int i=0; i<GA_rounds; i++) {
    //   Population population = new Population(size);
    //   for(int j=0; j<generations; j++) {
    //     System.out.println(population.getMinDistance());
    //     population = getNextGenerationTS(population);
    //   }
    //   // System.out.println(population.getMinDistance());
    //   if(population.getMinDistance() < cost) {
    //     cost = population.getMinDistance();
    //     best = population;
    //   }
    // }

    return best.getChromosomes()[0].getGenes();
  }
}
