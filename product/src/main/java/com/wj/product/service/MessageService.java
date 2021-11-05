//package com.wj.product.service;
//
//import org.hibernate.validator.constraints.ru.INN;
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
//
//public interface MessageService<T> {
//
////    String OUTPUT = "output";
//    String DELAYOUTPUT = "delay-output";
//    String INCREASE_STOCK= "increase-stock";
////    @Output(OUTPUT)
////    MessageChannel output();
//
//    @Output(DELAYOUTPUT)
//    MessageChannel delayOutput();
//
//    @Input(INCREASE_STOCK)
//    SubscribableChannel increaseStock();
//}
