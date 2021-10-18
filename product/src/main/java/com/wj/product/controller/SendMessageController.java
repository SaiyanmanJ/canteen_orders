//package com.wj.product.controller;
//
//import com.wj.product.entity.Product;
//import com.wj.product.message.StreamClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
///**
// * @author Wang Jing
// * @time 2021/10/15 21:39
// */
//@RestController
//public class SendMessageController {
//
//    @Autowired
//    private StreamClient streamClient;
//
//    @GetMapping(value = "/sendMessage")
//    public void process(){
//        Product product = new Product(1L, "超级烧麦", new BigDecimal(1.0), "很大哦", 1L, 10000);
////        String message = "now" + new Date();
//        streamClient.output().send(MessageBuilder.withPayload(product).build());
//    }
//}
