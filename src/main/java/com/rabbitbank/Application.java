package com.rabbitbank;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static final String exchange = "replyto.exch";
    public static final String queueName = "g4.rabbit.bank";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    DirectExchange exchange () {
        return new DirectExchange(exchange);
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, true, false, false);
    }

    @Bean
    RabbitAdmin rabbitAdmin (ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setDefaultType(MessageRequest.class);
        converter.setClassMapper(classMapper);
        return converter;
    }

}
