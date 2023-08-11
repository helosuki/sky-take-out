package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookDao;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookDao addressBookDao;

    private static final Integer DEFAULT =1;
    private static final Integer UN_DEFAULT =0;
    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(UN_DEFAULT);
        addressBookDao.insert(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @Override
    public List<AddressBook> list() {
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        List<AddressBook> addressBookList = addressBookDao.selectList(lqw);
        return addressBookList;
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        List<AddressBook> list = addressBookDao.selectList(lqw);
        for (int i = 0; i < list.size(); i++) {
            AddressBook book = list.get(i);
            if(book.getId()==addressBook.getId()){
                book.setIsDefault(DEFAULT);
                addressBookDao.updateById(book);
            }else {
                book.setIsDefault(UN_DEFAULT);
                addressBookDao.updateById(book);
            }
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook getDefault() {
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId()).eq(AddressBook::getIsDefault,DEFAULT);
        AddressBook addressBook = addressBookDao.selectOne(lqw);
        return addressBook;
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Integer id) {
        AddressBook addressBook = addressBookDao.selectById(id);
        return addressBook;
    }

    /**
     * 根据id修改地址
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookDao.updateById(addressBook);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        addressBookDao.deleteById(id);
    }
}
