import java.util.Arrays;
import java.util.Comparator;

public class Population {
  private Chromosome[] population;

  private double avgDistance = 0.0;

  private double minDistance = 0.0;

  private double maxDistance = Double.MAX_VALUE;

  private Integer[] roulette_wheel;

  public Population(Chromosome[] larger_population, int size) {
    Arrays.sort(larger_population, new Comparator<Chromosome>() {
        @Override
        public int compare(Chromosome c1, Chromosome c2) {
            return c1.getDistance().compareTo(c2.getDistance());
        }
    });
    Chromosome[] population = new Chromosome[size];
    System.arraycopy(larger_population, 0, population, 0, size);
    this.population = population;
    this.avgDistance = computeAverageDistance(this.population);
    this.minDistance = population[0].getDistance();
    this.maxDistance = population[population.length-1].getDistance();
    this.computeFitness(population);
  }

  public Population(Chromosome[] population) {
    Arrays.sort(population, new Comparator<Chromosome>() {
        @Override
        public int compare(Chromosome c1, Chromosome c2) {
            return c1.getDistance().compareTo(c2.getDistance());
        }
    });
    this.population = population;
    this.avgDistance = computeAverageDistance(this.population);
    this.minDistance = population[0].getDistance();
    this.maxDistance = population[population.length-1].getDistance();
    this.computeFitness(population);
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

  private void computeFitness(Chromosome[] population) {
    double total = 0.0;
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for(int i=0; i<population.length; i++) {
      total += population[i].getDistance();
    }
    for(int i=0; i<population.length; i++) {
      population[i].setFitness(total / population[i].getDistance());
      if(min > population[i].getFitness()) {
        min = population[i].getFitness();
      }
      if(max < population[i].getFitness()) {
        max = population[i].getFitness();
      }
    }
    int total_cells = 0;
    for(int i=0; i<population.length; i++) {
      population[i].setFitness((population[i].getFitness() - min)/(max-min));
      population[i].setCells((int)(population[i].getFitness() * population.length));
      if(population[i].getCells() == 0) {
        population[i].setCells(1);
      }
      total_cells += population[i].getCells();
    }

    this.roulette_wheel = new Integer[total_cells];
    int k = 0;
    for(int i=0; i<population.length; i++) {
      for(int j=0; j<population[i].getCells(); j++) {
        this.roulette_wheel[k++] = i;
      }
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

  public Integer[] getRouletteWheel() {
    return this.roulette_wheel;
  }
}
