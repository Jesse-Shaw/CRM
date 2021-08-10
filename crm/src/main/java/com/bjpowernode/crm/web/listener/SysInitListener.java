package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

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
        //数据字典处理完毕后，需要处理Stage2Possibility.properties文件
    /*
       处理Stage2Possibility.properties文件步骤：
       解析该文件，将该属性文件中的键值对关系处理成为java中键值对关系(map)
       Map<String(阶段Stage),String(可能性possibility)> pMap=。。。
       pMap.put("01资质审查"，10);
       pMap保存后，放在服务器缓存中
    */
        //解析Properties文件
        Map<String,String> pMap = new HashMap<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> enumeration= resourceBundle.getKeys();
        while (enumeration.hasMoreElements()){
            //阶段
            String key = enumeration.nextElement();
            //可能性
            String value = resourceBundle.getString(key);
            pMap.put(key,value);
        }
        application.setAttribute("pMap",pMap);
    }



}
