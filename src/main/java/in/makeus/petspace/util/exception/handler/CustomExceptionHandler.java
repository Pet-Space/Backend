package in.makeus.petspace.util.exception.handler;

import in.makeus.petspace.util.BaseResponse;
import in.makeus.petspace.util.exception.ReservationException;
import in.makeus.petspace.util.exception.AwsException;
import in.makeus.petspace.util.exception.ReissueException;
import in.makeus.petspace.util.exception.ReviewException;
import in.makeus.petspace.util.exception.RoomException;
import in.makeus.petspace.util.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;

import static in.makeus.petspace.util.BaseResponseStatus.EMPTY_REQUEST_PARAMETER;
import static in.makeus.petspace.util.BaseResponseStatus.METHOD_ARGUMENT_TYPE_MISMATCH;
import static in.makeus.petspace.util.BaseResponseStatus.INVALID_REQUEST;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public BaseResponse<Object> handleValidationException() {
        return new BaseResponse<>(INVALID_REQUEST);
    }

    @ExceptionHandler({DateTimeParseException.class})
    public BaseResponse<Object> handleDateTimeException() {
        return new BaseResponse<>(INVALID_REQUEST);
    }

    @ExceptionHandler({UserException.class})
    public BaseResponse<Object> handleUserException(UserException e) {
        return new BaseResponse<>(e.getStatus());
    }

    @ExceptionHandler({ReviewException.class})
    public BaseResponse<Object> handleUserException(ReviewException e) {
        return new BaseResponse<>(e.getStatus());
    }

    @ExceptionHandler({AwsException.class})
    public BaseResponse<Object> handleUserException(AwsException e) {
        return new BaseResponse<>(e.getStatus());
    }

    @ExceptionHandler({ReservationException.class})
    public BaseResponse<Object> handleUserException(ReservationException e) {
        return new BaseResponse<>(e.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public BaseResponse<Object> handlerRequestParam() {
        return new BaseResponse<>(METHOD_ARGUMENT_TYPE_MISMATCH);
    }

    @ExceptionHandler({RoomException.class})
    public BaseResponse<Object> handleUserException(RoomException e) {
        return new BaseResponse<>(e.getStatus());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public BaseResponse<Object> handleRequestParameter() {
        return new BaseResponse<>(EMPTY_REQUEST_PARAMETER);
    }
    @ExceptionHandler(ReissueException.class)
    public BaseResponse<Object> handleRefreshTokenException(ReissueException e) {
        return new BaseResponse<>(e.getStatus());
    }
}
