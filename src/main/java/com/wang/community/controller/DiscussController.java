package com.wang.community.controller;

import com.wang.community.entity.Comment;
import com.wang.community.entity.DiscussPost;
import com.wang.community.entity.Page;
import com.wang.community.entity.User;
import com.wang.community.service.CommentService;
import com.wang.community.service.DiscussPostService;
import com.wang.community.service.UserService;
import com.wang.community.util.CommunityConstant;
import com.wang.community.util.CommunityUtil;
import com.wang.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPost;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

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
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model , Page page){
        // 帖子
        DiscussPost post = discussPost.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        page.setPath("/discuss/detail/" + discussPostId);
        page.setLimit(5);
        page.setRows(post.getCommentCount());

        //评论：给帖子的评论
        //回复：对评论的评论

        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(),
                page.getOffset(), page.getLimit());

        // 评论显示列表
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {

                Map<String, Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                // 作者
                commentVO.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment replyComment: replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        // 回复本身
                        replyVO.put("reply", replyComment);
                        // 回复作者
                        replyVO.put("user", userService.findUserById(replyComment.getUserId()));
                        // 回复对象
                        replyVO.put("target", replyComment.getTargetId() == 0 ?
                                null : userService.findUserById(replyComment.getTargetId()));
                        replyVOList.add(replyVO);
                    }
                }
                commentVO.put("replys", replyVOList);
                // 回复数量
                commentVO.put("replyCount", commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId()));

                commentVOList.add(commentVO);


            }
        }
        model.addAttribute("comments", commentVOList);


        return "/site/discuss-detail";
    }
}
