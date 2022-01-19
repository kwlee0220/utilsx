package utils.react;

import io.reactivex.Observable;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public interface ProgressReporter<P> {
	public Observable<P> getProgressObservable();
}
