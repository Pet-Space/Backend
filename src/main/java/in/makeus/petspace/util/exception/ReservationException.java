package in.makeus.petspace.util.exception;

import in.makeus.petspace.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException{

    private final BaseResponseStatus status;

    public ReservationException(BaseResponseStatus status) {
        this.status = status;
    }
}
