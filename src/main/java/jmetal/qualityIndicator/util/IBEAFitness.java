package jmetal.qualityIndicator.util;

import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.IBEA;
import java.util.List;
import org.uma.jmetal.problem.Problem;

/**
 *
 * @author vinicius
 */
public class IBEAFitness {
    
    Problem problem;
    IBEA ibea;
    
    public IBEAFitness(Problem problem) {
        this.problem = problem;
        ibea = new IBEA(problem, 0, 0, 0, null, null, null);
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
