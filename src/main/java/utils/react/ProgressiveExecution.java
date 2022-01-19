package utils.react;

import utils.async.Execution;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public interface ProgressiveExecution<T,P> extends Execution<T>, ProgressReporter<P> {

}
