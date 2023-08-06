package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.constant.TypeConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.AlreadyExistsException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryDao;
import com.sky.mapper.DishDao;
import com.sky.mapper.SetmealDao;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import kotlin.jvm.internal.Lambda;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private DishDao dishDao;

    @Autowired
    private SetmealDao setmealDao;

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        IPage page = new Page(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        //模糊查询条件
        if (categoryPageQueryDTO.getName() != null) {
            if (categoryPageQueryDTO.getType() != null) {
                lqw
                        .like(Category::getType, categoryPageQueryDTO.getType())
                        .like(Category::getName, categoryPageQueryDTO.getName());
            }else {
                lqw
                        .like(Category::getName, categoryPageQueryDTO.getName());
            }
        }else {
            if (categoryPageQueryDTO.getType() != null) {
                lqw
                        .like(Category::getType, categoryPageQueryDTO.getType());
            }
        }

        //page:分页信息 lqw当前查询约束条件
        categoryDao.selectPage(page,lqw);

        //生产PageResult对象并设置总页数和当前页面数据
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());
        return pageResult;
    }

    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        //复制对象数据categoryDTO=>category
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //设置查询条件当表中sort与新增sort是否有重复
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getSort,category.getSort()).or().eq(Category::getName,category.getName());
        if(categoryDao.selectList(lqw).isEmpty()){

            //若srot无重复时，则设置分类的修改时间和修改人以及创建时间创建人，状态
            category.setStatus(StatusConstant.DISABLE);
/*            category.setCreateTime(LocalDateTime.now());
            category.setCreateUser(BaseContext.getCurrentId());
            category.setUpdateTime(LocalDateTime.now());
            category.setUpdateUser(BaseContext.getCurrentId());*/

            categoryDao.insert(category);
        }else {
            //排序重复，抛出异常
            throw new AlreadyExistsException(MessageConstant.SORT_OR_NAME_ALREADY_EXISTS);
        }
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void delete(Long id) {
        Category category = categoryDao.selectById(id);
        //设置DISH查询条件

        LambdaQueryWrapper<Dish> lqw_dish = new LambdaQueryWrapper<>();
        lqw_dish.eq(Dish::getCategoryId,id);

        //设置Setmeal查询条件

        LambdaQueryWrapper<Setmeal> lqw_setmeal = new LambdaQueryWrapper<>();
        lqw_setmeal.eq(Setmeal::getCategoryId,id);

        if(category.getType()== TypeConstant.TYPE_DISH){//判断是否是菜品分类
            if(dishDao.selectList(lqw_dish).isEmpty()){//判断该分类下是否还有菜品
                //无则删除该分类
                categoryDao.deleteById(id);
            }else {
                //有则提示该分类还有关联的菜品
                throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
            }
        }else if(category.getType() == TypeConstant.TYPE_SETMEAL) {//判断是否是套餐分类
            if(setmealDao.selectList(lqw_setmeal).isEmpty()){//判断该分类下是否还有菜品
                //无则删除该分类
                categoryDao.deleteById(id);
            }else {
                //有则提示该分类还有关联的菜品
                throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
            }
        }else {//其他异常
            throw new DeletionNotAllowedException(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        //复制数据DTO=》category中
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //设置查询条件当表修改时的分类名或者sort重复时抛出重复异常
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getSort,category.getSort()).or().eq(Category::getName,category.getName());
        List<Category> list = categoryDao.selectList(lqw);
        int size = list.size();
        if(size<=1){
            //设置修改时间与修改人
/*            category.setUpdateTime(LocalDateTime.now());
            category.setUpdateUser(BaseContext.getCurrentId());*/
            categoryDao.updateById(category);
        }else {
            //排序重复，抛出异常
            throw new AlreadyExistsException(MessageConstant.SORT_OR_NAME_ALREADY_EXISTS);
        }
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    @Override
    public void statOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
/*                .createTime(LocalDateTime.now())
                .createUser(BaseContext.getCurrentId())*/
                .build();

        categoryDao.updateById(category);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getType,type);
        List<Category> list = categoryDao.selectList(lqw);
        return list;
    }


}
