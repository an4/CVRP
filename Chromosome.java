import java.util.Arrays;
import java.util.Collections;

class Chromosome {
  final static int DIMENSION = Data.DIMENSION;

  private Integer[] genes = new Integer[DIMENSION];
  private Double distance = 0.0;
  private Double fitness = null;

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

  static double computeDistance(Integer[] genes) {
    double distance = 0.0;
    for(int i=1; i<genes.length; i++) {
      distance += Data.EDM[genes[i]][genes[i-1]];
    }
    return distance + Data.EDM[genes[genes.length-1]][0];
  }

  static Integer[] getPermutation() {
    Integer[] array = new Integer[DIMENSION];
    for(int i=0; i<DIMENSION; i++) {
      array[i] = i;
    }
    Collections.shuffle(Arrays.asList(array));
    if(array[0] != 0) {
      Solution.swapPosition(0, array[0], array);
    }
    return array;
  }
}
