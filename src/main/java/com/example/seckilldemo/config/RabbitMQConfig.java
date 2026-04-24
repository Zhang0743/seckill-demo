package com.example.seckilldemo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue seckillOrderQueue() {
        return new Queue("seckill.order.queue", true);
    }

    @Bean
    public TopicExchange seckillExchange() {
        return new TopicExchange("seckill.exchange");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(seckillOrderQueue())
                .to(seckillExchange())
                .with("seckill.order");
    }
}