package com.wj.order.thread;

import cn.hutool.core.util.IdUtil;
import com.wj.order.entity.Order;
import com.wj.order.entity.OrderItem;
import com.wj.order.enums.OrderStatusEnum;
import com.wj.order.exception.OrderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Wang Jing
 * @time 2021/10/26 21:12
 */
@Slf4j
public class ThreadTest {

    @Autowired
    private ThreadPoolExecutor executor;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        log.info("可用处理器数量：{}",availableProcessors);
    }

    //1.继承 Thread  调用方式 new Thread01().start()
    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }
    //2.实现Runable  调用方式 new Thread(new Runable01()).start();
    public static class Runable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }
    //3.callable + FutureTask 调用方式 new Thread(new FutureTask<>(new Callable01())).start()
    //可以拿到结果 futureTaskObject.get()，可以处理异常
    public static class Callable01 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }

    //4.线程池


    //5.

    public void test(Order order){
        log.info("开始创建订单");
        //获得订单项
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.size() == 0) {
            log.info("订单项为空！！");
            throw new OrderException(OrderStatusEnum.ORDER_ITEM_IS_NULL);
        }

        //异步 生成订单id，并设置未支付
        final CompletableFuture<Long> orderIdFuture = CompletableFuture.supplyAsync(() -> {
            //生成id 注意使用getSnowflake()不要用createSnowflake()
            Long orderId = IdUtil.getSnowflake(1L, 1L).nextId();
            log.info("处理订单id，订单项id");
            order.setId(orderId);
            order.setPayStatus(0); //默认未支付
            log.info("设置订单项的订单id");
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrderId(orderId);
                orderItem.setId(IdUtil.getSnowflake(1L, 2L).nextId());
            }
            return orderId;
        }, executor);

        try {
            Long id = orderIdFuture.get();
            log.info("id: {}", id);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
