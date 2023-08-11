package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookDao;
import com.sky.mapper.OrderDao;
import com.sky.mapper.OrderDetailDao;
import com.sky.mapper.ShoppingCartDao;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private AddressBookDao addressBookDao;
    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private OrderDetailDao orderDetailDao;
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Orders orders = new Orders();
        //处理业务异常
        //处理地址数据
        AddressBook addressBook = addressBookDao.selectById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //处理购物车数据异常
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw_shoppingCart = new LambdaQueryWrapper<>();
        lqw_shoppingCart.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartDao.selectList(lqw_shoppingCart);
        if(shoppingCartList.size()==0 || shoppingCartList == null){
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //将DTO中数据给orders
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        //给orders其他字段赋值
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orderDao.insert(orders);
        //向订orderDetail表插入数据
        for (int i = 0; i < shoppingCartList.size(); i++) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCartList.get(i),orderDetail);
            orderDetail.setId(null);
            orderDetail.setOrderId(orders.getId());
            orderDetailDao.insert(orderDetail);
        }

        //设定返回值
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }
}
