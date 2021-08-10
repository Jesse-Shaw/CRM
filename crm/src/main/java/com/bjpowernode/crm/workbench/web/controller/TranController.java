package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易控制器");
        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)) {
            add(request,response);
        }
        else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request,response);
        }
        else if ("/workbench/transaction/save.do".equals(path)) {
        save(request,response);
        }
        else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request,response);
        }
        else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)) {
            getHistoryListByTranId(request,response);
        }
        else if ("/workbench/transaction/changeState.do".equals(path)) {
            changeState(request,response);
        }

    }

    private void changeState(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行阶段改变操作");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy= ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.changeState(t);
        Map<String,String> pMap= (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("t",t);
        PrintJson.printJsonObj(response,map);

    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据交易ID获取相应的历史列表");
        String tranId = request.getParameter("tranId");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> tranHistoryList=tranService.getHistoryListByTranId(tranId);
        Map<String,String> pMap= (Map<String, String>) this.getServletContext().getAttribute("pMap");
        //将列表遍历
        for (TranHistory tranHistory:tranHistoryList) {
            String stage = tranHistory.getStage();
            String possibility =pMap.get(stage);
            tranHistory.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,tranHistoryList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到详细信息页");
        String id = request.getParameter("id");
        TranService tranService= (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = tranService.detail(id);
        //处理可能性
        /*
          需要阶段t.stage和可能性之间的对应关系pMap

        */
        String stage = t.getStage();
        Map<String,String> pMap= (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility =pMap.get(stage);
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行添加交易的操作");
        String id= UUIDUtil.getUUID();
        String owner= request.getParameter("owner");
        String money= request.getParameter("money");
        String name= request.getParameter("name");
        String expectedDate= request.getParameter("expectedDate");
        String customerName= request.getParameter("customerName");//此处只有客户名称无id
        String stage= request.getParameter("stage");
        String type= request.getParameter("type");
        String source= request.getParameter("source");
        String activityId= request.getParameter("activityId");
        String contactsId= request.getParameter("contactsId");
        String createBy= ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description= request.getParameter("description");
        String contactSummary= request.getParameter("contactSummary");
        String nextContactTime= request.getParameter("nextContactTime");
        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateBy(createBy);
        t.setCreateTime(createTime);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.save(t,customerName);
        if (flag){
            //request.getRequestDispatcher("workbench/transaction/index.jsp").forward(request,response);1.request域没存值，2.需要刷新新的页面
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得客户列表按照客户名称进行模糊查询");
        String name = request.getParameter("name");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> stringList = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,stringList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到交易添加页的操作");
        UserService userService= (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList =userService.getUserList();
        request.setAttribute("userList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
