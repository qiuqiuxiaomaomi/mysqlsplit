package com.splittree.service;

import com.splittree.dao.mapper.OrgMapper;
import com.splittree.entity.Org;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangmingquan on 2018/9/4.
 */
@Service("orgService")
public class OrgService {
    @Autowired
    OrgMapper orgMapper;
    @Value("${spring.datasource.test1.url}")
    String test1Url;
    @Value("${spring.datasource.test2.url}")
    String test2Url;
    @Value("${spring.datasource.test3.url}")
    String test3Url;
    @Value("${spring.datasource.test1.username}")
    String userName;
    @Value("${spring.datasource.test1.password}")
    String password;
    @Value("${spring.datasource.test1.driver-class-name}")
    String driverName;


    public void updateTransactional(Org org) throws SQLException, ClassNotFoundException {
        try{
            //插入所有的数据库
            insertCommon(test1Url, org);
            insertCommon(test2Url, org);
            insertCommon(test3Url, org);
        }catch(Exception e){
            throw e;
        }

    }

    public List<Org> getTransactional() {
        List<Org> objectList = null;
        try{
            objectList = orgMapper.getAll();
        }catch(Exception e){
            throw e;   // 事物方法中，如果使用trycatch捕获异常后，需要将异常抛出，否则事物不回滚。
        }
        return objectList;
    }

    public void insertCommon(String dbUrl, Org org) throws SQLException, ClassNotFoundException {
        try {
            Connection conn = null;
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbUrl, userName, password);
            String sql = "INSERT INTO t_org(id,name,code)VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, org.getId());
            ps.setString(2, org.getName());
            ps.setString(3, org.getCode());
            ps.execute();
            ps.close();
            conn.close();
        } catch (Exception e){
            throw e;
        }
    }
}
