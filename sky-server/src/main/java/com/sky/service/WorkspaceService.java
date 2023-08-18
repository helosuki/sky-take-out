package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDate;


public interface WorkspaceService {
    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO GetBusinesssData(LocalDate begin, LocalDate end);

    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO GetSetmealOverView();

    /**
     * 查询菜品总览
     * @return
     */
    DishOverViewVO GetDishOverView();

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO GetOrderOverView();

}
