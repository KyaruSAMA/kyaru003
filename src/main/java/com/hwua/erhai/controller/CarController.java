package com.hwua.erhai.controller;

import com.google.common.base.Preconditions;
import com.hwua.erhai.common.FileUploadUtil;
import com.hwua.erhai.common.PageNavUtil;
import com.hwua.erhai.common.model.MPageNav;
import com.hwua.erhai.entity.Car;
import com.hwua.erhai.entity.Car4Ext;
import com.hwua.erhai.model.MCar;
import com.hwua.erhai.model.MCarSearch;
import com.hwua.erhai.model.MUser;
import com.hwua.erhai.service.ICarService;
import com.hwua.erhai.vo.CarListRequest;
import com.hwua.erhai.vo.DoCarDeleteResponse;
import com.hwua.erhai.vo.DoCarUpdateUsableResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CarController {

    @Autowired
    private ICarService carService;

    @RequestMapping(path = "/carList", method = RequestMethod.GET)
    public String carList(HttpServletRequest request,
                          CarListRequest carListRequest,
                          Model model) {
        // 根据汽车查询条件的参数，生成用于分页的基准url，也就是baseUrl。
        // 这个baseUrl会用于生成分页导航中的a标签的href属性，也就是超链接的值。
        // 同时，还会生成数据库SQL查询的条件清单，也就是queryConditions
        List<String> params = new ArrayList<>();
        Map<String, Object> queryConditions = new HashMap<>();
        if (StringUtils.isNotEmpty(carListRequest.getCarId())) {
            params.add(String.format("carId=%s", carListRequest.getCarId()));
            queryConditions.put("carId", carListRequest.getCarId());
        }
        if (StringUtils.isNotEmpty(carListRequest.getCarBrand())) {
            params.add(String.format("carBrand=%s", carListRequest.getCarBrand()));
            queryConditions.put("carBrand", carListRequest.getCarBrand());
        }
        if (StringUtils.isNotEmpty(carListRequest.getCarCategory())) {
            params.add(String.format("carCategory=%s", carListRequest.getCarCategory()));
            queryConditions.put("carCategory", carListRequest.getCarCategory());
        }
        if (StringUtils.isNotEmpty(carListRequest.getPriceOrder())) {
            params.add(String.format("priceOrder=%s", carListRequest.getPriceOrder()));
            queryConditions.put("priceOrder", carListRequest.getPriceOrder());
        }

        HttpSession session = request.getSession(false);
        MUser mUser = (MUser) session.getAttribute("mUser");
        if ("普通用户".equals(mUser.getType())) {
            queryConditions.put("usable", "0");
        }

        String queryParams = String.join("&", params);
        String baseUrl = "carList";
        if (StringUtils.isNotEmpty(queryParams)) {
            baseUrl = baseUrl + "?" + queryParams;
        }

        // 根据指定的条件，查询符合条件的所有汽车的总数，
        // 就相当于 select count(*) from t_car where ${queryConditions}.
        // 获取这个总数的目的，是为了知道分页导航的最大页数是多少，也就是分页导航中尾页的索引号
        int records = (int) carService.countCars(queryConditions);

        // 根据基准url baseUrl，查询页面索引号，每页的记录数，总记录数 这4个参数，
        // 生成分页导航的Model数据，也就是MPageNav
        MPageNav mPageNav = PageNavUtil.genMPageNav(baseUrl,
                carListRequest.getPage(), carListRequest.getPageSize(), records);
        model.addAttribute("mPageNav", mPageNav);

        // 根据查询条件queryConditions，以及分页导航中指定页面需要查询的数据范围，也就是limit和offset参数，
        // 查询出页面需要的数据
        queryConditions.put("limit", Integer.parseInt(mPageNav.limit));
        queryConditions.put("offset", Integer.parseInt(mPageNav.offset));
        List<Car4Ext> cars = carService.queryCars(queryConditions);
        // 查询出汽车列表数据后，将其按照JSP页面需要的字段，转化为Model数据
        List<MCar> mCars = new ArrayList<>(cars.size());
        for (Car4Ext car : cars) {
            MCar mCar = new MCar();
            mCar.setId(String.valueOf(car.getId()));
            mCar.setModel(String.valueOf(car.getModel()));
            mCar.setComments(String.valueOf(car.gettComments()));
            mCar.setBrand(String.valueOf(car.getBrandName()));
            mCar.setCategory(String.valueOf(car.getCategoryName()));
            mCar.setRent(String.format("%.2f", car.getRent()));
            mCar.setUsable(car.getUsable() == 0 ? "是" : "否");
            mCar.setStatus(car.getStatus() == 0 ? "是" : "否");
            mCars.add(mCar);
        }
        // 将model数据放入request，之后在JSP中，能够通过requestScope访问到model数据
        model.addAttribute("mCars", mCars);

        // MCarSearch用于保存查询的条件，将其按照查询传入的值，填写到搜索表单中
        MCarSearch mCarSearch = new MCarSearch(
                carListRequest.getCarId(), carListRequest.getCarBrand(),
                carListRequest.getCarCategory(), carListRequest.getPriceOrder());
        model.addAttribute("mCarSearch", mCarSearch);

        // 使用forward方法，将M和V结合起来，从而生成动态的html页面，并最终返回给客户端浏览器
        // 这就是完整的MVC处理过程
//        request.getRequestDispatcher("/carList.jsp").forward(request, response);
        return "carList";
    }

    @RequestMapping(path = "/carAdd", method = RequestMethod.GET)
    public String carAdd() {
        return "carAdd";
    }

    @RequestMapping(path = "/doCarAdd", method = RequestMethod.POST)
    public String doCarAdd(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Map<String, FileItem> fileItemMap = new HashMap<>();
        try {
            FileUploadUtil.checkMultipartRequest(request);

            ServletFileUpload upload = FileUploadUtil.prepareFileUpload();

            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    fileItemMap.put(item.getFieldName(), item);
                }
            }

            Preconditions.checkNotNull(fileItemMap.get("brand"), "表单缺少brand");
            String brand = fileItemMap.get("brand").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(brand), "品牌不能为空");

            Preconditions.checkNotNull(fileItemMap.get("category"), "表单缺少category");
            String category = fileItemMap.get("category").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(category), "类型不能为空");

            Preconditions.checkNotNull(fileItemMap.get("model"), "表单缺少model");
            String model = fileItemMap.get("model").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(model), "型号不能为空");

            Preconditions.checkNotNull(fileItemMap.get("carNumber"), "表单缺少carNumber");
            String carNumber = fileItemMap.get("carNumber").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(carNumber), "车牌号不能为空");

            Preconditions.checkNotNull(fileItemMap.get("comments"), "表单缺少comments");
            String comments = fileItemMap.get("comments").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(comments), "简介不能为空");

            Preconditions.checkNotNull(fileItemMap.get("color"), "表单缺少color");
            String color = fileItemMap.get("color").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(color), "颜色不能为空");

            Preconditions.checkNotNull(fileItemMap.get("price"), "表单缺少price");
            String price = fileItemMap.get("price").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(price), "价格不能为空");

            Preconditions.checkNotNull(fileItemMap.get("rent"), "表单缺少rent");
            String rent = fileItemMap.get("rent").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(rent), "租金不能为空");

            String usable = fileItemMap.get("usable") == null ? "" :
                    fileItemMap.get("usable").getString("UTF-8");

            Car car = new Car();
            car.setId(null);
            car.setCarNumber(carNumber);
            car.setBrandId(carService.getBrandByName(brand).getId());
            car.setModel(model);
            car.setColor(color);
            car.setCategoryId(carService.getCategoryByName(category).getId());
            car.settComments(comments);
            car.setPrice(Float.parseFloat(price));
            car.setRent(Float.parseFloat(rent));
            car.setStatus(0);
            car.setUsable("on".equals(usable) ? 0 : 1);
            Car newCar = carService.addCar(car);
            if (newCar == null) {
                throw new Exception("新增汽车失败");
            } else {
                FileItem imageItem = fileItemMap.get("image");
                if (imageItem.getSize() > 0) {
                    long id = newCar.getId();
                    String fileName = String.format("car_%d.img", id);
                    String filePath = FileUploadUtil.getUploadPath() + File.separator + fileName;
                    File storeFile = new File(filePath);
                    // 在控制台输出文件的上传路径
                    System.out.println(filePath);
                    // 保存文件到硬盘
                    imageItem.write(storeFile);
                }
            }
            session.setAttribute("result", "succeed");
            session.setAttribute("message", "新增汽车成功！");
        } catch (Exception ex) {
            session.setAttribute("result", "failed");
            session.setAttribute("message", "新增汽车失败，错误信息: " + ex.getMessage());
        }

        return "redirect:/jsp/doCarAdd.jsp";
    }

    @RequestMapping(path = "/carUpdate", method = RequestMethod.GET)
    public String carUpdate(HttpServletRequest request,
                            String carId, Model model) {
        if (StringUtils.isBlank(carId)) {
            model.addAttribute("message", "carId不能为空");
            return "carFoundError";
        }

        Map<String, Object> queryConditions = new HashMap<>();
        queryConditions.put("carId", carId);
        List<Car4Ext> carList = carService.queryCars(queryConditions);
        if (carList == null || carList.size() == 0) {
            model.addAttribute("message", "找不到该汽车");
            return "carFoundError";
        }

        if (carList.size() > 1) {
            model.addAttribute("message", "找到多辆汽车");
            return "carFoundError";
        }

        Car4Ext car = carList.get(0);
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
        model.addAttribute("mCar", mCar);
        return "carUpdate";
    }

    @RequestMapping(path = "/doCarUpdate", method = RequestMethod.POST)
    public String doCarUpdate(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Map<String, FileItem> fileItemMap = new HashMap<>();
        try {
            FileUploadUtil.checkMultipartRequest(request);
            ServletFileUpload upload = FileUploadUtil.prepareFileUpload();
            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    fileItemMap.put(item.getFieldName(), item);
                }
            }

            Preconditions.checkNotNull(fileItemMap.get("carId"), "表单缺少carId");
            String carId = fileItemMap.get("carId").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(carId), "汽车编号不能为空");

            Preconditions.checkNotNull(fileItemMap.get("brand"), "表单缺少brand");
            String brand = fileItemMap.get("brand").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(brand), "品牌不能为空");

            Preconditions.checkNotNull(fileItemMap.get("category"), "表单缺少category");
            String category = fileItemMap.get("category").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(category), "类型不能为空");

            Preconditions.checkNotNull(fileItemMap.get("model"), "表单缺少model");
            String model = fileItemMap.get("model").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(model), "型号不能为空");

            Preconditions.checkNotNull(fileItemMap.get("carNumber"), "表单缺少carNumber");
            String carNumber = fileItemMap.get("carNumber").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(carNumber), "车牌号不能为空");

            Preconditions.checkNotNull(fileItemMap.get("comments"), "表单缺少comments");
            String comments = fileItemMap.get("comments").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(comments), "简介不能为空");

            Preconditions.checkNotNull(fileItemMap.get("color"), "表单缺少color");
            String color = fileItemMap.get("color").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(color), "颜色不能为空");

            Preconditions.checkNotNull(fileItemMap.get("price"), "表单缺少price");
            String price = fileItemMap.get("price").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(price), "价格不能为空");

            Preconditions.checkNotNull(fileItemMap.get("rent"), "表单缺少rent");
            String rent = fileItemMap.get("rent").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(rent), "租金不能为空");

            String usable = fileItemMap.get("usable") == null ? "" :
                    fileItemMap.get("usable").getString("UTF-8");


            Car car = carService.queryCarById(Integer.parseInt(carId));
            if (car == null) {
                request.setAttribute("message", "找不到该汽车");
                return "carFoundError";
            }

            car.setCarNumber(carNumber);
            car.setBrandId(carService.getBrandByName(brand).getId());
            car.setModel(model);
            car.setColor(color);
            car.setCategoryId(carService.getCategoryByName(category).getId());
            car.settComments(comments);
            car.setPrice(Float.parseFloat(price));
            car.setRent(Float.parseFloat(rent));
            car.setUsable("on".equals(usable) ? 0 : 1);

            Car newCar = carService.updateCar(car);
            if (newCar == null) {
                throw new Exception("更新汽车失败");
            } else {
                FileItem imageItem = fileItemMap.get("image");
                if (imageItem.getSize() > 0) {
                    long id = newCar.getId();
                    String fileName = String.format("car_%d.img", id);
                    String filePath = FileUploadUtil.getUploadPath() + File.separator + fileName;
                    File storeFile = new File(filePath);
                    // 在控制台输出文件的上传路径
                    System.out.println(filePath);
                    // 保存文件到硬盘
                    imageItem.write(storeFile);
                }
            }
            session.setAttribute("result", "succeed");
            session.setAttribute("message", "修改汽车成功！");
        } catch (Exception ex) {
            session.setAttribute("result", "failed");
            session.setAttribute("message", "修改汽车失败，错误信息: " + ex.getMessage());
        }

        return "redirect:/jsp/doCarUpdate.jsp";
    }

    @RequestMapping(path = "/doCarDelete", method = RequestMethod.POST)
    @ResponseBody
    public DoCarDeleteResponse doCarDelete(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String carId) {
        response.setCharacterEncoding("utf-8");//设置服务器端编码
        response.setContentType("text/html;charset=utf-8");//设置浏览器端解码

        if (StringUtils.isBlank(carId)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new DoCarDeleteResponse(
                    HttpServletResponse.SC_BAD_REQUEST, "请求参数为空", carId);
        }

        Car car = carService.deleteCarById(Integer.parseInt(carId));
        if (car == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new DoCarDeleteResponse(
                    HttpServletResponse.SC_BAD_REQUEST, "找不到汽车", carId);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return new DoCarDeleteResponse(
                HttpServletResponse.SC_OK, "删除汽车成功", carId);
    }

    @RequestMapping(path = "/carDetail", method = RequestMethod.GET)
    public String carDetail(String carId, Model model) {
        if (StringUtils.isBlank(carId)) {
            model.addAttribute("message", "汽车编号为空");
            return "carFoundError";
        }

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("carId", carId);
        List<Car4Ext> carList = carService.queryCars(conditions);
        if (carList == null || carList.size() == 0) {
            model.addAttribute("message", "找不到该汽车");
            return "carFoundError";
        }

        if (carList.size() > 1) {
            model.addAttribute("message", "找到多辆汽车");
            return "carFoundError";
        }

        Car4Ext car = carList.get(0);
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

        model.addAttribute("mCar", mCar);
        return "carDetail";
    }

    @RequestMapping(path = "/doCarUpdateUsable", method = RequestMethod.POST)
    @ResponseBody
    public DoCarUpdateUsableResponse doCarUpdateUsable(HttpServletRequest request,
                                                       String carId,
                                                       String usable,
                                                       HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");//设置服务器端编码
        response.setContentType("text/html;charset=utf-8");//设置浏览器端解码

        if (StringUtils.isBlank(carId) || StringUtils.isBlank(usable)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new DoCarUpdateUsableResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "请求参数为空", carId, usable);
        }

        Car car = carService.queryCarById(Integer.parseInt(carId));
        if (car == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new DoCarUpdateUsableResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "找不到该汽车", carId, usable);
        }
        car.setUsable("是".equals(usable.trim()) ? 0 : 1);
        Car newCar = carService.updateCar(car);
        if (newCar == null) {
            return new DoCarUpdateUsableResponse(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "服务器内部错误，更新汽车上下架状态失败", carId, usable);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return new DoCarUpdateUsableResponse(HttpServletResponse.SC_OK, "更新汽车上下架状态成功",
                String.valueOf(car.getId()), String.valueOf(car.getUsable()));
    }
}
