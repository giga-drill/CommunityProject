package com.wang.community.controller;

import com.wang.community.entity.DiscussPost;
import com.wang.community.entity.User;
import com.wang.community.service.DiscussPostService;
import com.wang.community.util.CommunityUtil;
import com.wang.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPost;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscuss(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403, "需要登录才能发布");
        }
        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setUserId(user.getId());

        discussPost.addDiscussPost(post);

        //报错的情况将来处理
        return CommunityUtil.getJSONString(0, "发布成功");

    }
}
