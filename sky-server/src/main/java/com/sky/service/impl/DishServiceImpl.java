package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryDao;
import com.sky.mapper.DishDao;
import com.sky.mapper.DishFlavorDao;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishDao dishDao;

    @Autowired
    private CategoryDao categoryDao;

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
    //TODO 后期可以用mybatis替换
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
        IPage iPage = dishDao.selectPage(page, lqw);
        //将Dish数据封装到DishVO中传到前端
        List<Dish> list = iPage.getRecords();
        List<DishVO> dishVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(list.get(i),dishVO);
            Category category = categoryDao.selectById(dishVO.getCategoryId());
            dishVO.setCategoryName(category.getName());
            dishVOList.add(dishVO);
        }
        //将结果存入PageResult
        PageResult pageResult = new PageResult();
        pageResult.setTotal(iPage.getTotal());
        pageResult.setRecords(dishVOList);
        return pageResult;
    }
}
