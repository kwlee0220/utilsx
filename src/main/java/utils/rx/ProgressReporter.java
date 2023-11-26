package utils.rx;

import io.reactivex.rxjava3.core.Observable;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public interface ProgressReporter<P> {
	public Observable<P> getProgressObservable();
}
