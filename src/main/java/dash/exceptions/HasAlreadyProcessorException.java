
package dash.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HasAlreadyProcessorException extends RuntimeException {

	private static final long serialVersionUID = 5520905786207281118L;

	public HasAlreadyProcessorException(String message) {
		super(message);
	}
}
