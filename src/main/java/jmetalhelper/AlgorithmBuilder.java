package jmetalhelper;

import benchmark.algorithm.Gde3;
import benchmark.algorithm.Ibea;
import benchmark.algorithm.Nsgaii;
import benchmark.algorithm.Spea2;
import benchmark.algorithm.mIbea;
import interfaces.LLHInterface;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMException;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

/**
 * This class builds algorithms.
 */
/**
 * The type Algorithm builder.
 *
 * @param <S> jMetal need.
 */
public class AlgorithmBuilder<S extends Solution<?>> {

    /**
     * The Problem.
     */
    protected Problem problem;

    protected final int k = 1;

    public AlgorithmBuilder(Problem problem) {
        this.problem = problem;
    }

    /**
     * Generate cross crossover operator.
     *
     * @param configParams the config params
     * @return the crossover operator
     * @throws JMException the jm exception
     */
    public CrossoverOperator generateCross(ParametersforHeuristics configParams) throws JMException {
        Operator operator = null;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("probability", configParams.getCrossoverProbality());
        if ("sbxCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            parameters.put("distributionIndex", configParams.getCrossoverDistribution());
            operator = new SBXCrossover(configParams.getCrossoverProbality(), configParams.getCrossoverDistribution());
        } else if ("DifferentialEvolutionCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            parameters.put("cr", configParams.getDeCr());
            parameters.put("f", configParams.getDeF());
            parameters.put("k", configParams.getDeK());
            parameters.put("variant", configParams.getDeVariant());
            operator = new DifferentialEvolutionCrossover(configParams.getDeCr(), configParams.getDeF(), configParams.getDeK(), configParams.getDeVariant());
        }
        return (CrossoverOperator) operator;
    }

    /**
     * Generate muta mutation operator.
     *
     * @param configParams the config params
     * @param maxIterations the max iterations
     * @return the mutation operator
     * @throws JMException the jm exception
     */
    public MutationOperator generateMuta(ParametersforHeuristics configParams, int maxIterations)
            throws JMException {
        Operator operator = null;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("probability", configParams.getMutationProbability());
        if ("polynomialMutation".equalsIgnoreCase(configParams.getMutationName())) {
            parameters.put("distributionIndex", configParams.getMutationDistribution());
            operator = new PolynomialMutation(configParams.getMutationProbability(), configParams.getMutationDistribution());
        }
        return (MutationOperator) operator;
    }

    public Comparator generateComparator() {
        return new DominanceComparator<>();
    }

    /**
     * Generate selection selection operator.
     *
     * @return the selection operator
     * @throws JMException the jm exception
     */
    public SelectionOperator generateSelection() throws JMException {
        SelectionOperator se = new BinaryTournamentSelection();
        return se;
    }

    /**
     * Create ibea standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createMIbea(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        LLHInterface algorithm = new mIbea(problem, configAlg.getPopulationSize(),
                configAlg.getArchiveSize(),
                configAlg.getMaxIteractions() * configAlg.getPopulationSize(),
                selection, crossover, mutation);
        return algorithm;
    }

    /**
     * Create ibea standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createIbea(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        LLHInterface algorithm = new Ibea(problem, configAlg.getPopulationSize(),
                configAlg.getArchiveSize(),
                configAlg.getMaxIteractions() * configAlg.getPopulationSize(),
                selection, crossover, mutation);
        return algorithm;
    }

    /**
     * Create nsga-ii standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createNsgaii(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        Comparator comparator = this.generateComparator();

        int matingPoolSize = configAlg.getPopulationSize();
        int offspringPopulationSize = configAlg.getPopulationSize();

        LLHInterface algorithm = new Nsgaii(problem,
                configAlg.getMaxIteractions() * configAlg.getPopulationSize(),
                configAlg.getPopulationSize(), matingPoolSize, offspringPopulationSize, crossover,
                mutation, selection, comparator, new SequentialSolutionListEvaluator());
        return algorithm;
    }

    /**
     * Create spea 2 standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createSpea2(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        LLHInterface algorithm = new Spea2(problem, configAlg.getMaxIteractions(),
                configAlg.getPopulationSize(), crossover,
                mutation, selection, new SequentialSolutionListEvaluator(), k);
        return algorithm;
    }

    /**
     * Create spea 2 standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createGde3(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        configHeuristic.setDeCr(0.2);//TEMP @TODO new parameter file
        configHeuristic.setDeF(0.2);
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        LLHInterface algorithm = new Gde3((DoubleProblem) problem,
                configAlg.getPopulationSize(), configAlg.getMaxIteractions()
                * configAlg.getPopulationSize(), new DifferentialEvolutionSelection(),
                (DifferentialEvolutionCrossover) crossover,
                new SequentialSolutionListEvaluator());
        return algorithm;
    }

    public int getK() {
        return k;
    }

    /**
     * Create standard metaheuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard metaheuristic
     * @throws JMException the jm exception
     * @throws FileNotFoundException the file not found exception
     */
    public LLHInterface create(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException, FileNotFoundException {
        switch (configAlg.getAlgorithmName()) {
            case "Ibea":
                return createIbea(configAlg, configHeuristic);
            case "mIbea":
                return createMIbea(configAlg, configHeuristic);
            case "Nsgaii":
                return this.createNsgaii(configAlg, configHeuristic);
            case "Spea2":
                return createSpea2(configAlg, configHeuristic);
            case "Gde3":
                return createGde3(configAlg, configHeuristic);
            default:
                System.err.println("Algorithm not found");
                return null;
        }
    }
}
