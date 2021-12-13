package com.hwua.erhai.controller;

import com.google.common.base.Preconditions;
import com.hwua.erhai.common.FileUploadUtil;
import com.hwua.erhai.entity.User;
import com.hwua.erhai.service.IUserService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserAddController {
    @Autowired
    private IUserService userService;

    @RequestMapping(path = "doUserAdd", method = RequestMethod.GET)
    public String addUser() {
        return "userAdd";
    }

    @RequestMapping(path = "doUserAdd", method = RequestMethod.POST)
    public String doAddUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        Map<String, FileItem> fileItemMap = new HashMap<>();
        try {
            // 检测是否为多媒体上传
            FileUploadUtil.checkMultipartRequest(request);

            // 解析请求的内容提取文件数据
            ServletFileUpload upload = FileUploadUtil.prepareFileUpload();
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 将表单里的参数，放置到fileItemMap表中，方便后面通过参数名直接取值
                    fileItemMap.put(item.getFieldName(), item);
                }
            }
            Preconditions.checkNotNull(fileItemMap.get("username"), "表单缺少username");
            String username = fileItemMap.get("username").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(username), "用户名不能为空");

            Preconditions.checkNotNull(fileItemMap.get("password"), "表单缺少password");
            String password = fileItemMap.get("password").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(password), "密码不能为空");

            Preconditions.checkNotNull(fileItemMap.get("sex"), "表单缺少sex");
            String sex = fileItemMap.get("sex").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(sex), "性别不能为空");

            Preconditions.checkNotNull(fileItemMap.get("idNumber"), "表单缺少idNumber");
            String idNumber = fileItemMap.get("idNumber").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(idNumber), "身份证号不能为空");

            Preconditions.checkNotNull(fileItemMap.get("tel"), "表单缺少tel");
            String tel = fileItemMap.get("tel").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(tel), "电话不能为空");

            Preconditions.checkNotNull(fileItemMap.get("addr"), "表单缺少addr");
            String addr = fileItemMap.get("addr").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(addr), "地址不能为空");

            Preconditions.checkNotNull(fileItemMap.get("type"), "表单缺少type");
            String type = fileItemMap.get("type").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(type), "角色不能为空");

            User u1 = userService.queryUserName(username);
            if (u1 != null) {
                username = "";
                Preconditions.checkArgument(StringUtils.isNotBlank(username), "该用户名已存在，请使用其他用户名");
            }
            User u2 = userService.queryUserIdNumber(idNumber);
            if (u2 != null) {
                idNumber = "";
                Preconditions.checkArgument(StringUtils.isNotBlank(idNumber), "身份证号输入错误");
            }
            User user = new User();
            user.setId(null);
            user.setUsername(username);
            user.setPassword(password);
            user.setSex(Integer.parseInt(sex));
            user.setIdNumber(idNumber);
            user.setTel(tel);
            user.setAddr(addr);
            user.setType(Integer.parseInt(type));
            User newUser = userService.addUser(user);
            Preconditions.checkNotNull(newUser, "新增用户失败");

            FileItem imageItem = fileItemMap.get("image");
            if (imageItem.getSize() > 0) {
                long id = newUser.getId();
                String fileName = String.format("user_%d.img", id);
                String filePath = FileUploadUtil.getUploadPath() + File.separator + fileName;
                File storeFile = new File(filePath);
                //在控制台输出文件的上传数据
                System.out.println(filePath);
                //保存文件到硬盘
                imageItem.write(storeFile);
            }

            session.setAttribute("result", "succeed");
            session.setAttribute("message", "新增用户成功！");

        } catch (Exception ex) {
            // 设置新增汽车失败的结果
            session.setAttribute("result", "failed");
            session.setAttribute("message", "新增用户失败，错误信息: " + ex.getMessage());
        }
        return "doUserAdd";
    }

    @RequestMapping(path = "doRegister", method = RequestMethod.POST)
    public String doRegister(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        Map<String, FileItem> fileItemMap = new HashMap<>();
        try {
            // 检测是否为多媒体上传
            FileUploadUtil.checkMultipartRequest(request);

            // 解析请求的内容提取文件数据
            ServletFileUpload upload = FileUploadUtil.prepareFileUpload();
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 将表单里的参数，放置到fileItemMap表中，方便后面通过参数名直接取值
                    fileItemMap.put(item.getFieldName(), item);
                }
            }
            Preconditions.checkNotNull(fileItemMap.get("username"), "表单缺少username");
            String username = fileItemMap.get("username").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(username), "用户名不能为空");

            Preconditions.checkNotNull(fileItemMap.get("password"), "表单缺少password");
            String password = fileItemMap.get("password").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(password), "密码不能为空");

            Preconditions.checkNotNull(fileItemMap.get("sex"), "表单缺少sex");
            String sex = fileItemMap.get("sex").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(sex), "性别不能为空");

            Preconditions.checkNotNull(fileItemMap.get("idNumber"), "表单缺少idNumber");
            String idNumber = fileItemMap.get("idNumber").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(idNumber), "身份证号不能为空");

            Preconditions.checkNotNull(fileItemMap.get("tel"), "表单缺少tel");
            String tel = fileItemMap.get("tel").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(tel), "电话不能为空");

            Preconditions.checkNotNull(fileItemMap.get("addr"), "表单缺少addr");
            String addr = fileItemMap.get("addr").getString("UTF-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(addr), "地址不能为空");

            User u1 = userService.queryUserName(username);
            if (u1 != null) {
                username = "";
                Preconditions.checkArgument(StringUtils.isNotBlank(username), "该用户名已被使用，请使用其他用户名");
            }
            User u2 = userService.queryUserIdNumber(idNumber);
            if (u2 != null) {
                idNumber = "";
                Preconditions.checkArgument(StringUtils.isNotBlank(idNumber), "身份证号输入错误");
            }
            User user = new User();
            user.setId(null);
            user.setUsername(username);
            user.setPassword(password);
            user.setSex(Integer.parseInt(sex));
            user.setIdNumber(idNumber);
            user.setTel(tel);
            user.setAddr(addr);
            user.setType(0);
            User newUser = userService.addUser(user);
            Preconditions.checkNotNull(newUser, "注册失败");

            FileItem imageItem = fileItemMap.get("image");
            if (imageItem.getSize() > 0) {
                long id = newUser.getId();
                String fileName = String.format("user_%d.img", id);
                String filePath = FileUploadUtil.getUploadPath() + File.separator + fileName;
                File storeFile = new File(filePath);
                //在控制台输出文件的上传数据
                System.out.println(filePath);
                //保存文件到硬盘
                imageItem.write(storeFile);
            }

            session.setAttribute("result", "succeed");
            session.setAttribute("message", "注册成功！");

        } catch (Exception ex) {
            // 设置新增汽车失败的结果
            session.setAttribute("result", "failed");
            session.setAttribute("message", "注册失败，错误信息: " + ex.getMessage());
        }
        return "redirect:/jsp/doRegister.jsp";
    }
}
