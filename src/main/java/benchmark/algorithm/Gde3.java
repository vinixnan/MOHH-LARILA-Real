package benchmark.algorithm;

import interfaces.LLHInterface;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author vinicius
 */
public class Gde3<S extends Solution<?>> extends GDE3 implements LLHInterface<S> {

    /**
     * Low-Level Heuristic Selector.
     */
    protected int maxIterations;
    protected int iterations;

    protected List<DoubleSolution> offspringPopulation;

    public Gde3(DoubleProblem problem, int populationSize, int maxEvaluations, DifferentialEvolutionSelection selection, DifferentialEvolutionCrossover crossover, SolutionListEvaluator<DoubleSolution> evaluator) {
        super(problem, populationSize, maxEvaluations, selection, crossover, evaluator);
        maxIterations = maxEvaluations / populationSize;
        iterations = 0;
    }

    @Override
    public int getMaxEvaluations() {
        return this.maxEvaluations;
    }

    @Override
    public int getIterations() {
        return this.iterations;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public int getPopulationSize() {
        return super.getMaxPopulationSize();
    }

    @Override
    public void setPopulationSize(int populationSize) {
        super.setMaxPopulationSize(populationSize);
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    @Override
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return iterations >= maxIterations || evaluations >= maxEvaluations;
    }

    @Override
    public void setCrossoverOperator(CrossoverOperator<S> co) {
        this.crossoverOperator = (DifferentialEvolutionCrossover) co;
    }

    @Override
    public void setMutationOperator(MutationOperator<S> mo) {
        //nothing to do here
    }

    @Override
    public void initMetaheuristic() {
        population = createInitialPopulation();
        population = evaluatePopulation(population);
        initProgress();
    }

    @Override
    public void initMetaheuristic(List pop) {
        population = pop;
        population = evaluatePopulation(population);
        initProgress();
    }

    @Override
    public void run() {
        this.initMetaheuristic();
        this.execute();
    }

    @Override
    protected void updateProgress() {
        super.updateProgress();
        iterations++;
    }

    @Override
    public List<S> execute() {
        /*This is necessary because of GDE3 impossibility for generating solutions with less than 4 parent solutions. Just used in HHs*/
        while (population.size() < getMaxPopulationSize() || population.size() < 4) {
            population.add(population.get(JMetalRandom.getInstance().nextInt(0, population.size() - 1)));
        }
        while (!isStoppingConditionReached()) {
            this.executeMethod();
            population = replacement(population, offspringPopulation);
            updateProgress();
        }
        return (List<S>) population;
    }

    @Override
    public List<DoubleSolution> executeMethod() {
        List<DoubleSolution> matingPopulation = selection(population);
        offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        return offspringPopulation;
    }

    @Override
    public List getPopulation() {
        return population;
    }

    @Override
    public void setPopulation(List population) {
        this.population = population;
    }

    public List<DoubleSolution> getOffspringPopulation() {
        return offspringPopulation;
    }

    public void setOffspringPopulation(List<DoubleSolution> offspringPopulation) {
        this.offspringPopulation = offspringPopulation;
    }

    @Override
    protected void initProgress() {
        super.initProgress();
        iterations = 1;
    }

    @Override
    public List<S> execute(List<S> inputPop, int evaluations) {
        population = (List<DoubleSolution>) inputPop;
        maxIterations = evaluations / getMaxPopulationSize();
        this.iterations = 0;
        return execute();
    }

    @Override
    public String getName() {
        return "myGDE3";
    }
}
