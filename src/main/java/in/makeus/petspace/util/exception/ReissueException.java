package in.makeus.petspace.util.exception;

import in.makeus.petspace.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ReissueException extends RuntimeException {

    private final BaseResponseStatus status;

    public ReissueException(BaseResponseStatus status) {
        this.status = status;
    }
}
