package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入防止恶意登陆过滤器");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();
        if ("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            User user = (User) request.getSession().getAttribute("user");
            if (user!=null){
                filterChain.doFilter(servletRequest,servletResponse);
            }else {
            /*
            重定向到登录页
              重定向路径怎么写
              在实际的项目开发中，对于路径的使用，不论操作的是前端还是后端，应该一律使用绝对路径
              请求转发：使用的是一种特殊的绝对路径，这种绝对路径前不加/项目名，这种路径也称为内部路径
              重定向：使用的是传统绝对路径的写法，前面必须加/项目名
              /crm/login.jsp
             为什么要使用重定向，转发不行吗?
             转发后，路径会停留在老路径上，而不是跳转后最新资源的路径
             我们应该为用户跳转到登录页的同时，将浏览器的地址栏应该自动设置为当前的登录页的路径
            */
                //request.getRequestDispatcher("/login.jsp").forward(request,response);
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }
}
