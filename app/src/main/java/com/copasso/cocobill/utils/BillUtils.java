package com.copasso.cocobill.utils;

import com.copasso.cocobill.model.bean.local.BBill;
import com.copasso.cocobill.model.bean.local.MonthChartBean;
import com.copasso.cocobill.model.bean.local.MonthDetailBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class BillUtils {

    /**
     * 包装账单展示列表
     */
    public static MonthDetailBean packageDetailList(List<BBill> list){
        MonthDetailBean bean=new MonthDetailBean();
        float t_income = 0;
        float t_outcome = 0;
        List<MonthDetailBean.DaylistBean> daylist=new ArrayList<>();
        List<BBill> beanList=new ArrayList<>();
        float income = 0;
        float outcome = 0;

        String preDay = "";  //记录前一天的时间
        for (int i=0;i<list.size();i++){
            BBill bBill=list.get(i);
            //计算总收入支出
            if (bBill.isIncome())
                t_income+=bBill.getCost();
            else
                t_outcome+=bBill.getCost();
            //判断后一个账单是否于前者为同一天
            if(preDay.equals(DateUtils.getDay(bBill.getCrdate()))){
                if (bBill.isIncome())
                    income+=bBill.getCost();
                else
                    outcome+=bBill.getCost();
                beanList.add(bBill);
                if((i+1)==list.size()){
                    //局部变量防止引用冲突
                    List<BBill> tmpList=new ArrayList<>();
                    tmpList.addAll(beanList);

                    MonthDetailBean.DaylistBean tmpDay=new MonthDetailBean.DaylistBean();
                    tmpDay.setList(tmpList);
                    tmpDay.setMoney("支出：" + outcome + " 收入：" + income);
                    tmpDay.setTime(DateUtils.getDay(bBill.getCrdate()));
                    daylist.add(tmpDay);
                }
            }else{
                if(i!=0){
                    //局部变量防止引用冲突
                    List<BBill> tmpList=new ArrayList<>();
                    tmpList.addAll(beanList);

                    MonthDetailBean.DaylistBean tmpDay=new MonthDetailBean.DaylistBean();
                    tmpDay.setList(tmpList);
                    tmpDay.setMoney("支出：" + outcome + " 收入：" + income);
                    tmpDay.setTime(preDay);
                    daylist.add(tmpDay);
                    //清空前一天的数据
                    beanList.clear();
                    income=0;
                    outcome=0;
                }
                beanList.add(bBill);
                preDay=DateUtils.getDay(bBill.getCrdate());
            }
        }

        bean.setT_income(String.valueOf(t_income));
        bean.setT_outcome(String.valueOf(t_outcome));
        bean.setDaylist(daylist);
        return bean;
    }

    public static MonthChartBean packageChartList(List<BBill> list) {
        MonthChartBean bean=new MonthChartBean();
        float t_income = 0;
        float t_outcome = 0;

        Map<String,List<BBill>> mapIn=new HashMap<>();
        Map<String,Float> moneyIn=new HashMap<>();
        Map<String,List<BBill>> mapOut=new HashMap<>();
        Map<String,Float> moneyOut=new HashMap<>();
        for (int i=0;i<list.size();i++) {
            BBill bBill = list.get(i);
            //计算总收入支出
            if (bBill.isIncome()) t_income+=bBill.getCost();
            else t_outcome+=bBill.getCost();

            //账单分类
            String sort=bBill.getSortName();
            List<BBill> listBill;
            if(bBill.isIncome()){
                if(mapIn.containsKey(sort)){
                    listBill=mapIn.get(sort);
                    moneyIn.put(sort,moneyIn.get(sort)+bBill.getCost());
                }else {
                    listBill=new ArrayList<>();
                    moneyIn.put(sort,bBill.getCost());
                }
                listBill.add(bBill);
                mapIn.put(sort,listBill);
            }else {
                if(mapOut.containsKey(sort)){
                    listBill=mapOut.get(sort);
                    moneyOut.put(sort,moneyOut.get(sort)+bBill.getCost());
                }else {
                    listBill=new ArrayList<>();
                    moneyOut.put(sort,bBill.getCost());
                }
                listBill.add(bBill);
                mapOut.put(sort,listBill);
            }
        }

        List<MonthChartBean.SortTypeList> outSortlist = new ArrayList<>();    //账单分类统计支出
        List<MonthChartBean.SortTypeList> inSortlist=new ArrayList<>();    //账单分类统计收入

        for (Map.Entry<String,List<BBill>> entry : mapOut.entrySet()) {
            MonthChartBean.SortTypeList sortTypeList=new MonthChartBean.SortTypeList();
            sortTypeList.setList(entry.getValue());
            sortTypeList.setSortName(entry.getKey());
            sortTypeList.setSortImg(entry.getValue().get(0).getSortImg());
            sortTypeList.setMoney(moneyOut.get(entry.getKey()));
            sortTypeList.setBack_color(StringUtils.randomColor());
            outSortlist.add(sortTypeList);
        }
        for (Map.Entry<String,List<BBill>> entry : mapIn.entrySet()) {
            MonthChartBean.SortTypeList sortTypeList=new MonthChartBean.SortTypeList();
            sortTypeList.setList(entry.getValue());
            sortTypeList.setSortName(entry.getKey());
            sortTypeList.setSortImg(entry.getValue().get(0).getSortImg());
            sortTypeList.setMoney(moneyIn.get(entry.getKey()));
            sortTypeList.setBack_color(StringUtils.randomColor());
            inSortlist.add(sortTypeList);
        }

        bean.setInSortlist(inSortlist);
        bean.setOutSortlist(outSortlist);
        bean.setTotalIn(t_income);
        bean.setTotalOut(t_outcome);

        return bean;
    }
}