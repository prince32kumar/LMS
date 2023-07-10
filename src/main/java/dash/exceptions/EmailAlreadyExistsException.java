
package dash.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 5520905786207281118L;

	public EmailAlreadyExistsException(String message) {
		super(message);
	}
}
