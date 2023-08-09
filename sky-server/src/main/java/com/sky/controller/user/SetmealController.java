package com.sky.controller.user;


import com.sky.dto.DishDTO;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  套餐管理
 * */
@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端-套餐浏览接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    /**
     * 用户端根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("用户端根据分类id查询套餐")
    public Result<List<Setmeal>> list(Integer categoryId){
        log.info("用户端根据分类id查询套餐：{}",categoryId);
        List<Setmeal> list = setmealService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 用户端根据套餐id查询包含的菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("用户端根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> getByIdUser(@PathVariable Long id){
        log.info("用户端根据套餐id查询包含的菜品:{}",id);
        List<DishItemVO> dishItemVOList = setmealService.getByIdUser(id);
        return Result.success(dishItemVOList);
    }
}
