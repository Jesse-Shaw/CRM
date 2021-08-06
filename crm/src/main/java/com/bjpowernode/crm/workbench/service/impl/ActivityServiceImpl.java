package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.PaginationVO;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);

    @Override
    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if (count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取得dataList
        List<Activity> dataList = activityDao.getActivityByCondition(map);
        //将total和dataList封装到VO中返回
        PaginationVO<Activity> paginationVO= new PaginationVO<>();
        paginationVO.setTotal(total);
        paginationVO.setDataList(dataList);
        return paginationVO;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag =true;
        //先查询出需要删除备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        //删除备注，返回收到影响的条数
        int count2 = activityRemarkDao.deleteByAids(ids);
        if (count1!=count2){
            flag=false;
        }
        //删除市场活动
        int count3 = activityDao.delete(ids);
        if (count3!= ids.length){
            flag=false;
        }
        return flag;
    }
}
