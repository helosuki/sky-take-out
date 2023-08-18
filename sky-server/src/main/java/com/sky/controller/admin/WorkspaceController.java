package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@RestController
@Slf4j
@RequestMapping("/admin/workspace")
@Api(tags = "工作台接口")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;
    /**
     * 查询今日运营数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> businessDataVOResult(){
        log.info("查询今日运营数据:");
        //获得当天的开始时间
        LocalDate begin = LocalDate.now();
        //获得当天的结束时间
        LocalDate end = LocalDate.now();
        BusinessDataVO vo = workspaceService.GetBusinesssData(begin,end);
        return Result.success(vo);
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> setmealOverViewVOResult(){
        log.info("查询套餐总览");
        SetmealOverViewVO vo  = workspaceService.GetSetmealOverView();
        return Result.success(vo);
    }


    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> DishOverViewVOResult(){
        log.info("查询菜品总览");
        DishOverViewVO vo  = workspaceService.GetDishOverView();
        return Result.success(vo);
    }


    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> OrderOverViewVOResult(){
        log.info("查询订单管理数据");
        OrderOverViewVO vo  = workspaceService.GetOrderOverView();
        return Result.success(vo);
    }

}
