package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao =SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private TranDao tranDao=SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue clue) {
        boolean flag =true;
        int count =clueDao.save(clue);
        if (count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue clue = clueDao.detail(id);
        return clue;
    }

    @Override
    public boolean unbond(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbond(id);
        if (count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean bond(String clueId, String[] activityIds) {
        boolean flag = true;
        for (String activityId:activityIds) {
            //取得每个activityId与clueId关联
            ClueActivityRelation clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setId(UUIDUtil.getUUID());
            clueActivityRelation.setActivityId(activityId);
            clueActivityRelation.setClueId(clueId);
            int count = clueActivityRelationDao.bond(clueActivityRelation);
            if (count!=1){
                flag=false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        String createTime = DateTimeUtil.getSysTime();
        Boolean flag = true;
        //(1)通过线索id获取线索对象
        Clue clue = clueDao.getById(clueId);
        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        if (customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setContactSummary(clue.getContactSummary());
            //添加客户
            int count=customerDao.save(customer);
            if (count!=1){
                flag=false;
            }
        }
        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setId(UUIDUtil.getUUID());
        contacts.setEmail(clue.getEmail());
        contacts.setEditTime(clue.getEditTime());
        contacts.setEditBy(clue.getEditBy());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());
        //添加联系人
        int count2 = contactsDao.save(contacts);
        if (count2!=1){
            flag=false;
        }
       // (4) 线索备注转换到客户备注以及联系人备注
        //查询出与该线索相关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        //取出每一个线索备注
        for (ClueRemark clueRemark:clueRemarkList) {
            //取出备注信息，主要转换到客户备注和联系人备注的就是这个信息
            String noteContent = clueRemark.getNoteContent();
            //创建客户的备注对象，添加客户备注
            CustomerRemark customerRemark =new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
           int count3 = customerRemarkDao.save(customerRemark);
            if (count3!=1){
                flag=false;
            }
            //创建联系人备注对象。添加联系人
            ContactsRemark contactsRemark =new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4 = contactsRemarkDao.save(contactsRemark);
            if (count4!=1){
                flag=false;
            }

        }
        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //先查询出与该条线索关联的市场活动，查询与市场活动关联的关系列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList) {
            //从每条记录中获取activityId
            String activityId = clueActivityRelation.getActivityId();
            //创建联系人与市场活动的关联关系对象，让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            //添加联系人与市场活动的关联关系
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5!=1){
                flag=false;
            }
        }
        //	(6) 如果有创建交易需求，创建一条交易  如果t！null
        if (t!=null){
            //t对象在controller里已经封装好的信息如下：
           // id, money,name,expectedDate,stage,activityId,createTime,createBy
            //接下来可以通过第一步生成的clue对象，取出一些 信息继续完善对t对象的封装
            t.setSource(clue.getSource());
            t.setOwner(clue.getOwner());
            t.setDescription(clue.getDescription());
            t.setCustomerId(customer.getId());
            t.setContactSummary(contacts.getContactSummary());
            t.setContactsId(contacts.getId());
            t.setNextContactTime(clue.getNextContactTime());
            //添加交易对象
            int count6 = tranDao.save(t);
            if (count6!=1){
                flag=false;
            }
            //	(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(t.getExpectedDate());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setMoney(t.getMoney());
            tranHistory.setStage(t.getStage());
            tranHistory.setTranId(t.getId());
            //添加交易历史
            int count7 = tranHistoryDao.save(tranHistory);
            if (count7!=1){
                flag=false;
            }
        }
        //(8)删除线索备注
        for (ClueRemark clueRemark:clueRemarkList) {
            int count8 = clueRemarkDao.delete(clueRemark);
            if (count8!=1){
                flag=false;
            }
        }
        //(9) 删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList) {
            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if (count9!=1){
                flag=false;
            }
        }
        //(10) 删除线索
        int count10 = clueDao.delete(clueId);
        if (count10!=1){
            flag=false;
        }
        return flag;
    }


}
