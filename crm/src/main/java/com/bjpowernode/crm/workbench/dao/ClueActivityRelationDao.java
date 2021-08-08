package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;

public interface ClueActivityRelationDao {


    int unbond(String id);

    int bond(ClueActivityRelation clueActivityRelation);
}
