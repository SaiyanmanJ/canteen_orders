//package com.wj.order.service;
//
//import org.springframework.cloud.stream.annotation.Input;
//import org.springframework.cloud.stream.annotation.Output;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.SubscribableChannel;
//
///**
// * @author Wang Jing
// * @time 2021/10/15 14:34
// */
//public interface MessageService {
////    String INPUT = "input";
//    String DELAYINPUT = "delay-input";
//    String INCREMENT_STOCK = "increase-stock";
////    @Input(INPUT)
////    SubscribableChannel receiveInput();
//
//    //接收延时队列的消息
//    @Input(DELAYINPUT)
//    SubscribableChannel receiveDelayInput();
//
//    //连接到加库存的队列
//    @Output(INCREMENT_STOCK)
//    MessageChannel increaseStock();
//}
