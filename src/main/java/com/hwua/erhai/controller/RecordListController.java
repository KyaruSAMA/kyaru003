package com.hwua.erhai.controller;

import com.hwua.erhai.common.PageNavUtil;
import com.hwua.erhai.common.model.MPageNav;
import com.hwua.erhai.entity.Car4Ext;
import com.hwua.erhai.entity.Record4Ext;
import com.hwua.erhai.model.MRecord;
import com.hwua.erhai.model.MRecordSearch;
import com.hwua.erhai.model.MUser;
import com.hwua.erhai.service.ICarService;
import com.hwua.erhai.vo.RecordRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RecordListController {
    @Autowired
    private ICarService carService;


    @RequestMapping(path = "rentList",method = RequestMethod.GET)
    public String recordList(HttpServletRequest request, RecordRequest recordRequest, Model model){
        List<String> params = new ArrayList<>();
        Map<String, Object> queryConditions = new HashMap<>();
        if (StringUtils.isNoneEmpty(recordRequest.getCarId())) {
            params.add(String.format("carId=%s", recordRequest.getCarId()));
            queryConditions.put("carId", recordRequest.getCarId());
        }

        HttpSession session = request.getSession(false);
        MUser mUser = (MUser) session.getAttribute("mUser");

        if ("普通用户".equals(mUser.getType())) {
            params.add(String.format("userId=%s", mUser.getId()));
            queryConditions.put("userId", mUser.getId());
        }else  if (StringUtils.isNoneEmpty(recordRequest.getUserId())) {
            params.add(String.format("userId=%s", recordRequest.getUserId()));
            queryConditions.put("userId", recordRequest.getUserId());
        }
        /*if (StringUtils.isNoneEmpty(recordRequest.getUserId())) {
            params.add(String.format("userId=%s", recordRequest.getUserId()));
            queryConditions.put("userId", recordRequest.getUserId());
        }*/
        if (StringUtils.isNoneEmpty(recordRequest.getUsername())) {
            params.add(String.format("username=%s", recordRequest.getUsername()));
            queryConditions.put("username", recordRequest.getUsername());
        }
        String queryParams = String.join("&", params);
        String baseUrl = "rentList";
        if (StringUtils.isNoneEmpty(queryParams)) {
            baseUrl = baseUrl + "?" + queryParams;
        }

        int records1 = (int)carService.countRecord4Ext(queryConditions);

        MPageNav mPageNav = PageNavUtil.genMPageNav(baseUrl, recordRequest.getPage(), recordRequest.getPageSize(), records1);
        request.setAttribute("mPageNav", mPageNav);

        queryConditions.put("limit", Integer.parseInt(mPageNav.limit));
        queryConditions.put("offset", Integer.parseInt(mPageNav.offset));

        List<Record4Ext> records = carService.queryRecord4Ext(queryConditions);

        List<MRecord> mRecords = new ArrayList<>(records.size());

        for (Record4Ext record : records) {
            MRecord mRecord = new MRecord();
            mRecord.setId(String.valueOf(record.getId()));
            mRecord.setUserId(String.valueOf(record.getUserId()));
            mRecord.setCarId(String.valueOf(record.getCarId()));
            mRecord.setModel(String.valueOf(record.getModel()));
            mRecord.setStatus(String.valueOf(record.getStatus()));
            if (record.getPayment() != null){
                mRecord.setPayment(String.valueOf(record.getPayment()));
            }else {
                mRecord.setPayment("");
            }
            mRecord.setRent(String.valueOf(record.getRent()));
            mRecord.setComments(String.valueOf(record.gettComments()));

            /*mRecord.setBrandName(String.valueOf(carService.queryBrandById(record.getBrandId()).getName()));
            mRecord.setCategoryName(String.valueOf(carService.queryCategoryById(record.getCategoryId()).getName()));*/
            Map<String, Object> conditions = new HashMap<>();
            if (StringUtils.isNoneEmpty(String.valueOf(record.getCarId()))) {
                queryConditions.put("carId", record.getCarId());
            }
            List<Car4Ext> car4Exts = carService.queryCars(conditions);
            for (Car4Ext c : car4Exts){
                mRecord.setBrandName(c.getBrandName());
                mRecord.setCategoryName(c.getCategoryName());
            }

            mRecord.setUserName(String.valueOf(record.getUsername()));

            SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
            String startDate = dateformat.format(record.getStartDate());
            mRecord.setStartDate(startDate);
            if (record.getReturnDate() == null){
                mRecord.setReturnDate("");
            }else {
                String returnDate = dateformat.format(record.getReturnDate());
                mRecord.setReturnDate(returnDate);
            }
            mRecords.add(mRecord);
        }
        request.setAttribute("mRecords", mRecords);

        MRecordSearch mRecordSearch = new MRecordSearch(recordRequest.getCarId(),recordRequest.getUserId());
        request.setAttribute("mRecordSearch", mRecordSearch);

        return "rentList";

    }
}
