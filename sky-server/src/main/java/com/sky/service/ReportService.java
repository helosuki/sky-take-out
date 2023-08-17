package com.sky.service;

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
}
