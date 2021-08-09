package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

public interface ClueService {

    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbond(String id);

    boolean bond(String clueId, String[] activityIds);


    boolean convert(String clueId, Tran t, String createBy);
}
