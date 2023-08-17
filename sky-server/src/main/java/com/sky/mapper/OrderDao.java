package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDao extends BaseMapper<Orders> {
    List<GoodsSalesDTO> getTop10(@Param("begin") LocalDateTime beginTime, @Param("end") LocalDateTime endTime);
}
