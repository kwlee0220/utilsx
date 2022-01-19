package utils.react;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 *
 * @author Kang-Woo Lee
 */
public class RxJavas {
	private RxJavas() {
		throw new AssertionError("Should not be invoked!!: class=" + RxJavas.class.getName());
	}
	
	public static final <T> Observable<T> from(Stream<T> stream) {
		return Observable.create(emitter -> {
			Iterator<T> iter = stream.iterator();
			while ( iter.hasNext() ) {
				if ( emitter.isDisposed() ) {
					return;
				}
				
				emitter.onNext(iter.next());
			}
			emitter.onComplete();
		});
	}
	
	public static Observable<WatchEvent<?>> watchDir(FileSystem fs, Path dir,
													WatchEvent.Kind<?>... events) {
		return Observable.create(new ObservableOnSubscribe<WatchEvent<?>>() {
			@Override
			public void subscribe(ObservableEmitter<WatchEvent<?>> emitter) throws Exception {
				try ( WatchService watch = fs.newWatchService() ) {
					dir.register(watch, events);
					
			        WatchKey key;
					while ( true ) {
						key = watch.take();
						if ( emitter.isDisposed() ) {
							return;
						}
						
						for ( WatchEvent<?> ev : key.pollEvents() ) {
							emitter.onNext(ev);
						}
						key.reset();
					}
				}
				catch ( ClosedWatchServiceException | InterruptedException e ) {
					emitter.onComplete();
				}
				catch ( Throwable e ) {
					emitter.onError(e);
				}
			}
		});
	}
	
	public static Observable<WatchEvent<?>> watchFile(FileSystem fs, Path file,
														WatchEvent.Kind<?>... events) {
		return watchDir(fs, file.getParent(), events)
				.filter(ev -> Files.isSameFile(file, (Path)ev.context()));
	}
	
	public static final Observable<Path> walk(Path start) {
		return Observable.create(new WalkOnSubscribe(start));
	}
	
	private static class WalkOnSubscribe extends SimpleFileVisitor<Path> 
										implements ObservableOnSubscribe<Path> {
		private final Path m_start;
		
		WalkOnSubscribe(Path start) {
			m_start = start;
		}

		@Override
		public void subscribe(ObservableEmitter<Path> emitter) throws Exception {
			try {
				Files.walkFileTree(m_start, new SimpleFileVisitor<Path>() {
					@Override
				    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
				    	throws IOException {
						if ( emitter.isDisposed() ) {
							return FileVisitResult.TERMINATE;
						}
						else {
							emitter.onNext(path);
							return FileVisitResult.CONTINUE;
						}
					}
				});
				emitter.onComplete();
			}
			catch ( Exception e ) {
				emitter.onError(e);
			}
		}
	}
	
	
	public static final Observable<String> lines(Path path, Charset cs) throws IOException {
		return from(Files.lines(path, cs));
	}
	
	public static final Observable<String> lines(Path path) throws IOException {
		return from(Files.lines(path));
	}
}
