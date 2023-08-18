package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDao;
import com.sky.mapper.UserDao;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private ReportService reportService;

    /**
     * 查询今日运营数据
     *
     * @return
     */
    @Override
    public BusinessDataVO GetBusinesssData() {
        //获取用户数据
        UserReportVO userReportVO = reportService.userReport(LocalDate.now(), LocalDate.now());
        //获取订单数据
        OrderReportVO orderReportVO = reportService.orderReport(LocalDate.now(), LocalDate.now());
        //获取营业额数据
        TurnoverReportVO turnoverReportVO = reportService.turnover(LocalDate.now(), LocalDate.now());
        String newUserList = userReportVO.getNewUserList();
        String turnoverList = turnoverReportVO.getTurnoverList();
        //获取今日有效订单
        Integer validOrderCount = orderReportVO.getValidOrderCount();
        //获取今日订单完成率
        Double orderCompletionRate = orderReportVO.getOrderCompletionRate();

        // 使用正则表达式解析
        String regex="\\d+(?:\\.\\d+)?";
        Matcher m= Pattern.compile(regex, Pattern.MULTILINE).matcher(turnoverList);
        //获取一个String 中所有的数字放到集合中
//        List<String> result=new ArrayList<String>();
//        while(m.find()){
//            result.add(m.group());
//        }
        Double turnover=0d;
        // 获取第一个数字
        if(m.find()){
            //获取今日营业额
            turnover = Double.valueOf(m.group());
        }
        //新增用户
        int newUser = Integer.parseInt(newUserList);

        //获取平均客单价
        double unitPrice = turnover / validOrderCount;
        BusinessDataVO vo = new BusinessDataVO();
        vo.setTurnover(turnover);
        vo.setOrderCompletionRate(orderCompletionRate);
        vo.setNewUsers(newUser);
        vo.setValidOrderCount(validOrderCount);
        vo.setUnitPrice(unitPrice);

        return vo;
    }
}
