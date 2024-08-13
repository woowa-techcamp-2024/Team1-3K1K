package camp.woowak.lab.store.exception;

// TODO: 404Exception 상속하도록 수정
public class NotFoundStoreException extends RuntimeException {

	public NotFoundStoreException(String message) {
		super(message);
	}

}
