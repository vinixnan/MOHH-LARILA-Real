package helpers;

import interfaces.LLHInterface;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;
import jmetalhelper.AlgorithmBuilder;
import jmetalhelper.ParametersforAlgorithm;
import jmetalhelper.ParametersforHeuristics;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 *
 * @author vinicius
 */
public class AlgorithmCreator {

    protected final ParametersforAlgorithm pNsgaii = new ParametersforAlgorithm("NSGAII.default");
    protected final ParametersforAlgorithm pSpea2 = new ParametersforAlgorithm("SPEA2.default");
    protected final ParametersforAlgorithm pmIBEA = new ParametersforAlgorithm("mIBEA.default");
    protected final ParametersforAlgorithm pIBEA = new ParametersforAlgorithm("IBEA.default");
    protected final ParametersforAlgorithm pGde3 = new ParametersforAlgorithm("GDE3.default");
    protected Problem problem;
    protected AlgorithmBuilder ab;
    protected ParametersforHeuristics pHeu;
    protected ParametersforHeuristics pHeuGDE3;

    public AlgorithmCreator(Problem problem) {
        this.problem = problem;
        ab = new AlgorithmBuilder(problem);
        try {
            pHeu = new ParametersforHeuristics("SBX.Poly.default", problem.getNumberOfVariables());
            pHeuGDE3 = new ParametersforHeuristics("DE.Poly.default", problem.getNumberOfVariables());
        } catch (ConfigurationException ex) {
            Logger.getLogger(AlgorithmCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LLHInterface create(int chosenHeuristic, int eval) throws JMException {
        if (eval != Integer.MAX_VALUE) {
            System.out.println("Create " + chosenHeuristic + " at " + eval);
        }
        switch (chosenHeuristic) {
            case 0:
                return ab.createNsgaii(pNsgaii, pHeu);
            case 1:
                return ab.createSpea2(pSpea2, pHeu);
            case 2:
                return ab.createIbea(pIBEA, pHeu);
            case 3:
                return ab.createGde3(pGde3, pHeuGDE3);
            case 4:
                return ab.createMIbea(pmIBEA, pHeu);
            default:
                return null;
        }
    }

    public LLHInterface create(int chosenHeuristic) throws JMException {
        return create(chosenHeuristic, Integer.MAX_VALUE);
    }

    public Algorithm createJMetalAlg(int chosenHeuristic) throws JMException {

        ParametersforHeuristics configHeuristic = null;
        ParametersforAlgorithm configAlg = null;
        Algorithm algorithm = null;

        switch (chosenHeuristic) {
            case 0:
                configAlg = pNsgaii;
                configHeuristic = pHeu;
                break;
            case 1:
                configAlg = pSpea2;
                configHeuristic = pHeu;
                break;
            case 2:
                configAlg = pIBEA;
                configHeuristic = pHeu;
                break;
            case 3:
                configAlg = pGde3;
                configHeuristic = pHeuGDE3;
                configHeuristic.setDeCr(0.2);//TEMP @TODO new parameter file
                configHeuristic.setDeF(0.2);
                break;
            case 4:
                configAlg = pmIBEA;
                configHeuristic = pHeu;
                break;
        }

        SelectionOperator selection = ab.generateSelection();
        CrossoverOperator crossover = ab.generateCross(configHeuristic);
        MutationOperator mutation = ab.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        Comparator comparator = ab.generateComparator();

        int matingPoolSize = configAlg.getPopulationSize();
        int offspringPopulationSize = configAlg.getPopulationSize();
        int k = ab.getK();

        switch (chosenHeuristic) {
            case 0:
                algorithm = new NSGAII(problem, configAlg.getMaxEvaluations(), configAlg.getPopulationSize(), matingPoolSize, offspringPopulationSize, crossover,
                        mutation, selection, comparator, new SequentialSolutionListEvaluator());
                break;
            case 1:
                algorithm = new SPEA2(problem, configAlg.getMaxIteractions(),
                        configAlg.getPopulationSize(), crossover,
                        mutation, selection, new SequentialSolutionListEvaluator(), k);
                break;
            case 2:
                algorithm = new IBEA(problem, configAlg.getPopulationSize(),
                        configAlg.getArchiveSize(),
                        configAlg.getMaxEvaluations(),
                        selection, crossover, mutation);
                break;
            case 3:
                algorithm = new GDE3((DoubleProblem) problem,
                        configAlg.getPopulationSize(), configAlg.getMaxEvaluations(), new DifferentialEvolutionSelection(),
                        (DifferentialEvolutionCrossover) crossover,
                        new SequentialSolutionListEvaluator());
                break;
        }
        return algorithm;
    }

    public void setMaxEvaluationsAndPopulation(int evaluations, int populationSize) {
        int iterations = evaluations / populationSize;
        System.out.println("Set iterations=" + iterations + " popSize=" + populationSize);
        pGde3.setMaxIteractions(iterations);
        pGde3.setArchiveSize(populationSize);
        pGde3.setPopulationSize(populationSize);
        pNsgaii.setMaxIteractions(iterations);
        pNsgaii.setArchiveSize(populationSize);
        pNsgaii.setPopulationSize(populationSize);
        pSpea2.setMaxIteractions(iterations);
        pSpea2.setArchiveSize(populationSize);
        pSpea2.setPopulationSize(populationSize);
        pIBEA.setMaxIteractions(iterations);
        pIBEA.setArchiveSize(populationSize);
        pIBEA.setPopulationSize(populationSize);
        pmIBEA.setMaxIteractions(iterations);
        pmIBEA.setArchiveSize(populationSize);
        pmIBEA.setPopulationSize(populationSize);
    }
}
