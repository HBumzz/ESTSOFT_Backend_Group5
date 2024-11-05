package com.app.salty.config.globalExeption;

import com.app.salty.config.globalExeption.custom.DuplicateEmailException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.Banner;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

   // private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    protected ModelAndView illegalArgumentException(
            IllegalArgumentException e,
            Model model
    ) {
        model.addAttribute("error", new ErrorResponse("400","요청한 자료를 찾을 수 없습니다."));
        return new ModelAndView("error/error");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(
            Exception e
            , Model model
    ) {
        model.addAttribute("error", new ErrorResponse("500","서버에 문제가 생겼습니다."));
        return new ModelAndView("error/error");  // 500 에러 페이지로 이동
    }

    //409 Conflict
    @ExceptionHandler(DuplicateEmailException.class)
    public ModelAndView handleDuplicateEmailException(
            Exception e
            , Model model
    ){
        model.addAttribute("error", new ErrorResponse("409",e.getMessage()));
        return new ModelAndView("error/error");  // 500 에러 페이지로 이동
    }

    /// ErrorResponse 클래스 (내부 정적 클래스로 정의)
    @Getter @Setter
    public static class ErrorResponse {
        // getters
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ModelAndView httpMessageNotReadableException(Exception e, Model model) {
        model.addAttribute("error", new ErrorResponse("400","잘못된 값이 전달 됐습니다."));
        return new ModelAndView("error/error");
    }
}
