package com.example.seckilldemo.controller;

import com.example.seckilldemo.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @PostMapping("/{goodsId}")
    public String doSeckill(@RequestParam Long userId, @PathVariable Long goodsId) {
        return seckillService.doSeckill(userId, goodsId);
    }

    @GetMapping("/goods")
    public String info() {
        return "秒杀商品: iPhone 15, 秒杀价: ¥3999, 库存: 10台";
    }
}