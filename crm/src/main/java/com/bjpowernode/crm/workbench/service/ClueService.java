package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Clue;

public interface ClueService {

    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbond(String id);

    boolean bond(String clueId, String[] activityIds);
}
