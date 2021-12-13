package com.hwua.erhai.controller;

import com.google.common.base.Preconditions;
import com.hwua.erhai.common.FileUploadUtil;
import com.hwua.erhai.entity.User;
import com.hwua.erhai.model.MUser;
import com.hwua.erhai.service.IUserService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class UserUpdateController {
    @Autowired
    private IUserService userService;
    @RequestMapping(path = "userUpdate",method = RequestMethod.GET)
    public String userUpdate(HttpServletRequest request,String userId, Model model){

        if (StringUtils.isBlank(userId)) {
            model.addAttribute("message", "userId不能为空");
            return "userFoundError";
        }
        Map<String, Object> queryConditions = new HashMap<>();
        queryConditions.put("userId", userId);

        List<User> userList = userService.queryUserConditions(queryConditions);

        if (userList == null || userList.size() == 0) {
            request.setAttribute("message", "找不到用户");
            return "userFoundError";
        }
        if (userList.size() > 1) {
            request.setAttribute("message", "找到多个用户");
            return "userFoundError";
        }

        User user = userList.get(0);
        MUser mUser = new MUser();
        mUser.setId(String.valueOf(user.getId()));
        mUser.setUsername(String.valueOf(user.getUsername()));
        mUser.setPassword(String.valueOf(user.getPassword()));
        mUser.setSex(user.getSex() == 0 ? "男" : "女");
        mUser.setIdNumber(String.valueOf(user.getIdNumber()));
        mUser.setTel(String.valueOf(user.getTel()));
        mUser.setAddr(String.valueOf(user.getAddr()));
        mUser.setType(user.getType() == 1 ? "管理员" : "普通用户");
        mUser.setImagePath(String.format("upload/user_%d.img?t=%d", user.getId(), System.currentTimeMillis()));

        request.setAttribute("mUser", mUser);
        return "userUpdate";
    }

    @RequestMapping(path = "doUserUpdate",method = RequestMethod.POST)
    public String doUserUpdate(HttpServletRequest request, HttpServletResponse response){
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

            Preconditions.checkNotNull(fileItemMap.get("userId"), "表单缺少userId");
            String userId = fileItemMap.get("userId").getString("utf-8");
            Preconditions.checkArgument(StringUtils.isNotBlank(userId), "用户编号不能为空");

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

            User user = userService.queryUserById(Integer.parseInt(userId));
            if (user == null){
                request.setAttribute("message", "找不到该用户");
                return "userFoundError";
            }

            user.setId(user.getId());
            user.setUsername(username);
            user.setPassword(password);
            user.setSex(Integer.parseInt(sex));
            user.setIdNumber(idNumber);
            user.setTel(tel);
            user.setAddr(addr);
            user.setType(Integer.parseInt(type));

            User newUser = userService.updateUser(user);
            if (newUser == null) {
                throw new Exception("更新用户失败");
            } else {
                FileItem imageItem = fileItemMap.get("image");
                if (imageItem.getSize() > 0) {
                    long id = user.getId();
                    String fileName = String.format("user_%d.img", id);
                    String filePath = FileUploadUtil.getUploadPath() + File.separator + fileName;
                    File storeFile = new File(filePath);
                    //在控制台输出文件的上传数据
                    System.out.println(filePath);
                    //保存文件到硬盘
                    imageItem.write(storeFile);
                }
            }

            session.setAttribute("result", "succeed");
            session.setAttribute("message", "修改用户成功！");


        } catch (Exception ex) {
            session.setAttribute("result", "failed");
            session.setAttribute("message", "修改用户失败，错误信息：" + ex.getMessage());
        }
        return "doUserUpdate";
    }
}
