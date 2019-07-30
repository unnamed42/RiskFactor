package com.tjh.riskfactor.controllers;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default JSON for error rather than normal HttpUtils error pages
 */
@RestController
@RequestMapping("/error")
@RequiredArgsConstructor
public class ErrorJsonController implements ErrorController {

    @Value("${debug}")
    private boolean includeStacktrace;

    private final ErrorAttributes errorAttributes;

    @RequestMapping("/")
    Map<String, Object> error(HttpServletRequest req, HttpServletResponse resp) {
        val attrs = errorAttributes.getErrorAttributes(new ServletWebRequest(req), includeStacktrace);
        val trace = (String)attrs.get("trace");
        if(trace != null)
            attrs.put("trace", trace.split("\n\t"));
        return attrs;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
