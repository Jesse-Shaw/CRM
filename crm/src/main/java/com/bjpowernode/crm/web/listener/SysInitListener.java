package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        /*
        该方法是用来监听上下文域对象的方法，当服务器启动，上下文域对象创建对象创建完毕后，马上执行该方法
        event：该参数能够取得监听的对象
               监听的是什么对象，就可以通过该参数取得什么对象
               例如我们现在监听的是上下文域对象
        */
        System.out.println("服务器处理数据字典开始");
        ServletContext application =event.getServletContext();
        //业务层取数据字典，按照typeCode分为7类，select7个list，然后放进map，再放进application
        //7个这样的list{"typeCode",dvList} 存进了map
        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue> >map = dicService.getAllList();
        //将map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();
        for (String key:set) {
            application.setAttribute(key,map.get(key));
        }
        System.out.println("服务器处理数据字典结束");

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
