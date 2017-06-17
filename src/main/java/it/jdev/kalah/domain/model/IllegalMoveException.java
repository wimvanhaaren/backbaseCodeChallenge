package it.jdev.kalah.domain.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Illegal move")
public class IllegalMoveException extends RuntimeException {

	private static final long serialVersionUID = -2561739503648193622L;
	
	public IllegalMoveException() {}
	
	public IllegalMoveException(String message) {
		super(message);
	}


}
