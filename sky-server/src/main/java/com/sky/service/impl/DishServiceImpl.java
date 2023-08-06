package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishDao;
import com.sky.mapper.DishFlavorDao;
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
            BeanUtils.copyProperties(dishDTO.getFlavors().get(i),dishFlavor);
            dishFlavor.setDishId(dish.getId());
            dishFlavorDao.insert(dishFlavor);
        }
    }
}
