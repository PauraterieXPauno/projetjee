package fr.cytech.bde.web.mvc;

import fr.cytech.bde.service.exception.BusinessException;
import fr.cytech.bde.service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@ControllerAdvice(basePackages = "fr.cytech.bde.web.mvc")
public class MvcExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView ressourceIntrouvable(ResourceNotFoundException exception) {
        return new ModelAndView("error/404", Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView erreurMetier(BusinessException exception) {
        return new ModelAndView("error/business", Map.of("message", exception.getMessage()));
    }
}
