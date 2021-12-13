package com.hwua.erhai.controller;

import com.hwua.erhai.entity.Car4Ext;
import com.hwua.erhai.entity.Record;
import com.hwua.erhai.entity.Record4Ext;
import com.hwua.erhai.model.MCar;
import com.hwua.erhai.model.MRecord;
import com.hwua.erhai.model.MUser;
import com.hwua.erhai.service.ICarService;
import com.hwua.erhai.service.IUserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.hwua.erhai.util.Util;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class rentCarController {
    @Autowired
    ICarService carService;
    @Autowired
    IUserService userService;

    @RequestMapping(path = "doRentCar1", method = RequestMethod.GET)
    public String rentCar(HttpServletRequest request) {
        String carId = request.getParameter("carId");
        HttpSession session = request.getSession();

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("carId", carId);
        List<Car4Ext> carList = carService.queryCars(conditions);
        if (carList == null || carList.size() == 0) {
            request.setAttribute("message", "找不到汽车");
            return "carFoundError";
        }

        if (carList.size() > 1) {
            request.setAttribute("message", "找到多辆汽车");
            return "carFoundError";
        }

        Car4Ext car = carList.get(0);

        if (car.getStatus() == 1) {
            request.setAttribute("result", "isRent");
            request.setAttribute("message", "该车已被租出!!!");
            return "carFoundError";
        }

        MCar mCar = new MCar();
        mCar.setId(String.valueOf(car.getId()));
        mCar.setModel(String.valueOf(car.getModel()));
        mCar.setColor(String.valueOf(car.getColor()));
        mCar.setComments(String.valueOf(car.gettComments()));
        mCar.setCarNumber(String.valueOf(car.getCarNumber()));
        mCar.setBrand(String.valueOf(car.getBrandName()));
        mCar.setCategory(String.valueOf(car.getCategoryName()));
        mCar.setPrice(String.valueOf(car.getPrice()));
        mCar.setRent(String.format("%.2f", car.getRent()));
        mCar.setUsable(car.getUsable() == 0 ? "是" : "否");
        mCar.setStatus(car.getStatus() == 0 ? "是" : "否");
        mCar.setImagePath(String.format("upload/car_%d.img?t=%d", car.getId(), System.currentTimeMillis()));

        request.setAttribute("mCar", mCar);
        session.setAttribute("mCar", mCar);

        return "rentCar";
    }

    @RequestMapping(path = "doRentCar", method = RequestMethod.GET)
    public String doRentCar(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String carId = request.getParameter("carId");
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("carId", carId);
        List<Car4Ext> carList = carService.queryCars(conditions);
        if (carList == null || carList.size() == 0) {
            request.setAttribute("message", "找不到汽车");
            return "carFoundError";
        }

        if (carList.size() > 1) {
            request.setAttribute("message", "找到多辆汽车");
            return "carFoundError";
        }

        Car4Ext car = carList.get(0);

        if (car.getStatus() == 1) {
            request.setAttribute("result", "isRent");
            request.setAttribute("message", "该车已被租出!!!");
            return "carFoundError";
        }
        try {
            MUser mUser = (MUser) session.getAttribute("mUser");
            String userId = mUser.getId();

            if (StringUtils.isBlank(carId) || StringUtils.isBlank(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("请求参数为空");
            }

            Record r = new Record();
            r.setId(null);
            r.setCarId(Integer.parseInt(carId));
            r.setUserId(Integer.parseInt(userId));

            String dateStr = Util.today();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sf.parse(dateStr);
            r.setStartDate(date);
//            r.setStartDate(Util.parseDate(Util.today()));
            r.setReturnDate(null);
            r.setPayment(null);
            Record record = carService.rentCar(r);
            if (record == null) {
                throw new Exception("租车失败");
            }
            session.setAttribute("result", "succeed");
            session.setAttribute("massage", "租车成功！");
        } catch (Exception ex) {
            session.setAttribute("result", "failed");
            session.setAttribute("massage", "租车失败，错误信息：" + ex.getMessage());
        }
        return "doRentCar";
    }

    @RequestMapping(path = "returnCar", method = RequestMethod.GET)
    public String returnCar(HttpServletRequest request, String id, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Map<String, Object> conditions = new HashMap<>();
        if (StringUtils.isNoneEmpty(id)) {
            conditions.put("id", id);
        }
        List<Record4Ext> recordList = carService.queryRecord4Ext(conditions);

        if (recordList == null || recordList.size() == 0) {
            request.setAttribute("message", "找不到记录");
            return "recordFoundError";
        }

        if (recordList.size() > 1) {
            request.setAttribute("message", "找到多条记录");
            return "recordFoundError";
        }

        Record4Ext record = recordList.get(0);
        MRecord mRecord = new MRecord();
        mRecord.setId(String.valueOf(record.getId()));
        mRecord.setUserId(String.valueOf(record.getUserId()));
        mRecord.setCarId(String.valueOf(record.getCarId()));
        mRecord.setModel(String.valueOf(carService.queryCarById(record.getCarId()).getModel()));

        Record record1 = carService.queryNotReturnRecord(record.getUserId(), record.getCarId());

        Map<String, Object> queryConditions = new HashMap<>();
        queryConditions.put("carId", record1.getCarId());

        List<Car4Ext> carList = carService.queryCars(queryConditions);
        for (Car4Ext c : carList) {

            Date startTime = record.getStartDate();
            Date endTime = Util.parseDate(Util.today());
            long minus = endTime.getTime() - startTime.getTime();
            double payment = 0;
            if (minus > 1) {
                payment = record.getRent() * (minus / 24 / 3600 / 1000);
            } else {
                payment = record.getRent();
            }
            mRecord.setPayment(String.valueOf(payment));

            mRecord.setRent(String.valueOf(c.getRent()));
            mRecord.setComments(String.valueOf(c.gettComments()));
            mRecord.setBrandName(String.valueOf(c.getBrandName()));
            mRecord.setCategoryName(String.valueOf(c.getCategoryName()));

            mRecord.setUserName(String.valueOf(userService.queryUserById(record1.getUserId()).getUsername()));

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = dateformat.format(record1.getStartDate());
            mRecord.setStartDate(startDate);

            mRecord.setReturnDate(Util.today());
        }
        request.setAttribute("mRecord", mRecord);
        request.getRequestDispatcher("/doReturnCar1").forward(request,response);
        return null;
    }

    @RequestMapping(path = "doReturnCar1", method = RequestMethod.GET)
    public String doReturnCar(HttpServletRequest request, Model model, HttpServletResponse response) {
        HttpSession session = request.getSession(false);

        try {
            MRecord mRecord = (MRecord) request.getAttribute("mRecord");
            String carId = mRecord.getCarId();

            MUser mUser = (MUser) session.getAttribute("mUser");
            String userId = mUser.getId();

            if ("管理员".equals(mUser.getType())) {
                userId = mRecord.getUserId();
            }

            if (StringUtils.isBlank(carId) || StringUtils.isBlank(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("请求参数为空");

            }
            Record record = new Record();
            record.setId(Integer.parseInt(mRecord.getId()));
            record.setCarId(Integer.parseInt(carId));
            record.setUserId(Integer.parseInt(userId));
            record.setPayment(Float.valueOf(mRecord.getPayment()));
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sf.parse(mRecord.getStartDate());
            record.setStartDate(date1);

            Date date2 = sf.parse(Util.today());
            record.setReturnDate(date2);
            Record r = carService.returnCar(record);
            if (r == null) {
                throw new Exception("还车失败");
            }
            session.setAttribute("result", "succeed");
            session.setAttribute("massage", "还车成功！");
        } catch (Exception ex) {
            session.setAttribute("result", "failed");
            session.setAttribute("massage", "还车失败，错误信息：" + ex.getMessage());
        }

        return "doReturnCar";
    }
}
