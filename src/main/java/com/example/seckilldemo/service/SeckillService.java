package com.example.seckilldemo.service;

import com.example.seckilldemo.message.SeckillMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private DefaultRedisScript<Long> redisScript;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("seckill.lua")));
        redisScript.setResultType(Long.class);
    }

    public String doSeckill(Long userId, Long goodsId) {
        String stockKey = "seckill:stock:" + goodsId;
        String userKey = "seckill:user:" + userId + ":" + goodsId;

        Long result = redisTemplate.execute(
                redisScript,
                Arrays.asList(stockKey, userKey)
        );

        if (result == null || result == 0) {
            return "❌ 库存不足，秒杀失败！";
        }
        if (result == -1) {
            return "⚠️ 您已参与过本次秒杀，请勿重复操作！";
        }

        // 尝试发送消息到 RabbitMQ
        try {
            SeckillMessage msg = new SeckillMessage(userId, goodsId);
            rabbitTemplate.convertAndSend("seckill.exchange", "seckill.order", msg);
        } catch (Exception e) {
            System.out.println("⚠️ RabbitMQ 不可用，订单将稍后处理");
        }

        return "✅ 秒杀成功！订单正在处理中，请稍后查看。";
    }
}