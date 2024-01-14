package in.makeus.petspace.util.exception;

import in.makeus.petspace.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException{

    private final BaseResponseStatus status;

    public ReviewException(BaseResponseStatus status) {
        this.status = status;
    }
}
