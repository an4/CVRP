import java.util.Arrays;
import java.util.Collections;

class Chromosome {
  final static int DIMENSION = Data.DIMENSION;

  private Integer[] genes = new Integer[DIMENSION];
  private Double distance = 0.0;
  private Double fitness = null;
  private Integer cells = 0;

  public Chromosome() {
    this.genes = getPermutation();
    this.distance = computeDistance(this.genes);
  }

  public Chromosome(Integer[] genes) {
    this.genes = Arrays.copyOf(genes, genes.length);
    this.distance = computeDistance(genes);
  }

  /* Total distance of all chromosomes / distance of current chromosome */
  public Double getFitness() {
    return this.fitness;
  }

  public void setFitness(double fitness) {
    this.fitness = fitness;
  }

  public Integer[] getGenes() {
    return this.genes;
  }

  public Double getDistance() {
    return this.distance;
  }

  public void setCells(int n) {
    this.cells = n;
  }

  public Integer getCells() {
    return this.cells;
  }

  public static double computeDistance(Integer[] genes) {
    double distance = 0.0;
    for(int i=1; i<genes.length; i++) {
      distance += Data.EDM[genes[i]][genes[i-1]];
    }
    return distance + Data.EDM[genes[genes.length-1]][0];
  }

  // static double computeDistance(Integer[] genes) {
  //   int i=0;
  //   int capacity = Data.CAPACITY;
  //   double cost = 0.0;
  //   for(i=1; i<DIMENSION; i++) {
  //     if(capacity - Data.demand[genes[i]] > 0) {
  //       capacity -= Data.demand[genes[i]];
  //       cost += Data.EDM[genes[i]][genes[i-1]];
  //     } else {
  //       capacity = Data.CAPACITY - Data.demand[genes[i]];
  //       cost += Data.EDM[genes[i-1]][0];
  //       cost += Data.EDM[0][genes[i]];
  //     }
  //   }
  //   cost += Data.EDM[genes[i-1]][0];
  //   return cost;
  // }

  static Integer[] getPermutation() {
    Integer[] array = new Integer[DIMENSION];
    for(int i=0; i<DIMENSION; i++) {
      array[i] = i;
    }
    Collections.shuffle(Arrays.asList(array));
    if(array[0] != 0) {
      SimpleGA.swapPosition(0, array[0], array);
    }
    return array;
  }
}
