package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderDao;
import com.sky.mapper.UserDao;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
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
        String totalUser = StringUtils.join(totalUsers,",");
        String newUser = StringUtils.join(newUsers,",");
        vo.setTotalUserList(totalUser);
        vo.setNewUserList(newUser);
        return vo;
    }

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderReport(LocalDate begin, LocalDate end) {
        //调用设置日期方法
        List<LocalDate> localDates = getLocalDates(begin, end);
        String join = StringUtils.join(localDates);
        OrderReportVO vo = new OrderReportVO();
        vo.setDateList(join);
        //总用户数量
        List<Integer> totalOrdersList = new ArrayList<>();
        //新增用户数量
        List<Integer> validOrdersList = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //总订单数列表
            LambdaQueryWrapper<Orders> lqw_total= new LambdaQueryWrapper<>();
            lqw_total
                    .ge(Orders::getOrderTime,beginTime)
                    .lt(Orders::getOrderTime,endTime);
            List<Orders> total = orderDao.selectList(lqw_total);
            totalOrdersList.add(total.size());

            //有效订单列表
            LambdaQueryWrapper<Orders> lqw_valid = new LambdaQueryWrapper<>();
            lqw_valid
                    .ge(Orders::getOrderTime,beginTime)
                    .lt(Orders::getOrderTime,endTime)
                    .eq(Orders::getStatus,Orders.COMPLETED);
            List<Orders> valid = orderDao.selectList(lqw_valid);
            validOrdersList.add(valid.size());
        }
        String totalList = StringUtils.join(totalOrdersList,",");
        vo.setOrderCountList(totalList);
        String validList = StringUtils.join(validOrdersList,",");
        vo.setValidOrderCountList(validList);


        Integer totalOrderCount = totalOrdersList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrdersList.stream().reduce(Integer::sum).get();
        Double rate = validOrderCount.doubleValue()/totalOrderCount;
        vo.setValidOrderCount(validOrderCount);
        vo.setTotalOrderCount(totalOrderCount);
        vo.setOrderCompletionRate(rate);
        return vo;
    }

    /**
     * 查询销量排名top10接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO Top10Report(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> dto = orderDao.getTop10(beginTime,endTime);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : dto) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        String name = StringUtils.join(nameList, ",");
        String number = StringUtils.join(numberList, ",");
        SalesTop10ReportVO vo  = new SalesTop10ReportVO();
        vo.setNameList(name);
        vo.setNumberList(number);
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
