package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran t, String customerName) {
        /*
        交易添加业务，在左添加之前，参数t里少了一项信息，就是客户的主键customerId
        先处理客户相关的需求
        （1）判断customerName，根据客户名称在客户表中进行 精确查询
            如果有这个客户就取出这个客户的id，封装到t对象中
            如果没有这个客户，就在客户表新创建一个客户信息，然后将新创建的客户id取出，封装到t对象中
        （2）经过以上操作后，t对象中的信息就全了，需要执行添加交易的操作
        （3）添加交易完毕后，需要创建一条交易历史
        */
        boolean flag = true;
        Customer customer = customerDao.getCustomerByName(customerName);
        if (customer==null){
            customer= new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(t.getCreateBy());
            customer.setCreateTime(t.getCreateTime());
            customer.setContactSummary(t.getContactSummary());
            customer.setNextContactTime(t.getNextContactTime());
            customer.setOwner(t.getOwner());
            int count1 = customerDao.save(customer);
            if (count1!=1){
                flag=false;
            }
        }
        //通过以上客户处理，获得了客户id
        t.setCustomerId(customer.getId());
        int count2=tranDao.save(t);
        if (count2!=1){
            flag=false;
        }
        //添加交易历史
        TranHistory th =new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(t.getCreateTime());
        th.setCreateBy(t.getCreateBy());
        th.setTranId(t.getId());
        int count3 = tranHistoryDao.save(th);
        if (count3!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran t=tranDao.detail(id);
        return t;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> tranHistoryList=tranHistoryDao.getHistoryListByTranId(tranId);
        return tranHistoryList;
    }

    @Override
    public boolean changeState(Tran t) {
        boolean flag =true;
        //改变交易阶段
        int count =tranDao.changeState(t);
        if (count!=1){
            flag=false;
        }
        //伴随着交易阶段的改变，交易历史应该生产
        TranHistory th = new TranHistory();
        th.setMoney(t.getMoney());
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(t.getEditTime());
        th.setTranId(t.getId());
        th.setExpectedDate(t.getExpectedDate());
        th.setStage(t.getStage());
        int count2 = tranHistoryDao.save(th);
        if (count2!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        //取得total
        int total = tranDao.getTotal();
        //取得datalist
        List<Map<String,Object>> dataList =tranDao.getCharts();
        //将两项打包进map
        Map<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }
}
