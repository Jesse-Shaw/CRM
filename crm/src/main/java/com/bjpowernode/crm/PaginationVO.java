package com.bjpowernode.crm;

import java.util.List;

public class PaginationVO<T> {
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    private  int total;
    private List<T> dataList;

}
