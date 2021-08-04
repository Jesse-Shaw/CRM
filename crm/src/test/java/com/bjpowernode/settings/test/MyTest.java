package com.bjpowernode.settings.test;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MD5Util;
import org.junit.Test;

public class MyTest {

    @Test
    //验证失效时间
    public void Test01(){

        String expireTime="2023-10-10 10:10:10";
/*        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        System.out.println(time);*/
        //当前系统时间
        String currentTime= DateTimeUtil.getSysTime();
        int count =expireTime.compareTo(currentTime);
        System.out.println(count);
    }
    @Test
    //验证锁定状态
    public void Test02(){

        String lockState = "0";
        if ("0".equals(lockState)){
            System.out.println("账号已锁定");
        }
    }
    @Test
    //验证IP地址
    public void Test03(){
        String ip ="192.168.1.4";
        String allowIps="192.168.1.1,192.168.1.2,192.168.1.3";
        if (allowIps.contains(ip)){
            System.out.println("有效IP地址，允许访问系统");
        }else {
            System.out.println("IP地址非法，请联系管理员");
        }
    }
    @Test
    //验证加密
    public void Test04(){
        String pwd="Xjx961019@/*-$";
        String pwd1= MD5Util.getMD5(pwd);
        System.out.println(pwd1);
       // 202cb962ac59075b964b07152d234b70
        //202cb962ac59075b964b07152d234b70
    }
}
