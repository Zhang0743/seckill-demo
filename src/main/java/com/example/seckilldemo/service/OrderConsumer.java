package com.example.seckilldemo.service;

import com.example.seckilldemo.entity.Goods;
import com.example.seckilldemo.entity.Order;
import com.example.seckilldemo.entity.SeckillGoods;
import com.example.seckilldemo.mapper.GoodsMapper;
import com.example.seckilldemo.mapper.OrderMapper;
import com.example.seckilldemo.mapper.SeckillGoodsMapper;
import com.example.seckilldemo.message.SeckillMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderConsumer {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @RabbitListener(queues = "seckill.order.queue")
    public void handle(SeckillMessage msg) {
        Goods goods = goodsMapper.selectById(msg.getGoodsId());
        SeckillGoods seckillGoods = seckillGoodsMapper.selectById(msg.getGoodsId());

        if (goods == null) return;

        Order order = new Order();
        order.setUserId(msg.getUserId());
        order.setGoodsId(msg.getGoodsId());
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods != null ? seckillGoods.getSeckillPrice() : goods.getGoodsPrice());
        order.setStatus(0);
        order.setCreateDate(new Date());

        orderMapper.insert(order);
        System.out.println("📦 订单已创建: 用户=" + msg.getUserId() + ", 商品=" + goods.getGoodsName());
    }
}