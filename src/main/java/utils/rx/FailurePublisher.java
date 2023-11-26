package utils.rx;

import utils.func.FailureCase;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class FailurePublisher<T> extends Observable<FailureCase<T>>
										implements Observer<FailureCase<T>> {
	private final Subject<FailureCase<T>> m_subject = PublishSubject.create();

	@Override
	protected void subscribeActual(Observer<? super FailureCase<T>> observer) {
		m_subject.subscribe(observer);
	}

	@Override
	public void onSubscribe(Disposable d) {
		m_subject.onSubscribe(d);
	}

	@Override
	public void onNext(FailureCase<T> t) {
		m_subject.onNext(t);
	}

	@Override
	public void onError(Throwable e) {
		m_subject.onError(e);
	}

	@Override
	public void onComplete() {
		m_subject.onComplete();
	}
}