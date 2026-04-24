package com.example.seckilldemo.config;

import com.example.seckilldemo.entity.SeckillGoods;
import com.example.seckilldemo.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockPreheat implements ApplicationRunner {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void run(ApplicationArguments args) {
        List<SeckillGoods> goodsList = seckillGoodsMapper.selectList(null);
        for (SeckillGoods goods : goodsList) {
            String key = "seckill:stock:" + goods.getGoodsId();
            redisTemplate.opsForValue().set(key, String.valueOf(goods.getStockCount()));
        }
        System.out.println("✅ 库存预热完成，共加载 " + goodsList.size() + " 个秒杀商品");
    }
}