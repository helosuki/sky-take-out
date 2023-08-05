package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryDao;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDao categoryDao;

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

        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryDao.insert(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void delete(Long id) {
        categoryDao.deleteById(id);
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
        //设置修改时间与修改人
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryDao.updateById(category);
    }


}
