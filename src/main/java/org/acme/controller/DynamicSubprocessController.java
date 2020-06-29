package org.acme.controller;

import org.acme.model.ProcessExchangeModel;
import org.acme.model.Product;
import org.acme.service.DynamicSubprocessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynamicSubprocessController {
    private final DynamicSubprocessService dynamicSubprocessService;

    @Autowired
    public DynamicSubprocessController(DynamicSubprocessService dynamicSubprocessService) {
        this.dynamicSubprocessService = dynamicSubprocessService;
    }

    @RequestMapping(value = "/getProcess", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ProcessExchangeModel getProductProcess(@RequestBody Product product) {
        return dynamicSubprocessService.getSubprocess(product);
    }

}