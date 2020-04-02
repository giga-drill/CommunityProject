package com.wang.community.dao;

import com.wang.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //只有一个参数且在<if>中使用必须用别名
    //@Param用于给变量取别名
    int selectDiscussPostRows(@Param("userId") int userId);


}


