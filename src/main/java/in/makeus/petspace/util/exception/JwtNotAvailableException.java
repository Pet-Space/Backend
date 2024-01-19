package in.makeus.petspace.util.exception;

import in.makeus.petspace.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class JwtNotAvailableException extends RuntimeException {

    private final BaseResponseStatus status;

    public JwtNotAvailableException(BaseResponseStatus status) {
        this.status = status;
    }
}
