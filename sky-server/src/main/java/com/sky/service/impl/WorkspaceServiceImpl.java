package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishDao;
import com.sky.mapper.OrderDao;
import com.sky.mapper.SetmealDao;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private ReportService reportService;
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private DishDao dishDao;
    @Autowired
    private OrderDao orderDao;
    /**
     * 查询今日运营数据
     *
     * @return
     */
    @Override
    public BusinessDataVO GetBusinesssData(LocalDate begin,LocalDate end) {
        //获取用户数据
        UserReportVO userReportVO = reportService.userReport(begin, end);
        //获取订单数据
        OrderReportVO orderReportVO = reportService.orderReport(begin, end);
        //获取营业额数据
        TurnoverReportVO turnoverReportVO = reportService.turnover(begin, end);
        String newUserList = userReportVO.getNewUserList();
        String turnoverList = turnoverReportVO.getTurnoverList();
        //获取今日有效订单
        Integer validOrderCount = orderReportVO.getValidOrderCount();
        //获取今日订单完成率
        Double orderCompletionRate = orderReportVO.getOrderCompletionRate();

        // 使用正则表达式解析
        String regex = "\\d+(?:\\.\\d+)?";
        Matcher m = Pattern.compile(regex, Pattern.MULTILINE).matcher(turnoverList);
        Double turnover = 0d;
        // 获取第一个数字
        while (m.find()) {
            //获取今日营业额
            turnover += Double.valueOf(m.group());
        }
        //新增用户
        int newUser = 0;
        char[] charArr = newUserList.toCharArray();
        int num = 0;
        int cur = 0;
        for(int i=0;i<charArr.length;i++){
            cur = charArr[i]-'0';
            if(cur<0 || cur>9){
                newUser+=num;
                num=0;
            } else {
                num = num*10+cur;
            }

        }
        newUser+=num;

        double unitPrice = 0d;
        //获取平均客单价
        if(turnover!=0d){
            unitPrice = turnover / validOrderCount;
        }
        BusinessDataVO vo = new BusinessDataVO();
        vo.setTurnover(turnover);
        vo.setOrderCompletionRate(orderCompletionRate);
        vo.setNewUsers(newUser);
        vo.setValidOrderCount(validOrderCount);
        vo.setUnitPrice(unitPrice);

        return vo;
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @Override
    public SetmealOverViewVO GetSetmealOverView() {
        LambdaQueryWrapper<Setmeal> lqw_sold = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> lqw_discontinued = new LambdaQueryWrapper<>();

        lqw_sold.eq(Setmeal::getStatus, StatusConstant.ENABLE);
        List<Setmeal> sold = setmealDao.selectList(lqw_sold);
        lqw_discontinued.eq(Setmeal::getStatus, StatusConstant.DISABLE);
        List<Setmeal> discontinued = setmealDao.selectList(lqw_discontinued);

        SetmealOverViewVO vo = new SetmealOverViewVO();
        vo.setSold(sold.size());
        vo.setDiscontinued(discontinued.size());
        return vo;
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @Override
    public DishOverViewVO GetDishOverView() {
        LambdaQueryWrapper<Dish> lqw_sold = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Dish> lqw_discontinued = new LambdaQueryWrapper<>();

        lqw_sold.eq(Dish::getStatus, StatusConstant.ENABLE);
        List<Dish> sold = dishDao.selectList(lqw_sold);
        lqw_discontinued.eq(Dish::getStatus, StatusConstant.DISABLE);
        List<Dish> discontinued = dishDao.selectList(lqw_discontinued);

        DishOverViewVO vo = new DishOverViewVO();
        vo.setSold(sold.size());
        vo.setDiscontinued(discontinued.size());
        return vo;
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO GetOrderOverView() {
        LocalDateTime begin= LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        //全部订单
        LambdaQueryWrapper<Orders> lqw_all = new LambdaQueryWrapper<>();
        lqw_all.ge(Orders::getOrderTime, begin);
        List<Orders> allOrders = orderDao.selectList(lqw_all);
        //取消的订单
        LambdaQueryWrapper<Orders> lqw_cancel = new LambdaQueryWrapper<>();
        lqw_cancel
                .eq(Orders::getStatus, Orders.CANCELLED)
                .ge(Orders::getOrderTime, begin);
        List<Orders> cancelOrders = orderDao.selectList(lqw_cancel);
        //已完成的订单
        LambdaQueryWrapper<Orders> lqw_complete = new LambdaQueryWrapper<>();
        lqw_complete
                .eq(Orders::getStatus, Orders.COMPLETED)
                .ge(Orders::getOrderTime,begin);
        List<Orders> completeOrders = orderDao.selectList(lqw_complete);
        //待派送订单
        LambdaQueryWrapper<Orders> lqw_confirmed = new LambdaQueryWrapper<>();
        lqw_confirmed
                .eq(Orders::getStatus, Orders.CONFIRMED)
                .ge(Orders::getOrderTime,begin);
        List<Orders> confirmedOrders = orderDao.selectList(lqw_confirmed);
        //待接单数
        LambdaQueryWrapper<Orders> lqw_to_be_confirmed = new LambdaQueryWrapper<>();
        lqw_to_be_confirmed
                .eq(Orders::getStatus, Orders.TO_BE_CONFIRMED)
                .ge(Orders::getOrderTime,begin);
        List<Orders> toBeConfirmedOrders = orderDao.selectList(lqw_to_be_confirmed);

        OrderOverViewVO vo = OrderOverViewVO.builder()
                .allOrders(allOrders.size())
                .cancelledOrders(cancelOrders.size())
                .completedOrders(completeOrders.size())
                .deliveredOrders(confirmedOrders.size())
                .waitingOrders(toBeConfirmedOrders.size())
                .build();

        return vo;
    }

}
