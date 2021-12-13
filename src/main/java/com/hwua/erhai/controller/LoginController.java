package com.hwua.erhai.controller;

import com.hwua.erhai.entity.User;
import com.hwua.erhai.model.MUser;
import com.hwua.erhai.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private IUserService userService;

    @RequestMapping(path = "/doLogin", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, String username, String password) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(300000);

        User user = userService.login(username, password);
        if (user == null) {
            session.setAttribute("result", "failed");
            return "redirect:/jsp/login.jsp";
        }

        session.setAttribute("result", "succeed");
        session.setAttribute("username", username);

        MUser mUser = new MUser();
        mUser.setId(String.valueOf(user.getId()));
        mUser.setUsername(String.valueOf(user.getUsername()));
        mUser.setPassword(String.valueOf(user.getPassword()));
        mUser.setSex(user.getSex() == 0 ? "男" : "女");
        mUser.setIdNumber(String.valueOf(user.getIdNumber()));
        mUser.setTel(String.valueOf(user.getTel()));
        mUser.setAddr(String.valueOf(user.getAddr()));
        mUser.setType(user.getType() == 1 ? "管理员" : "普通用户");
        mUser.setImagePath(String.format("upload/user_%d.img",user.getId()));

        session.setAttribute("mUser", mUser);
        return "redirect:carList";
    }
    @RequestMapping(path = "loginOut",method = RequestMethod.GET)
    public String exitLogin(HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:jsp/login.jsp";
    }
}
