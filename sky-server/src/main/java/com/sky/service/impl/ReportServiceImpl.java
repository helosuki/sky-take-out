package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderDao;
import com.sky.mapper.UserDao;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserDao userDao;

    /**
     * 营业额统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnover(LocalDate begin, LocalDate end) {
        //调用设置日期方法
        List<LocalDate> dateList = getLocalDates(begin, end);
        //将集合拼成字符串，separator是分隔符
        String join = StringUtils.join(dateList, ",");
        TurnoverReportVO vo = new TurnoverReportVO();
        vo.setDateList(join);
        String turnover = vo.getTurnoverList();
        for (LocalDate localDate : dateList) {
            //根据日期获取当天最小时间和最大时间参数
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //设置查询条件
            LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
            lqw
                    .lt(Orders::getOrderTime, endTime)
                    .ge(Orders::getOrderTime, beginTime)
                    .eq(Orders::getStatus, Orders.COMPLETED);
            //获得当前日期的订单集合
            List<Orders> ordersList = orderDao.selectList(lqw);
            BigDecimal bigDecimal = new BigDecimal(0);
            for (Orders orders : ordersList) {
                //大数相加
                bigDecimal = bigDecimal.add(new BigDecimal(orders.getAmount().toString()));
            }
            turnover += bigDecimal + ",";
        }
        vo.setTurnoverList(turnover);
        return vo;
    }

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userReport(LocalDate begin, LocalDate end) {
        //调用设置日期方法
        List<LocalDate> localDates = getLocalDates(begin, end);
        String join = StringUtils.join(localDates);
        UserReportVO vo = new UserReportVO();
        vo.setDateList(join);
        //总用户数量
        List<Integer> totalUsers = new ArrayList<>();
        //新增用户数量
        List<Integer> newUsers = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            //根据日期获取当天最小时间和最大时间参数
            //获取今日用户量
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            LambdaQueryWrapper<User> lqw_today = new LambdaQueryWrapper<>();
            lqw_today
                    .lt(User::getCreateTime, endTime)
                    .ge(User::getCreateTime, beginTime);
            List<User> users_today = userDao.selectList(lqw_today);
            newUsers.add(users_today.size());

            //获取昨日用户量
            LambdaQueryWrapper<User> lqw_yesterday = new LambdaQueryWrapper<>();
            lqw_yesterday
                    .lt(User::getCreateTime, endTime);
            List<User> users_total= userDao.selectList(lqw_yesterday);
            totalUsers.add(users_total.size());
        }
        String totalUser = StringUtils.join(totalUsers);
        String newUser = StringUtils.join(newUsers);
        vo.setTotalUserList(totalUser);
        vo.setNewUserList(newUser);
        return vo;
    }


    @NotNull
    private static List<LocalDate> getLocalDates(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            //计算日期，计算指定日期后一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
