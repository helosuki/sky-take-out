package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserDishController")
@RequestMapping("/user/dish")
@Api(tags = "C端-菜品浏览接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Integer categoryId) {
        log.info("根据分类id查询菜品:{}", categoryId);
        //设置Redis中的key
        String key = "dish_" + categoryId;
        //查询redis中是否有缓存数据
        List<DishVO> dishVOList= (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(dishVOList!=null && dishVOList.size()>0){//有则直接返回
            return Result.success(dishVOList);
        }
        //无则查询数据库
        dishVOList = dishService.UserList(categoryId);
        //并缓存到redis中
        redisTemplate.opsForValue().set(key,dishVOList);
        return Result.success(dishVOList);
    }
}
