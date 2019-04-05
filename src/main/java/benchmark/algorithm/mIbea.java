package benchmark.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

/**
 * This class extends the algorithm from jMetal and implements operations
 * necessary for algorithm transitions.
 */
/**
 * The type Ibea.
 *
 * @param <S> jMetal need.
 */
@SuppressWarnings("serial")
public class mIbea<S extends Solution<?>> extends Ibea<S> {

    public mIbea(Problem<S> problem, int populationSize, int archiveSize, int maxEvaluations, SelectionOperator<List<S>, S> selectionOperator, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator) {
        super(problem, populationSize, archiveSize, maxEvaluations, selectionOperator, crossoverOperator, mutationOperator);
    }

    /**
     * Selection list.
     *
     * @param population the population
     * @return the list
     */
    @Override
    protected List<S> selection(List<S> population) {
        List<S> union = new ArrayList<>();
        union.addAll(population);
        union.addAll(archive);
        union = SolutionListUtils.getNondominatedSolutions(union);//modificated part
        calculateFitness(union);
        archive = union;
        while (archive.size() > populationSize) {
            removeWorst(archive);
        }
        return archive;
    }

    @Override
    public String getName() {
        return "mIBEA";
    }

    @Override
    public String getDescription() {
        return "Modificated Indicator based Evolutionary Algorithm";
    }
}
