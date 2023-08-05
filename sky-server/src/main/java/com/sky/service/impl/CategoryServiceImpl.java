package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryDao;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
