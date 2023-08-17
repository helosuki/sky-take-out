package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.util.Date;

public interface ReportService {
    /**
     * 营业额统计接口
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnover(LocalDate begin, LocalDate end);

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userReport(LocalDate begin, LocalDate end);

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO orderReport(LocalDate begin, LocalDate end);

    /**
     * 查询销量排名top10接口
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO Top10Report(LocalDate begin, LocalDate end);
}
