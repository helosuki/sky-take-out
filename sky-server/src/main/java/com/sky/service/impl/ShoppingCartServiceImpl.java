package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishDao;
import com.sky.mapper.SetmealDao;
import com.sky.mapper.ShoppingCartDao;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartDao shoppingCartDao;
    @Autowired
    private DishDao dishDao;

    @Autowired
    private SetmealDao setmealDao;

    /**
     * 用户端查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartDao.selectList(lqw);
        return list;
    }

    /**
     * 用户端添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //将数据copy到shoppingCart中
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //设置动态sql
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw
                .eq(shoppingCartDTO.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCartDTO.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor())
                .eq(ShoppingCart::getUserId, userId)
                .eq(shoppingCartDTO.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        List<ShoppingCart> list = shoppingCartDao.selectList(lqw);
        //判断购物车中是否有数据
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartDao.updateById(cart);
        } else {
            //如果不存在,需要插入一条购物车数排
            //判断本次添加到购物车的是菜品还是套餐
            if (shoppingCartDTO.getDishId() != null) {
                Dish dish = dishDao.selectById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setNumber(1);
            } else {
                Setmeal setmeal = setmealDao.selectById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setNumber(1);
            }

            shoppingCartDao.insert(shoppingCart);
        }

    }

    /**
     * 删除购物车中一个商品
     *
     * @param shoppingCartDTO
     */
    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw
                .eq(shoppingCartDTO.getDishId() != null, ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                .eq(shoppingCartDTO.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor())
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .eq(shoppingCartDTO.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId());

        List<ShoppingCart> list = shoppingCartDao.selectList(lqw);
        ShoppingCart cart = list.get(0);
        if(cart.getNumber()>1){
            cart.setNumber(cart.getNumber()-1);
            shoppingCartDao.updateById(cart);
        }else {
            shoppingCartDao.delete(lqw);
        }
    }

    /**
     * 清空购物车
     */
    @Override
    public void deleteAll() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartDao.delete(lqw);
    }


}
