package org.acme.service;

import org.acme.model.ProcessExchangeModel;
import org.acme.model.Product;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicSubprocessService {
    @Autowired
    private KieContainer kieContainer;

    public ProcessExchangeModel getSubprocess(Product product) {
        ProcessExchangeModel pem = new ProcessExchangeModel("child-b",
                "No subprocess found for product " + product.getName());
        KieSession ks = kieContainer.newKieSession("rulesSession");
        ks.insert(product);
        ks.insert(pem);
        ks.fireAllRules();
        return pem;
    }

}