package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到用户控制器");
        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }
        else if ("/settings/user/xxx.do".equals(path)){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码明文改密文
        loginPwd= MD5Util.getMD5(loginPwd);
        //获取Ip地址
        String ip = request.getRemoteAddr();
        System.out.println("ip地址是"+ip);
        //业务层Service使用动态代理形态的接口对象
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        //调用Service的login方法
        try {
            User user =service.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);
            //如果程序执行到此处，说明业务层没有给controller抛出任何异常
            //需要给前端返回{"success":true},调用json工具给前端返回
            PrintJson.printJsonFlag(response,true);
        } catch (Exception e) {
            e.printStackTrace();
            //一旦执行到这里，说明业务层为我们验证失败，为controller抛出了异常
            //表示登陆失败
            /*
            {"success":false,"msg":?}
            Controller为Ajax提供多项信息，可以有两种手段
            1.封装一个map集合，将map解析为json返回
            2.创建一个对象Vo对象
                  boolean success;
                  String msg;
              如果对于展现的信息将来还会大量使用就使用VO类
              如果展现的信息只有在这个需求中使用，使用map就可，此处我们选择map
            */
            String msg = e.getMessage();
            Map<String,Object> map =new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);
        }
    }
}
