package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.Map;

public interface ActivityService {
    boolean save(Activity activity);

    PaginationVO<Activity> pageList(Map<String, Object> map);
}
