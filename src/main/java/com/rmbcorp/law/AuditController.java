package com.rmbcorp.law;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditController {

    @RequestMapping(value = "/audit", method = RequestMethod.GET, produces = "text/plain")
    public String index() {
        return Auditor.getInstance().getLog() + "\n";
    }
}
