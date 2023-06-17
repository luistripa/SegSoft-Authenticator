package api.authenticator.exceptions;

public class AuthenticatorException extends RuntimeException {

    public AuthenticatorException(Exception exception) {
        super(exception);
    }
}
