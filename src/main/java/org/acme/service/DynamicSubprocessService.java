package org.acme.service;

import org.acme.model.ProcessExchangeModel;
import org.acme.model.Product;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DynamicSubprocessService {

    @Autowired
    private ApplicationContext context;

    public ProcessExchangeModel getSubprocess(Product product) {
        ProcessExchangeModel pem = new ProcessExchangeModel("child-b",
                "No subprocess found for product " + product.getName());

        // getting a container from the pool
        KieContainer kc = context.getBean("kieContainer", KieContainer.class);

        KieSession ks = kc.newKieSession();
        ks.insert(product);
        ks.insert(pem);
        ks.fireAllRules();
        return pem;
    }

}