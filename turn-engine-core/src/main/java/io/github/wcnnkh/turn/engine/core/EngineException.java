package io.github.wcnnkh.turn.engine.core;

public class EngineException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EngineException() {
		super();
	}

	public EngineException(String message) {
		super(message);
	}

	public EngineException(String message, Throwable cause) {
		super(message, cause);
	}

	public EngineException(Throwable cause) {
		super(cause);
	}
}
