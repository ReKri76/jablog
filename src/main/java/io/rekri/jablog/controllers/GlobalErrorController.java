package io.rekri.jablog.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        WebRequest webRequest = new ServletWebRequest(request);

        Map<String, Object> errors = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)
        );

        Object statusAttr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = 500;
        if (statusAttr != null) {
            try {
                statusCode = Integer.valueOf(statusAttr.toString());
            } catch (NumberFormatException ignored) {}
        }


        String message = (String) errors.get("message");
        String error = (String) errors.get("error");


        model.addAttribute("status", statusCode);
        model.addAttribute("error", error);
        model.addAttribute("message", message);


        if (statusCode == 500) {
            log.error("Internal Server Error occurred. Message: {}", message);
        }

        return "error";
    }
}