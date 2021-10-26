package com.wj.order.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Wang Jing
 * @time 2021/10/26 21:12
 */
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main开始");
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
        new Thread(futureTask).start();
        Integer ans = futureTask.get();
        System.out.println("ans====" + ans);
        System.out.println("main结束");
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

}
