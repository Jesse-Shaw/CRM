package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao userdao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userdao.login(map);
        if (user == null){
            throw new LoginException("用户名或者密码错误");
        }
        //如果程序执行到此，说明账号密码正确
        //需要继续向下验证
        //验证失效时间
        String expireTime=user.getExpireTime();
        String currentTime= DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime)<0){
            throw new LoginException("该账号已失效，请联系管理员处理");
        }
        //判断锁定状态 0代表锁定，1代表正常
        String lockState = user.getLockState();
        if ("0".equals(lockState)){
            throw new LoginException("该账号已锁定，请联系管理员处理");
        }
        //判断ip
        String allowIps= user.getAllowIps();
        if (allowIps!=null && allowIps!="") {
            if (!allowIps.contains(ip)) {
                throw new LoginException("当前ip地址非法，请合理访问");
            }
        }
        return user;
    }
}
