package com.xxq.messgaeborad.mapper;

import org.springframework.stereotype.Repository;

import com.xxq.messgaeborad.entity.Message;

@Repository
public interface MessageMapper {
    int deleteByPrimaryKey(String id);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);
}