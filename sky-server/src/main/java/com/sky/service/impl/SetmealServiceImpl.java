package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishDao;
import com.sky.mapper.SetmealDao;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private SetMealDishDao setMealDishDao;
    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealDao.insert(setmeal);
        for (int i = 0; i < setmealDTO.getSetmealDishes().size(); i++) {
            SetmealDish setmealDish = new SetmealDish();
            BeanUtils.copyProperties(setmealDTO.getSetmealDishes().get(i),setmealDish);
            setmealDish.setSetmealId(setmeal.getId());
            setMealDishDao.insert(setmealDish);
        }
    }
}
