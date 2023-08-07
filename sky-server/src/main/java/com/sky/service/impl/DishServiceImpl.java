package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
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
    //菜品接口
    @Autowired
    private DishDao dishDao;
    //分类接口
    @Autowired
    private CategoryDao categoryDao;
    //喜好接口
    @Autowired
    private DishFlavorDao dishFlavorDao;
    //套餐接口
    @Autowired
    private SetmealDishDao setMealDishDao;

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
                .like(dishPageQueryDTO.getName() != null, Dish::getName, dishPageQueryDTO.getName())
                .like(dishPageQueryDTO.getCategoryId() != null, Dish::getCategoryId, dishPageQueryDTO.getCategoryId())
                .like(dishPageQueryDTO.getStatus() != null, Dish::getStatus, dishPageQueryDTO.getStatus());

        //分页查询
        IPage iPage = dishDao.selectPage(page, lqw);
        //将Dish数据封装到DishVO中传到前端
        List<Dish> list = iPage.getRecords();
        List<DishVO> dishVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(list.get(i), dishVO);
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

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void delete(Long[] ids) {
        LambdaQueryWrapper<SetmealDish> lqw_setMealDish = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<DishFlavor> lqw_dishFlavor = new LambdaQueryWrapper<>();
        for (int i = 0; i < ids.length; i++) {
            //获取当前ids数组id值
            Long id = ids[i];
            Dish dish = dishDao.selectById(id);
            //设定套餐关联菜品表的查询条件
            lqw_setMealDish.eq(SetmealDish::getDishId,id);
            //设定喜好表查询条件
            lqw_dishFlavor.eq(DishFlavor::getDishId,id);
            if (dish.getStatus() != StatusConstant.ENABLE){//当菜品不为启售状态时进入下一步
                if(setMealDishDao.selectList(lqw_setMealDish).size()==0){//当菜品没有关联套餐时执行删除菜品和菜品喜好
                    dishDao.deleteById(id);
                    dishFlavorDao.delete(lqw_dishFlavor);
                }else {//当菜品关联套餐时，抛出异常
                    throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
                }
            }else {//当菜品为禁售状态时抛出异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        //将查询到的Dish转为DishVO
        DishVO dishVO = new DishVO();
        Dish dish = dishDao.selectById(id);
        BeanUtils.copyProperties(dish,dishVO);
        Category category = categoryDao.selectById(dish.getCategoryId());
        dishVO.setCategoryName(category.getName());
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishDao.updateById(dish);

        //复制属性到dishFlavor
        for (int i = 0; i < dishDTO.getFlavors().size(); i++) {
            DishFlavor dishFlavor = new DishFlavor();
            BeanUtils.copyProperties(dishDTO.getFlavors().get(i), dishFlavor);
            dishFlavor.setDishId(dish.getId());
            dishFlavorDao.insert(dishFlavor);
        }
    }

    /**
     * 菜品启售禁售
     * @param status
     * @param id
     */
    @Override
    public void starOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishDao.updateById(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Integer categoryId) {
        LambdaQueryWrapper<Dish> lqw =new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId,categoryId);
        List<Dish> dishList = dishDao.selectList(lqw);
        return dishList;
    }
}
