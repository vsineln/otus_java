package ru.otus.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView error(Exception ex) {
        logger.info("Exception {}", ex.getMessage());

        ModelAndView mv = new ModelAndView();
        mv.addObject("message", "Page is not found");
        mv.setViewName("error");

        return mv;
    }
}
