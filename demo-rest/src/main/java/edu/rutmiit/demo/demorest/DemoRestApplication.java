package edu.rutmiit.demo.demorest;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        scanBasePackages = {"edu.rutmiit.demo.demorest", "edu.rutmiit.demo.booksapicontract", "edu.rutmiit.demo.events"},
        exclude = {DataSourceAutoConfiguration.class}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class DemoRestApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(DemoRestApplication.class, args);
//    }


    @Autowired
    private RabbitTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DemoRestApplication.class, args);
    }

    @PostConstruct
    public void checkRabbit() {
        System.out.println("=== RabbitTemplate is: " + rabbitTemplate);
        if (rabbitTemplate != null) {
            System.out.println("=== RabbitTemplate is not null! RabbitMQ configured!");
        } else {
            System.out.println("=== RabbitTemplate is NULL! RabbitMQ NOT configured!");
        }
    }
}
