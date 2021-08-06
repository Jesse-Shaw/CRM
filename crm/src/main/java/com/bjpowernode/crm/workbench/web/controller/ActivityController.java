package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.PaginationVO;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到市场活动控制器");
        String path = request.getServletPath();
        if ("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }
        else if ("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }
        else if ("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }
        else if ("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }
        else if ("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }
        else if ("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }
        else if ("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }
        else if ("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        }
        else if ("/workbench/activity/deleteRemark.do".equals(path)) {
            deleteRemark(request, response);
        }
        else if ("/workbench/activity/saveRemark.do".equals(path)) {
            saveRemark(request, response);
        }

    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行添加备注的操作");
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId ");
        String id =UUIDUtil.getUUID();
        String createTime= DateTimeUtil.getSysTime();
        String createBy= ((User)request.getSession().getAttribute("user")).getName();
        String editFlag="0";
        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(id);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setActivityId(activityId);
        activityRemark.setCreateBy(createBy);
        activityRemark.setCreateTime(createTime);
        activityRemark.setEditFlag(editFlag);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag =activityService.saveRemark(activityRemark);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("activityRemark",activityRemark);
        PrintJson.printJsonObj(response,map);

    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除备注操作");
        String id = request.getParameter("id");
        ActivityService activityService= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);


    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        String activityId = request.getParameter("activityId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> activityRemarkList = activityService.getRemarkListByAid(activityId);
        PrintJson.printJsonObj(response,activityRemarkList);


    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到详细信息页的操作");
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity =activityService.detail(id);
        request.setAttribute("activity",activity);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);

    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改市场活动");
        String id= request.getParameter("id");
        String owner= request.getParameter("owner");
        String name= request.getParameter("name");
        String startDate= request.getParameter("startDate");
        String endDate= request.getParameter("endDate");
        String cost= request.getParameter("cost");
        String description= request.getParameter("description");
        String editTime= DateTimeUtil.getSysTime();
        String editBy= ((User)request.getSession().getAttribute("user")).getName();
        Activity activity= new Activity();
        activity.setCost(cost);
        activity.setEditBy(editBy);
        activity.setEditTime(editTime);
        activity.setDescription(description);
        activity.setEndDate(endDate);
        activity.setStartDate(startDate);
        activity.setName(name);
        activity.setOwner(owner);
        activity.setId(id);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.update(activity);
        PrintJson.printJsonFlag(response,flag);


    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单挑记录的操作");
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
        总结：controller调用service的方法，返回值应该是什么
              决定是前端需要的是什么
              前端需要ulist和a（activity）
              以上两项信息，复用率不高，我们选择使用map来打包这两项信息
        */
        Map<String,Object> map = activityService.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动删除操作");
        String ids[]=request.getParameterValues("id");
        ActivityService activityService= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.delete(ids);
        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入查询市场活动信息列表的操作（结合条件查询和分页查询）");
        String name= request.getParameter("name");
        String owner= request.getParameter("owner");
        String startDate= request.getParameter("startDate");
        String endDate= request.getParameter("endDate");
        //每页展现的记录数
        String pageNumStr= request.getParameter("pageNum");
        int pageNum = Integer.valueOf(pageNumStr);
        //计算出略过的记录数
        String pageSizeStr= request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount =(pageNum-1)*pageSize;
        //创建一个map
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);
        //Service层调用pageList方法，返回一个VO对象给controller去返回给前端
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
       PaginationVO<Activity> paginationVO = activityService.pageList(map);
       PrintJson.printJsonObj(response,paginationVO);


    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");
        UserService userService= (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("添加市场活动");
        String id= UUIDUtil.getUUID();
        String owner= request.getParameter("owner");
        String name= request.getParameter("name");
        String startDate= request.getParameter("startDate");
        String endDate= request.getParameter("endDate");
        String cost= request.getParameter("cost");
        String description= request.getParameter("description");
        String createTime= DateTimeUtil.getSysTime();
        String createBy= ((User)request.getSession().getAttribute("user")).getName();
        Activity activity= new Activity();
        activity.setCost(cost);
        activity.setCreateBy(createBy);
        activity.setCreateTime(createTime);
        activity.setDescription(description);
        activity.setEndDate(endDate);
        activity.setStartDate(startDate);
        activity.setName(name);
        activity.setOwner(owner);
        activity.setId(id);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.save(activity);
        PrintJson.printJsonFlag(response,flag);
    }

}
