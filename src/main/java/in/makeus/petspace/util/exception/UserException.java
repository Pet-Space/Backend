package in.makeus.petspace.util.exception;

import in.makeus.petspace.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException{

    private final BaseResponseStatus status;

    public UserException(BaseResponseStatus status) {
        this.status = status;
    }
}
