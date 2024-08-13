package camp.woowak.lab.common.exception;

public interface ErrorCode {
	int getStatus();

	String getErrorCode();

	String getMessage();
}
