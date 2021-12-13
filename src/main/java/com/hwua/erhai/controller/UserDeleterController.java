package com.hwua.erhai.controller;

import com.hwua.erhai.entity.User;
import com.hwua.erhai.model.MUser;
import com.hwua.erhai.service.IUserService;
import com.hwua.erhai.vo.DoUserDeleteResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserDeleterController {
    @Autowired
    private IUserService userService;
    @RequestMapping(value = "doUserDelete", method = RequestMethod.POST)
    public DoUserDeleteResponse doUserDelete( HttpServletRequest request,
                                              String userId,
                                              HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");//设置服务器端编码
        response.setContentType("text/html;charset=utf-8");//设置浏览器端解码

        if (StringUtils.isBlank(userId)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new DoUserDeleteResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "请求参数为空",Long.parseLong(userId));
        }

        User user = userService.deleteUser(Integer.parseInt(userId));
       if (user == null){
           response.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return new DoUserDeleteResponse(HttpServletResponse.SC_BAD_REQUEST,
                   "找不到该用户", Long.parseLong(userId));
       }

        HttpSession session = request.getSession(false);
        MUser mUser = (MUser) session.getAttribute("mUser");
        if (userId.equals(mUser.getId())){
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return new DoUserDeleteResponse(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "无法删除自己的信息",Long.parseLong(userId));
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return  new DoUserDeleteResponse(
                HttpServletResponse.SC_OK, "删除用户成功", user.getId()
        );
    }
}
