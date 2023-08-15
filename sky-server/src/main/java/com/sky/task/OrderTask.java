package com.sky.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderDao orderDao;

    /**
     * 定时处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void orderTimeOutTask(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw
                .eq(Orders::getStatus,Orders.PENDING_PAYMENT)
                .lt(Orders::getOrderTime,time);
        List<Orders> ordersList = orderDao.selectList(lqw);
        if(ordersList!=null&&ordersList.size()>0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，已自动取消！");
                orders.setCancelTime(LocalDateTime.now());
                orderDao.updateById(orders);
            }
        }
    }


    /**
     * 定时处理带派送订单
     */

    @Scheduled(cron = "0 0 4 * * ? ")
    public void orderDeliveryTask(){
        log.info("定时处理带派送订单:{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusDays(-1);

        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw
                .lt(Orders::getOrderTime,time)
                .eq(Orders::getStatus,Orders.DELIVERY_IN_PROGRESS);
        List<Orders> ordersList = orderDao.selectList(lqw);
        if(ordersList!=null&&ordersList.size()>0){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
            }
        }
    }
}
