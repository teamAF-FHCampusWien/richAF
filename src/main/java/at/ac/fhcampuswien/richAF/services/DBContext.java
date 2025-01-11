package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.*;

import at.ac.fhcampuswien.richAF.model.dao.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Class containing the Data Access Objects for the Tables
 * @author Stefan
 */
public class DBContext {
    /**
     * Database ConnectionSource
     */
    private ConnectionSource connectionSource;
    /**
     * DAO for the tblPages
     */
    private Dao<tblPage, Integer> daoTblPage;
    /**
     * DAO for the tblJobs
     */
    private Dao<tblJob, Integer> daoTblJob;
    /**
     * DAO for the tblSources
     */
    private Dao<tblSource, Integer> daoTblSource;
    /*does not suit needs anymore
    /**
     * DAO for the tblCompany
     *
    private Dao<tblCompany, Integer> daoTblCompany;

     */
    /**
     * DAO for the tblResult
     */
    private Dao<tblResult, Integer> daoTblResult;

    /**
     * Constructor:
     * creates the JDBC connectionsource to the Database
     * creates the DAOs for the tables with the connectionsource and their corresponding Class Model
     * if not all tables are created then they will here
     * @param config The Config object with the properties from the resources file
     * @param booTablesExists boolean value which holds the information of all Tables are already created
     * @param em EventManager object for logging
     */
    public DBContext(Config config, Boolean booTablesExists, EventManager em){

        try {
            connectionSource = new JdbcConnectionSource(config.getProperty("db.url"));
            daoTblPage = DaoManager.createDao(connectionSource, tblPage.class);
            daoTblJob = DaoManager.createDao(connectionSource, tblJob.class);
            //daoTblCompany = DaoManager.createDao(connectionSource, tblCompany.class);
            daoTblResult = DaoManager.createDao(connectionSource, tblResult.class);
            daoTblSource = DaoManager.createDao(connectionSource, tblSource.class);

            if (!booTablesExists){
                TableUtils.createTableIfNotExists(connectionSource, tblPage.class);
                TableUtils.createTableIfNotExists(connectionSource, tblJob.class);
                //TableUtils.createTableIfNotExists(connectionSource, tblCompany.class);
                TableUtils.createTableIfNotExists(connectionSource, tblResult.class);
                TableUtils.createTableIfNotExists(connectionSource, tblSource.class);
            }
        } catch (SQLException e) {
           em.logErrorMessage(e);
        }

    }

    public Dao<tblPage, Integer> getTblPageDao() {
        return daoTblPage;
    }

    public Dao<tblJob, Integer> getTblJobDao() {
        return daoTblJob;
    }

    /* does not suit needs anymore
    public Dao<tblCompany, Integer> getTblCompanyDao() {
        return daoTblCompany;
    }*/

    public Dao<tblResult, Integer> getTblResultDao() {
        return daoTblResult;
    }

    public Dao<tblSource, Integer> getTblSourceDao() {
        return daoTblSource;
    }

    public void close() throws SQLException, IOException {
        connectionSource.close();
    }

}
