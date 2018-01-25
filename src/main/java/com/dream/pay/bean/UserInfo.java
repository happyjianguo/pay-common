package com.dream.pay.bean;

import lombok.Data;

@Data
/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption 用户类<br/>
 */
public class UserInfo {

    private Integer id;
    //中文名称
    private String aliasname;
    //手机号
    private String mobile;
    //邮箱
    private String email;
    //用户名
    private String username;
    //真实姓名
    private String realname;
    //性别
    private boolean gender;

}
