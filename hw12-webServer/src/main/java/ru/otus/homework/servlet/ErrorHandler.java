package ru.otus.homework.servlet;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import ru.otus.homework.services.TemplateProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static ru.otus.homework.util.Constants.TXT_HTML;


public class ErrorHandler extends ErrorPageErrorHandler {
    private final TemplateProcessor templateProcessor;

    public ErrorHandler(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void generateAcceptableResponse(Request baseRequest, HttpServletRequest request, HttpServletResponse response, int code, String message, String mimeType)
            throws IOException {
        response.setContentType(TXT_HTML);
        response.getWriter().println(templateProcessor.getPage("error.html", Map.of("message", message)));
    }
}
