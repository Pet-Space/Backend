package in.makeus.petspace.util.exception;

import in.makeus.petspace.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class RoomException extends RuntimeException {

    private final BaseResponseStatus status;

    public RoomException(BaseResponseStatus status) {
        this.status = status;
    }

}
