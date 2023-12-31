package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryDao;
import com.sky.mapper.DishDao;
import com.sky.mapper.SetmealDishDao;
import com.sky.mapper.SetmealDao;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import kotlin.jvm.internal.Lambda;
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
    @Autowired
    private DishDao dishDao;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealDao.insert(setmeal);
        for (int i = 0; i < setmealDTO.getSetmealDishes().size(); i++) {
            SetmealDish setmealDish = new SetmealDish();
            BeanUtils.copyProperties(setmealDTO.getSetmealDishes().get(i), setmealDish);
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishDao.insert(setmealDish);
        }
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        IPage page = new Page(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Setmeal> lqw_setmeal = new LambdaQueryWrapper<>();
        lqw_setmeal
                .like(setmealPageQueryDTO.getName() != null, Setmeal::getName, setmealPageQueryDTO.getName())
                .like(setmealPageQueryDTO.getCategoryId() != null, Setmeal::getCategoryId, setmealPageQueryDTO.getCategoryId())
                .like(setmealPageQueryDTO.getStatus() != null, Setmeal::getStatus, setmealPageQueryDTO.getStatus());
        IPage iPage = setmealDao.selectPage(page, lqw_setmeal);
        List<Setmeal> setmealList = iPage.getRecords();
        List<SetmealVO> setmealVOList = new ArrayList<>();
        for (int i = 0; i < setmealList.size(); i++) {
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(setmealList.get(i), setmealVO);
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

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    @Transactional
    public void delete(Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Long id = ids[i];
            Setmeal setmeal = setmealDao.selectById(id);
            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setmealDao.deleteById(setmeal.getId());
            lqw.eq(SetmealDish::getSetmealId, setmeal.getId());
            setmealDishDao.delete(lqw);
        }
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        Setmeal setmeal = setmealDao.selectById(id);
        Category category = categoryDao.selectById(setmeal.getCategoryId());
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishDao.selectList(lqw);
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setCategoryName(category.getName());
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealDao.updateById(setmeal);

        Long id = setmealDTO.getId();
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishDao.selectList(lqw);
        for (int i = 0; i < list.size(); i++) {
            setmealDishDao.deleteById(list.get(i).getId());
        }

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (int i = 0; i < setmealDishes.size(); i++) {
            SetmealDish setmealDish = setmealDishes.get(i);
            setmealDish.setSetmealId(id);
            setmealDishDao.insert(setmealDish);
        }

    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    @Override
    public void starOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealDao.updateById(setmeal);
    }

    /**
     * 用户端根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> list(Integer categoryId) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId,categoryId).eq(Setmeal::getStatus,StatusConstant.ENABLE);
        List<Setmeal> list = setmealDao.selectList(lqw);
        return list;
    }

    /**
     * 用户端根据套餐id查询包含的菜品
     * @param id
     */
    @Override
    public List<DishItemVO> getByIdUser(Long id) {
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishDao.selectList(lqw);
        List<DishItemVO> dishItemVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Dish dish = dishDao.selectById(list.get(i).getDishId());
            DishItemVO dishItemVO = new DishItemVO();
            BeanUtils.copyProperties(dish,dishItemVO);
            dishItemVO.setCopies(list.get(i).getCopies());
            dishItemVOList.add(dishItemVO);
        }
        return dishItemVOList;
    }


}
