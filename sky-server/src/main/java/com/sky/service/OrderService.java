package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getById(Long id);

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery(int page, int pageSize, Integer status);

    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id);

    /**
     * 再来一单
     * @param id
     */
    void againOrder(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult adminPageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancelAdmin(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     */
    void deliverOrder(Long id);

    /**
     * 完成订单
     * @param id
     */
    void completeOrder(Long id);

    /**
     * 用户催单
     * @param id
     */
    void reminderOrder(Long id);
}
