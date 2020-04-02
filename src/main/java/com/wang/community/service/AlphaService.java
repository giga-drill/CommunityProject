package com.wang.community.service;

import com.wang.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;


    public AlphaService(){
        System.out.println("实例化");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化AlphaService");
    }
    @PreDestroy
    public void destory() {
        System.out.println("xiaohui");
    }


    public String find() {
        return alphaDao.select();
    }

}
