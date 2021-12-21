package io.github.wcnnkh.turn.engine.core;

public class TurnEngineException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TurnEngineException() {
		super();
	}

	public TurnEngineException(String message) {
		super(message);
	}

	public TurnEngineException(String message, Throwable cause) {
		super(message, cause);
	}

	public TurnEngineException(Throwable cause) {
		super(cause);
	}
}
