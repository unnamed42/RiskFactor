package com.tjh.riskfactor.controllers;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ErrorJsonController implements ErrorController {

    private class ErrorJson {
        public int status;
        public String error;
        public String reason;
        public String timestamp;
        public String trace;

        ErrorJson(int status, Map<String, Object> attrs) {
            this.status = status;
            this.error = (String)attrs.get("error");
            this.reason = (String)attrs.get("message");
            this.timestamp = attrs.get("timestamp").toString();
            this.trace = (String)attrs.get("trace");
        }
    }

    private static final String PATH = "/error";

    @Value("${debug}")
    private boolean debug;

    private final ErrorAttributes errorAttributes;

    @Autowired
    public ErrorJsonController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(PATH)
    ErrorJson error(HttpServletRequest req, HttpServletResponse resp) {
        val attrs = errorAttributes.getErrorAttributes(new ServletWebRequest(req), debug);
        return new ErrorJson(resp.getStatus(), attrs);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
