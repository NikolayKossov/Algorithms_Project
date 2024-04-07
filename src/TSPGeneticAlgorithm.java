import java.util.*;
import java.util.stream.IntStream;

class City {
    int x, y;

    public City(int x, int y) {
        this.x = (int) (Math.random() * 50) + 1;
        this.y = (int) (Math.random() * 50) + 1;
    }

    public double distanceTo(City city) {
        int xDistance = Math.abs(this.x - city.x);
        int yDistance = Math.abs(this.y - city.y);
        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }
}

class Tour {
    private ArrayList<City> cities = new ArrayList<>();
    private double fitness = 0;
    private double distance = 0;

    public Tour(ArrayList<City> cities) {
        this.cities.addAll(cities);
        Collections.shuffle(this.cities);
    }

    public double calculateFitness() {
        if (fitness == 0) {
            fitness = 1 / calculateTotalDistance();
        }
        return fitness;
    }

    public double calculateTotalDistance() {
        if (distance == 0) {
            for (int cityIndex = 0; cityIndex < this.cities.size() - 1; cityIndex++) {
                distance += this.cities.get(cityIndex).distanceTo(this.cities.get(cityIndex + 1));
            }
            distance += this.cities.get(this.cities.size() - 1).distanceTo(this.cities.get(0));
        }
        return distance;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public String toString() {
        String geneString = "|";
        for (City city : cities) {
            geneString += city.x + "," + city.y + "|";
        }
        return geneString;
    }
}

class Population {
    Tour[] tours;

    public Population(int populationSize, ArrayList<City> cities) {
        tours = new Tour[populationSize];
        for (int i = 0; i < populationSize; i++) {
            tours[i] = new Tour(new ArrayList<>(cities));
        }
    }

    public Tour getFittest() {
        return Collections.min(List.of(tours), Comparator.comparing(Tour::calculateFitness));
    }
}

class GeneticAlgorithm {
    private static final double mutationRate = 0.01;
    private static final int tournamentSize = 5;
    private static final Random rand = new Random();

    public static Population evolve(Population pop) {
        Population newPopulation = new Population(pop.tours.length, pop.getFittest().getCities());

        newPopulation.tours[0] = pop.getFittest();

        IntStream.range(1, pop.tours.length).forEach(i -> {
            Tour parent1 = tournamentSelection(pop);
            Tour parent2 = tournamentSelection(pop);
            Tour child = crossover(parent1, parent2);
            newPopulation.tours[i] = child;
        });

        for (int i = 1; i < newPopulation.tours.length; i++) {
            mutate(newPopulation.tours[i]);
        }

        return newPopulation;
    }

    private static Tour crossover(Tour parent1, Tour parent2) {
        ArrayList<City> childCities = new ArrayList<>(parent1.getCities().subList(0, parent1.getCities().size() / 2));
        parent2.getCities().stream().filter(city -> !childCities.contains(city)).forEach(childCities::add);
        return new Tour(childCities);
    }

    private static void mutate(Tour tour) {
        for (int tourPos1 = 0; tourPos1 < tour.getCities().size(); tourPos1++) {
            if (Math.random() < mutationRate) {
                int tourPos2 = (int) (tour.getCities().size() * Math.random());

                City city1 = tour.getCities().get(tourPos1);
                City city2 = tour.getCities().get(tourPos2);

                tour.getCities().set(tourPos2, city1);
                tour.getCities().set(tourPos1, city2);
            }
        }
    }

    private static Tour tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, pop.getFittest().getCities());
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = rand.nextInt(pop.tours.length);
            tournament.tours[i] = pop.tours[randomId];
        }
        return tournament.getFittest();
    }
}

public class TSPGeneticAlgorithm {
    public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            ArrayList<City> cities = new ArrayList<>();

            System.out.println("Enter the number of cities: ");
            int numberOfCities = scanner.nextInt();

            for (int i = 0; i < numberOfCities; i++) {
                System.out.println("Enter x coordinate for city " + (i + 1) + ": ");
                int x = scanner.nextInt();

                System.out.println("Enter y coordinate for city " + (i + 1) + ": ");
                int y = scanner.nextInt();

                cities.add(new City(x, y));
            }

        Population pop = new Population(100, cities);
        System.out.println("Initial distance: " + pop.getFittest().calculateTotalDistance());

        pop = GeneticAlgorithm.evolve(pop);
        for (int i = 0; i < 100; i++) {
            pop = GeneticAlgorithm.evolve(pop);
        }

        System.out.println("Finished");
        System.out.println("Final distance: " + pop.getFittest().calculateTotalDistance());
        System.out.println("Solution:");
        System.out.println(pop.getFittest());
    }
}