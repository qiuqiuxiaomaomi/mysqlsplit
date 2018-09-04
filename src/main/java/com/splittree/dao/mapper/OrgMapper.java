package com.splittree.dao.mapper;

import com.splittree.entity.Org;

import java.util.List;

/**
 * Created by yangmingquan on 2018/9/4.
 */
public interface OrgMapper {
    List<Org> getAll();

    void insert(Org org);
}
