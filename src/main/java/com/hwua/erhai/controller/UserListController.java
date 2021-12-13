package com.hwua.erhai.controller;

import com.hwua.erhai.common.PageNavUtil;
import com.hwua.erhai.common.model.MPageNav;
import com.hwua.erhai.entity.User;
import com.hwua.erhai.model.MUser;
import com.hwua.erhai.model.MUserSearch;
import com.hwua.erhai.service.IUserService;
import com.hwua.erhai.vo.UserListRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserListController {
    @Autowired
    private IUserService userService;
    @RequestMapping(path = "userList",method = RequestMethod.GET)
    public String userList(HttpServletRequest request, UserListRequest userListRequest){
        List<String> params = new ArrayList<>();
        Map<String, Object> queryConditions = new HashMap<>();
        if (StringUtils.isNoneEmpty(userListRequest.getUserId())) {
            params.add(String.format("userId=%s", userListRequest.getUserId()));
            queryConditions.put("userId", userListRequest.getUserId());
        }

        if (StringUtils.isNoneEmpty(userListRequest.getUsername())) {
            params.add(String.format("username=%s", userListRequest.getUsername()));
            queryConditions.put("username", userListRequest.getUsername());
        }

        if (StringUtils.isNoneEmpty(userListRequest.getUserType())) {
            params.add(String.format("userType=%s", userListRequest.getUserType()));
            queryConditions.put("userType", userListRequest.getUserType());
        }

        String queryParams = String.join("&", params);
        String baseUrl = "userList";
        if (StringUtils.isNoneEmpty(queryParams)) {
            baseUrl = baseUrl + "?" + queryParams;
        }

        int records = (int)userService.countUser4Ext(queryConditions);
        MPageNav mPageNav = PageNavUtil.genMPageNav(baseUrl, userListRequest.getPage(), userListRequest.getPageSize(), records);
        request.setAttribute("mPageNav", mPageNav);

        queryConditions.put("limit", Integer.parseInt(mPageNav.limit));
        queryConditions.put("offset", Integer.parseInt(mPageNav.offset));
        List<User> users = userService.queryUserConditions(queryConditions);

        List<MUser> mUsers = new ArrayList<>(users.size());
        for (User user : users) {
            MUser mUser = new MUser();
            mUser.setId(String.valueOf(user.getId()));
            mUser.setUsername(String.valueOf(user.getUsername()));
            mUser.setPassword(String.valueOf(user.getPassword()));
            mUser.setSex(user.getSex() == 0 ? "男" : "女");
            mUser.setIdNumber(String.valueOf(user.getIdNumber()));
            mUser.setTel(String.valueOf(user.getTel()));
            mUser.setAddr(String.valueOf(user.getAddr()));
            mUser.setType(user.getType() == 1 ? "管理员" : "普通用户");
            mUser.setImagePath(String.format("upload/user_%d.img?t=%d",user.getId(),System.currentTimeMillis()));
            mUsers.add(mUser);
        }

        request.setAttribute("mUsers", mUsers);

        MUserSearch mUserSearch = new MUserSearch(userListRequest.getUserId(),userListRequest.getUsername(), userListRequest.getUserType());
        request.setAttribute("mUserSearch", mUserSearch);

        return "userList";
    }
}
