package jmetal.qualityIndicator.util;

import benchmark.algorithm.Ibea;
import java.util.List;
import org.uma.jmetal.problem.Problem;

/**
 *
 * @author vinicius
 */
public class IBEAFitness {
    
    Problem problem;
    Ibea ibea;
    
    public IBEAFitness(Problem problem) {
        this.problem = problem;
        ibea = new Ibea(problem, 0, 0, 0, null, null, null);
    }
    
    public void calculateFitness(List pop, int max) {
        ibea.setArchiveSize(max);
        ibea.setPopulationSize(max);
        ibea.calculateFitness(pop);
    }
    
    public void removeWorst(List pop) {
        ibea.removeWorst(pop);
    }
}
