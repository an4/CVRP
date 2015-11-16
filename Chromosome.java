import java.util.Arrays;

class Chromosome {
  final static int DIMENSION = 250;

  private Integer[] genes = new Integer[DIMENSION];
  private Double distance = 0.0;
  private Double fitness = 0.0;

  public Chromosome(Integer[] genes, Double distance) {
    this.genes = Arrays.copyOf(genes, genes.length);
    this.distance = distance;
    this.fitness = 1.0/distance;
  }

  public Double getFitness() {
    return this.fitness;
  }

  public Integer[] getGenes() {
    return this.genes;
  }

  public Double getDistance() {
    return this.distance;
  }
}
