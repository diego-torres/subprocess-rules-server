package org.acme;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.drools.core.SessionConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @Scope("prototype")
    public KieContainer kieContainer() throws Exception {
        log.debug("Building a new kieContainer");
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        URL oracle = new URL("https://raw.githubusercontent.com/diego-torres/dynamic-subprocess-rules-kjar/master/src/main/resources/com/myspace/dynamic_subprocess_rules_kjar/process-by-product.drl");
        URLConnection yc = oracle.openConnection();
        kfs.write(ResourceFactory.newInputStreamResource(yc.getInputStream()));
        kieServices.newKieBuilder(kfs).buildAll();
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    @Bean
    public TargetSource commonsPool2TargetSource() {
        long minEvictionTime = 1000 * 60 * 5; // 2 minutes
        long eviction = 1000 * 60 * 10; // 5 minutes
        CommonsPool2TargetSource cpool = new CommonsPool2TargetSource();
        cpool.setTargetBeanName("kieContainer");
        cpool.setMaxSize(10);
        cpool.setMinEvictableIdleTimeMillis(minEvictionTime);
        cpool.setTimeBetweenEvictionRunsMillis(eviction);
        return cpool;
    }

    @Bean
	public ProxyFactoryBean proxyFactoryBean() {
		ProxyFactoryBean p = new ProxyFactoryBean();
		p.setTargetSource(commonsPool2TargetSource());
		return p;
	}
}
