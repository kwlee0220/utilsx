package utils.react;

import static utils.Utilities.checkNotNullArgument;

import java.util.concurrent.CancellationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Completable;
import io.reactivex.Observable;
import utils.async.Execution;
import utils.async.ExecutionProgress;
import utils.func.Unchecked;
import utils.stream.FStream;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class Observables {
	static final Logger s_logger = LoggerFactory.getLogger(Observables.class);
	
	private Observables() {
		throw new AssertionError("Should not be called: class=" + Observables.class);
	}
	
	public static <T> Observable<T> from(FStream<T> stream) {
		return Observable.create(emitter -> {
			try {
				stream.takeWhile(v -> !emitter.isDisposed())
						.forEach(Unchecked.ignore(emitter::onNext));
				if ( !emitter.isDisposed() ) {
					emitter.onComplete();
				}
			}
			catch ( Throwable e ) {
				emitter.onError(e);
			}
		});
	}
	
	/**
	 * 주어진 {@link Observable}객체로부터 FStream 객체를 생성한다.
	 * 
	 * @param <T> Observable에서 반환하는 데이터 타입
	 * @param ob	입력 {@link Observable} 객체.
	 * @return FStream 객체
	 */
	public static <T> FStream<T> from(Observable<? extends T> ob) {
		checkNotNullArgument(ob, "Observable is null");
		
		return new ObservableStream<>(ob);
	}
	
	public static <T> Observable<ExecutionProgress<T>> observeExecution(Execution<T> exec,
																		boolean cancelOnDispose) {
		return Observable.create(new ExecutionProgressReport<T>(exec, cancelOnDispose));
	}
	
	public static Completable fromVoidExecution(Execution<Void> exec) {
		return Completable.create(emitter -> {
			exec.whenFinished(r -> {
				if ( !emitter.isDisposed() ) {
					if ( r.isCompleted() ) {
						emitter.onComplete();
					}
					else if ( r.isFailed() ) {
						emitter.onError(r.getCause());
					}
					else if ( r.isCancelled() ) {
						emitter.onError(new CancellationException());
					}
				}
			});
		});
	}
}
