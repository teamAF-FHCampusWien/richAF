package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.model.*;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;


public class DBContext {
    private ConnectionSource connectionSource;
    private Dao<tblPage, Integer> daoTblPage;
    private Dao<tblJob, Integer> daoTblJob;
    private Dao<tblCompany, Integer> daoTblCompany;
    private Dao<tblResult, Integer> daoTblResult;
    private Config _config;
    public DBContext(Config config, Boolean booTablesExists) throws SQLException {
        _config = config;
        connectionSource = new JdbcConnectionSource(config.getProperty("db.url"));
        daoTblPage = DaoManager.createDao(connectionSource, tblPage.class);
        daoTblJob = DaoManager.createDao(connectionSource, tblJob.class);
        daoTblCompany = DaoManager.createDao(connectionSource, tblCompany.class);
        daoTblResult = DaoManager.createDao(connectionSource, tblResult.class);
        if (!booTablesExists){
            TableUtils.createTableIfNotExists(connectionSource, tblPage.class);
            TableUtils.createTableIfNotExists(connectionSource, tblJob.class);
            TableUtils.createTableIfNotExists(connectionSource, tblCompany.class);
            TableUtils.createTableIfNotExists(connectionSource, tblResult.class);
        }

    }

    public Dao<tblPage, Integer> getTblPageDao() {
        return daoTblPage;
    }

    public Dao<tblJob, Integer> getTblJobDao() {
        return daoTblJob;
    }

    public Dao<tblCompany, Integer> getTblCompanyDao() {
        return daoTblCompany;
    }

    public Dao<tblResult, Integer> getTblResultDao() {
        return daoTblResult;
    }

    public void close() throws SQLException, IOException {
        connectionSource.close();
    }

}
