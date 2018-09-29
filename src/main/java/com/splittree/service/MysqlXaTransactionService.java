package com.splittree.service;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;
import org.springframework.beans.BeansException;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yangmingquan on 2018/9/29.
 */
@Service
public class MysqlXaTransactionService {
    public static Map<String, String> propertiesMap = new HashMap<>();
    private Properties props;
    private String propertyfile = "jdbc.properties";

    private String sql_1 = "delete from t_order_0 where order_id =0;";
    private String sql_2 = "insert into t_order_0(order_id, user_id,status) values(4,4,0);";


    public void mysqlXaTransactionServiceExcute() {
        Connection connection_1 = null;
        Connection connection_2 = null;
        DruidXADataSource xaDataSource_1 = null;
        DruidXADataSource xaDataSource_2 = null;
        Xid xid_1 = null;
        Xid xid_2 = null;
        XAConnection xaConnection_1 = null;
        XAConnection xaConnection_2 = null;
        XAResource xaResource_1 = null;
        XAResource xaResource_2 = null;

        try {
            props = PropertiesLoaderUtils.loadAllProperties(propertyfile);
            processProperties(props);
        } catch (IOException io) {
            System.err.println("Error while accessing the properties file (" + propertyfile + "). Abort.");
            System.exit(1);
        }

        DruidXADataSource[] xaDataSources = initXADataSource();
        xaDataSource_1 = xaDataSources[0];
        xaDataSource_2 = xaDataSources[1];

        XAConnection[] xaConnections = initXAConnection(xaDataSource_1, xaDataSource_2);
        xaConnection_1 = xaConnections[0];
        xaConnection_2 = xaConnections[1];

        xaResource_1 = initXAResource(xaConnection_1);
        xaResource_2 = initXAResource(xaConnection_2);

        connection_1 = getDatabaseConnection(xaConnection_1);
        connection_2 = getDatabaseConnection(xaConnection_2);

        // create a separate branch for a common transaction
        Xid[] xids = createXID();
        xid_1 = xids[0];
        xid_2 = xids[1];

        try {
            execBranch(connection_1, xaResource_1, xid_1, sql_1);
            execBranch(connection_2, xaResource_2, xid_2, sql_2);

            if (prepareCommit(xaResource_1, xid_1) == XAResource.XA_OK &&
                    prepareCommit(xaResource_2, xid_2) == XAResource.XA_OK) {
                commitBranch(xaResource_1, xid_1);
                commitBranch(xaResource_2, xid_2);
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            rollbackBranch(xaResource_1, xid_1);
            rollbackBranch(xaResource_2, xid_2);
        }
    }

    DruidXADataSource[] initXADataSource() {
        System.out.print("Create a XADataSource_1 data source: ");
        DruidXADataSource xaDataSource_1 = new DruidXADataSource();
        xaDataSource_1.setDbType(getProperty("db1.dbtype"));
        xaDataSource_1.setUrl(getProperty("db1.url"));
        xaDataSource_1.setUsername(getProperty("db1.username"));
        xaDataSource_1.setPassword(getProperty("db1.password"));
        System.out.println("Okay.");

        System.out.print("Create a XADataSource_2 data source: ");
        DruidXADataSource xaDataSource_2 = new DruidXADataSource();
        xaDataSource_2.setDbType(getProperty("db2.dbtype"));
        xaDataSource_2.setUrl(getProperty("db2.url"));
        xaDataSource_2.setUsername(getProperty("db2.username"));
        xaDataSource_2.setPassword(getProperty("db2.password"));
        System.out.println("Okay.");
        return new DruidXADataSource[]{xaDataSource_1, xaDataSource_2};
    }

    XAConnection[] initXAConnection(DruidXADataSource xaDataSource_1, DruidXADataSource xaDataSource_2) {
        XAConnection xaconn_1 = null;
        XAConnection xaconn_2 = null;
        try {
            System.out.print("Set up DB_1 XA connection: ");
            xaconn_1 = xaDataSource_1.getXAConnection();
            System.out.println("Okay.");

            System.out.print("Set up DB_2 XA connection: ");
            xaconn_2 = xaDataSource_2.getXAConnection();
            System.out.println("Okay.");
        } catch (SQLException e) {
            sqlerr(e);
        }
        return new XAConnection[]{xaconn_1, xaconn_2};
    }

    XAResource initXAResource(XAConnection xacon) {
        XAResource xares = null;
        try {
            System.out.print("Setting up a XA resource: ");
            xares = xacon.getXAResource();
            System.out.println("Okay.");
        } catch (SQLException e) {
            sqlerr(e);
        }
        return xares;
    }

    Connection getDatabaseConnection(XAConnection xacon) {
        Connection con = null;
        try {
            System.out.print("Establish database connection: ");
            con = xacon.getConnection();
            con.setAutoCommit(false);
            System.out.println("Okay.");
        } catch (SQLException e) {
            sqlerr(e);
        }
        return con;
    }

    Xid[] createXID() {
        Xid xid_1 = null;
        byte[] gid_1 = new byte[1];
        byte[] bid_1 = new byte[1];
        gid_1[0] = (Byte.decode(getProperty("xid.global"))).byteValue();
        bid_1[0] = (Byte.decode(getProperty("xid.branch.db_1"))).byteValue();
        System.out.print("Creating an XID (" + Byte.toString(gid_1[0]) + ", " + Byte.toString(bid_1[0]) + ") for DB_1: ");
        xid_1 = new MysqlXid(gid_1, bid_1, 0);
        System.out.println("Okay.");

        Xid xid_2 = null;
        byte[] gid_2 = new byte[1];
        byte[] bid_2 = new byte[1];
        gid_2[0] = (Byte.decode(getProperty("xid.global"))).byteValue();
        bid_2[0] = (Byte.decode(getProperty("xid.branch.db_2"))).byteValue();
        System.out.print("Creating an XID (" + Byte.toString(gid_2[0]) + ", " + Byte.toString(bid_2[0]) + ") for DB_2: ");
        xid_2 = new MysqlXid(gid_2, bid_2, 0);
        System.out.println("Okay.");
        return new Xid[]{xid_1, xid_2};
    }

    void execBranch(Connection con, XAResource xares, Xid xid, String sql) {
        try {
            xares.start(xid, XAResource.TMNOFLAGS);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            xares.end(xid, XAResource.TMSUCCESS);
        } catch (XAException e) {
            System.err.println("XA exception caught:");
            System.err.println("Cause  : " + e.getCause());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SQLException e) {
            sqlerr(e);
            throw new RuntimeException(e);
        }
    }

    int prepareCommit(XAResource xares, Xid xid) {
        int rc = 0;
        System.out.print("Prepare XA branch (" +
                Byte.toString((xid.getGlobalTransactionId())[0]) + ", " +
                Byte.toString((xid.getBranchQualifier())[0]) + "): ");
        try {
            xares.prepare(xid);
        } catch (XAException e) {
            xaerr(e);
            throw new RuntimeException(e);
        }
        System.out.println("Okay.");
        return rc;
    }

    void commitBranch(XAResource xares, Xid xid) {
        System.out.print("Commit XA branch (" +
                Byte.toString((xid.getGlobalTransactionId())[0]) + ", " +
                Byte.toString((xid.getBranchQualifier())[0]) + "): ");
        try {
            // second parameter is 'false' since we have a two phase commit
            xares.commit(xid, false);
        } catch (XAException e) {
            xaerr(e);
            throw new RuntimeException(e);
        }
        System.out.println("Okay.");
    }

    void rollbackBranch(XAResource xares, Xid xid) {
        System.out.print("Rollback XA branch (" +
                Byte.toString((xid.getGlobalTransactionId())[0]) + ", " +
                Byte.toString((xid.getBranchQualifier())[0]) + "): ");
        try {
            xares.rollback(xid);
        } catch (XAException e) {
            xaerr(e);
            throw new RuntimeException(e);
        }
        System.out.println("Okay.");
    }

    void sqlerr(SQLException exception) {
        System.err.println("FAILED.");
        while (exception != null) {
            System.err.println("==> SQL Exception caught");
            System.err.println("--> SQLCODE : " + exception.getErrorCode());
            System.err.println("--> SQLSTATE: " + exception.getSQLState());
            System.err.println("--> Message : " + exception.getMessage());
            exception = exception.getNextException();
        }
    }

    void xaerr(XAException exception) {
        System.err.println("FAILED.");
        System.err.println("==> XA Exception caught");
        System.err.println("--> Cause  : " + exception.getCause());
        System.err.println("--> Message: " + exception.getMessage());
        exception.printStackTrace();
    }

    private static void processProperties(Properties props) throws BeansException {
        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            try {
                // PropertiesLoaderUtils的默认编码是ISO-8859-1,在这里转码一下
                propertiesMap.put(keyStr, new String(props.getProperty(keyStr).getBytes("ISO-8859-1"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getProperty(String name) {
        return propertiesMap.get(name).toString();
    }
}
