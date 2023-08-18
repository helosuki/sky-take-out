package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@Slf4j
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("营业额统计接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverReportVOResult(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("营业额统计接口:{},{}", begin, end);
        TurnoverReportVO turnoverReportVO = reportService.turnover(begin, end);
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result<UserReportVO> userReportVOResult(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("用户统计接口:{},{}", begin, end);
        UserReportVO userReportVO = reportService.userReport(begin,end);
        return Result.success(userReportVO);
    }




    /**
     * 订单统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public Result<OrderReportVO> OrderVOResult(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("订单统计接口:{},{}", begin, end);
        OrderReportVO orderReportVO = reportService.orderReport(begin,end);
        return Result.success(orderReportVO);
    }


    /**
     * 查询销量排名top10接口
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<SalesTop10ReportVO> Top10Result(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("查询销量排名top10接口:{},{}", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.Top10Report(begin,end);
        return Result.success(salesTop10ReportVO);
    }


    /**
     * 导出Excel报表接口
     */
    @ApiOperation("导出Excel报表接口")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        log.info("导出Excel报表接口:");
        reportService.exportExcel(response);
    }
}
