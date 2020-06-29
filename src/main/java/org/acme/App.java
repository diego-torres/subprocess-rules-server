package org.acme;

import java.net.URL;
import java.net.URLConnection;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieContainer;
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

    @Bean ("kieContainer")
    @Scope("prototype")
    public KieContainer kieContainer() throws Exception {
        log.debug("Building a new kieContainer");
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        URL ruleUrl = new URL("https://raw.githubusercontent.com/diego-torres/subprocess-rules-server/master/src/main/resources/dynamic-subprocess-rules.drl");
        URLConnection conn = ruleUrl.openConnection();
        kfs.write("src/main/resources/dynamic-subprocess-rules.drl", ResourceFactory.newInputStreamResource(conn.getInputStream()));
        KieBuilder kb = kieServices.newKieBuilder(kfs).buildAll();
        if (kb.getResults().getMessages(Level.ERROR).size() != 0) {
            log.error("Invalid file: {}", kb.getResults().getMessages());
        }
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    @Bean
    public TargetSource commonsPool2TargetSource() {
        long minEvictionTime = 1000 * 60 * 5; // 2 minutes
        long eviction = 1000 * 60 * 10; // 5 minutes
        CommonsPool2TargetSource cpool = new CommonsPool2TargetSource();
        cpool.setTargetBeanName("kieContainer");
        cpool.setMinIdle(2);
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
