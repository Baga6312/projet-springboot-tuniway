package com.tuniway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     * Forward all non-API routes to index.html for Angular SPA routing
     */
    @GetMapping({"/", "/{x:[\\w\\-]+}", "/{x:^(?!api).*$}/**"})
    public String index() {
        return "forward:/index.html";
    }
}
