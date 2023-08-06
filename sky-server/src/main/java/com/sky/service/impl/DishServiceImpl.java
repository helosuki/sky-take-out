package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishDao;
import com.sky.mapper.DishFlavorDao;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishDao dishDao;

    @Autowired
    private DishFlavorDao dishFlavorDao;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        //复制属性到dish
        BeanUtils.copyProperties(dishDTO, dish);

        dishDao.insert(dish);

        //复制属性到dishFlavor
        for (int i = 0; i < dishDTO.getFlavors().size(); i++) {
            DishFlavor dishFlavor = new DishFlavor();
            BeanUtils.copyProperties(dishDTO.getFlavors().get(i), dishFlavor);
            dishFlavor.setDishId(dish.getId());
            dishFlavorDao.insert(dishFlavor);
        }
    }

    /**
     * 菜品分页管理
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        IPage page = new Page(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //设定查询条件
        lqw
                .like(dishPageQueryDTO.getName()!=null,Dish::getName, dishPageQueryDTO.getName())
                .like(dishPageQueryDTO.getCategoryId()!=null,Dish::getCategoryId, dishPageQueryDTO.getCategoryId())
                .like(dishPageQueryDTO.getStatus()!=null,Dish::getStatus, dishPageQueryDTO.getStatus());

        //分页查询
        dishDao.selectPage(page, lqw);
        //将结果存入PageResult
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }
}