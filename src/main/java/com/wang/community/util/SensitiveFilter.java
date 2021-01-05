package com.wang.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            logger.error("加载敏感词失败", e.getMessage());
        }
    }


    //将敏感词添加到前缀树
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subnode = tempNode.getSubNode(c);

            if (subnode == null) {
                // 初始化子节点
                subnode = new TrieNode();
                tempNode.addSubNode(c, subnode);

            }
            //指向子节点，进入下一轮循环

            tempNode = subnode;

            //设置结束标志
            if (i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 需要过滤的文本
     * @return 替换后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text) == true) {
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指2
        int begin = 0;
        //指3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while (begin < text.length()) {

            position = begin;
            tempNode = rootNode;
            while (position < text.length()){

                char c = text.charAt(position);

                //跳过符号
                if (isSysmbol(c)){
                    //若指1处于根节点，存入结果，指2向下一步
                    if (tempNode == rootNode){
                        sb.append(c);
                    }
                    ++position;
                    continue;
                }

                //检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    //begin开头字符不是敏感词
                    sb.append(text.charAt(position));
                    //进入下一个位置
                    break;
                    //重新指向根节点
                    //tempNode = rootNode;
                }
                else if (tempNode.isKeywordEnd()){
                    //发现敏感词
                    sb.append(REPLACEMENT);
                    //  进入下一个位置
                    begin = position;
                    break;
                    //重新指向根节点
                    //tempNode = rootNode;
                }
                else {
                    //检查下一个字符
                    position++;

                }

            }
            begin++;
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();

    }

    //判断是否为符号
    private boolean isSysmbol (Character c){
        //0x2E80 - 0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode {

        // 描述关键词结束的标识
        private boolean isKeywordEnd = false;

        //描述当前节点的子节点,key是下级节点字符，Node是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
