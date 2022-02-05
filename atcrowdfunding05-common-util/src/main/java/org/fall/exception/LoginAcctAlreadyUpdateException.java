package org.fall.exception;

/**
 *  更新Admin如果有相同的账户，则抛出这个异常
 */
public class LoginAcctAlreadyUpdateException extends RuntimeException{
    public LoginAcctAlreadyUpdateException() {
    }

    public LoginAcctAlreadyUpdateException(String message) {
        super(message);
    }

    public LoginAcctAlreadyUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginAcctAlreadyUpdateException(Throwable cause) {
        super(cause);
    }

    public LoginAcctAlreadyUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
