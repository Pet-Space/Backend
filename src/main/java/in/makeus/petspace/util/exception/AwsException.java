package in.makeus.petspace.util.exception;

import in.makeus.petspace.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class AwsException extends RuntimeException{

    private final BaseResponseStatus status;

    public AwsException(BaseResponseStatus status) {
        this.status = status;
    }
}
