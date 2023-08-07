package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.CategoryDao;
import com.sky.mapper.SetmealDishDao;
import com.sky.mapper.SetmealDao;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private SetmealDishDao setmealDishDao;
    @Autowired
    private CategoryDao categoryDao;

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
            setmealDishDao.insert(setmealDish);
        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        IPage page = new Page(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Setmeal> lqw_setmeal = new LambdaQueryWrapper<>();
        lqw_setmeal
                .like(setmealPageQueryDTO.getName()!=null,Setmeal::getName,setmealPageQueryDTO.getName())
                .like(setmealPageQueryDTO.getCategoryId()!=null,Setmeal::getCategoryId,setmealPageQueryDTO.getCategoryId())
                .like(setmealPageQueryDTO.getStatus()!=null,Setmeal::getStatus,setmealPageQueryDTO.getStatus());
        IPage iPage = setmealDao.selectPage(page, lqw_setmeal);
        List<Setmeal> setmealList = iPage.getRecords();
        List<SetmealVO> setmealVOList = new ArrayList<>();
        for (int i = 0; i < setmealList.size(); i++) {
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(setmealList.get(i),setmealVO);
            //获取分类名
            Category category = categoryDao.selectById(setmealVO.getCategoryId());
            setmealVO.setCategoryName(category.getName());
            //获取套餐关联菜品
            LambdaQueryWrapper<SetmealDish> lqw_setmeal_dish = new LambdaQueryWrapper<>();
            lqw_setmeal_dish.eq(SetmealDish::getSetmealId, setmealVO.getId());
            List<SetmealDish> setmealDishList = setmealDishDao.selectList(lqw_setmeal_dish);
            setmealVO.setSetmealDishes(setmealDishList);
            setmealVOList.add(setmealVO);
        }
        PageResult pageResult = new PageResult();
        pageResult.setTotal(iPage.getTotal());
        pageResult.setRecords(setmealVOList);
        return pageResult;
    }
}
