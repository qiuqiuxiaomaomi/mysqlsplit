package com.splittree.entity;

import javax.persistence.Table;

/**
 * Created by yangmingquan on 2018/9/4.
 */
@Table(name = "t_org")
public class Org {
    private Integer id;

    /**
     * 组织名称
     */
    private String name;

    /**
     * 组织编码
     */
    private String code;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取组织名称
     *
     * @return name - 组织名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置组织名称
     *
     * @param name 组织名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取组织编码
     *
     * @return code - 组织编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置组织编码
     *
     * @param code 组织编码
     */
    public void setCode(String code) {
        this.code = code;
    }
}
