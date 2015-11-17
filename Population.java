import java.util.Arrays;
import java.util.Comparator;

public class Population {
  private Chromosome[] population;

  private double avgDistance = 0.0;

  private double minDistance = 0.0;

  private double maxDistance = Double.MAX_VALUE;

  public Population(Chromosome[] population) {
    this.population = population;
    this.avgDistance = computeAverageDistance(this.population);
    this.minDistance = population[0].getDistance();
    this.maxDistance = population[population.length-1].getDistance();
    computeFitness(population);
  }

  public Population(int n) {
    this(getInitialPopulation(n));
  }

  /**
   * Get initial population.
   * int n - size of population
   */
  private static Chromosome[] getInitialPopulation(int n) {
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

  private static void computeFitness(Chromosome[] population) {
    double total = 0.0;
    for(int i=0; i<population.length; i++) {
      total += population[i].getDistance();
    }
    for(int i=0; i<population.length; i++) {
      population[i].setFitness(population[i].getDistance()/total);
    }
  }

  /**
   * Get average fitness level.
   */
  public double computeAverageDistance(Chromosome[] population) {
    double sum = 0.0;
    for(int i=0; i<population.length; i++) {
      sum += population[i].getDistance();
    }
    return (double)sum/population.length;
  }

  public Chromosome[] getChromosomes() {
    return this.population;
  }

  public double getAvgDistance() {
    return this.avgDistance;
  }

  public double getMinDistance() {
    return this.minDistance;
  }

  public double getMaxDistance() {
    return this.maxDistance;
  }
}
