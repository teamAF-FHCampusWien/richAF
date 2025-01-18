package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.controller.Controller;
import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.*;
import at.ac.fhcampuswien.richAF.crawler.Crawler;
import at.ac.fhcampuswien.richAF.mesh.Node;
import at.ac.fhcampuswien.richAF.model.dao.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;

/**
 * Class containing the "Database Service"
 * the Database operations (INSERT, SELECT, UPDATE) are called here and the service uses for most of the time the DAOs
 * @author Stefan
 */
public class DBService {

    private String _url;
    private Config _config;
    private DBContext _context;
    private EventManager _em;

    /**
     * Constructor: setting the path for the Database and try the connection with getConnection
     * then the DBContext is created
     *
     * @param config
     * @param _em
     */
    public DBService(Config config, EventManager _em)  {
        _config = config;
        _url = _config.getProperty("db.url");
        this._em = _em;
        Connection conn = getConnection();

        if (conn == null) {
            //_em.logErrorMessage("DBService Constructor: Could not connect to database");
            return;
        }
        try {
            conn.close();
            _context = new DBContext( config,allTablesExist(), this._em);
        } catch (SQLException e) {
            this._em.logErrorMessage(e);
        }

    }

    /**
     * trys to get a Connection to the Database to the path set in the config.properties
     * if the Database can't be found, it will be created if possible
     * @return the Connection object to the Database created during getConnection
     */
    public Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(_url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                //_em.logInfoMessage("Database Connection Established");
            }
            return conn;
        } catch (ClassNotFoundException e) {
            _em.logFatalMessage(e);
            //_em.logFatalMessage("getConnection:SQLite JDBC driver not found.");
        } catch (SQLException e) {
            _em.logErrorMessage(e);
           // _em.logErrorMessage("getConnection:Connection failed.");
        }
        return null;
    }

    /**
     * checks if all Tables declared in the config properties are also present in the Database
     * How to perform this check was asked in COPILOT
     * @return true for all Tables present, false one or more tables missing
     */
    public boolean allTablesExist() {
        ArrayList<String> tables = new ArrayList<>();
        //COPILOT
        String sql = "SELECT name FROM sqlite_master WHERE type='table'";
        try ( Connection conn = DriverManager.getConnection(_url);
              Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tables.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            _em.logErrorMessage(e);
           // _em.logErrorMessage("allTablesExist:"+e.getMessage());
        }
        //COPILOT ENDE

        String cTables =_config.getProperty("db.tables");
        for (String table : tables) {
            cTables = cTables.replace(table, "");
        }
        cTables = cTables.replace(";", "");
        return cTables.equals("");
    }


    /** DEAKTIVIERT da Crawler noch fehlt
     *  saves all webpages the Crawler has been crawling through his run into the tblPages
     *  if a keyword is given then the url will be searched for it and only if it's mentioned the pages will be accepted
     *  to be saved in the Database
     *  TODO checking why the DAO Object throws exception when inserting, while this happens Insert via preparedStatements
    // * @param crawler .. the webcrawler object
    */
    public void SavePagesFromCrawler(Crawler crawler) {
        for (Node node : crawler.getMesh().getNodes()) {
            try  (Connection conn = DriverManager.getConnection(_url);){
                String raw = node.getPage().getRawContent();
                URI uri = node.getPage().getUri();

                Dao<tblPage, Integer> daoTblPage = _context.getTblPageDao();

                tblPage tp= new tblPage();
                tp.setStatus(Enums_.Status.NEW);
                tp.setStrPage(raw);
                tp.setStrLink(uri.toString());
                tp.setTsCreated_on(new Timestamp(System.currentTimeMillis()));

                daoTblPage.create(tp);

            } catch (SQLException e) {
                _em.logErrorMessage(e);
                //_em.logErrorMessage("SavePagesFromCrawler:"+e.getMessage());
            }

        }

    }

    /**
     * updated die übergebene tblPage
     * @param tp
     */
    public void updatePage(tblPage tp){
        try {
            _context.getTblPageDao().update((tblPage) tp);
        } catch (SQLException e) {
            _em.logErrorMessage(e);
        }
    }

    /**
     * returns all tblPages from the DB
     * @return ArrayList<tblPage> of all Entries in the DB
     */
    public ArrayList<tblPage> getPages() {
        return getPages(Enums_.Status.ALL);
    }

    /**
     * returns the tblPages from the DB with the Status and the keyword in the columns
     * @param status ... Enumerator Status to be returned, Enums_.Status.ALL if the Status does not matter
     * @return ArrayList<tblPage> of the pages requested
     */
    public ArrayList<tblPage> getPages(Enums_.Status status) {
        // counter if there is a where set in the select
        int wcount= 0;
        ArrayList<tblPage> result = new ArrayList<>();
        Dao<tblPage, Integer> daoTblPage = _context.getTblPageDao();

        try {
            if ((status == Enums_.Status.ALL)) {
                // Select query for all entries
                result.addAll(daoTblPage.queryForAll());

            }else {
                // building a Select query with a WHERE clasuse
                QueryBuilder<tblPage, Integer> queryBuilder = daoTblPage.queryBuilder();
                Where<tblPage, Integer> where = queryBuilder.where();
                if (status != Enums_.Status.ALL) {
                    wcount++;
                    where.eq("intStatus", status.ordinal());
                }
                /* not needed anymore
                if(keyword != "")
                    if (wcount==0)
                        where.eq("strKeyword", keyword);
                    else //if where is already in use add an and
                        where.and().eq("strKeyword", keyword);
                    */
                result.addAll(daoTblPage.query(queryBuilder.prepare()));
            }


        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("getPages:"+e.getMessage());
        }
        return result;

    }

    /**
     * updates the Status of an tblObject which extends the baseTbl via its DAO
     * @param tbl
     * @param status
     */
    public void UpdateStatus(baseTbl tbl, Enums_.Status status){
        //set the status of the "line"
        tbl.setStatus(status);

        //Call the update on the corresponding DAO
        try {
            if (tbl instanceof tblPage) {
                _context.getTblPageDao().update((tblPage) tbl);
            } else if (tbl instanceof tblJob) {
                _context.getTblJobDao().update((tblJob) tbl);
            } else if (tbl instanceof tblResult) {
                _context.getTblResultDao().update((tblResult) tbl);
            } else {
                //_em.logErrorMessage("UpdateStatus: table objecttyp has no status Status:"+tbl.getClass().getName());
            }

        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("UpdateStatus:"+e.getMessage());
        }
    }

    /**
     *  Inserts a job in tblPages
     *  TODO checking why the DAO Object throws exception when inserting, while this happens Insert via preparedStatements
     * @param paragraph the paragraphs from the webpages
     */
    public void addJob(String paragraph, int pageid) {
        try  (Connection conn = DriverManager.getConnection(_url);){

            if (conn != null) {
                String sql = "INSERT INTO tblJobs (intStatus, strParagraphs, tsCreated_on, intPageID) VALUES(?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Enums_.Status.NEW.ordinal());
                    pstmt.setString(2, paragraph);
                    pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(4, pageid);
                    pstmt.executeUpdate();

                } catch (SQLException e) {
                    _em.logErrorMessage(e);
                    //_em.logErrorMessage("addJob INSERT:"+e.getMessage());
                }
            }
        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("addJob:"+e.getMessage());
        }

    }

    /**
     * returns the tblCompany line for the company with id in the parameter
     * @param id of the company
     * @return tblCompany
     *//*
    public tblCompany GetCompanyById(int id){
        try {
            return _context.getTblCompanyDao().queryForId(id);
        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("GetCompanyById:"+e.getMessage());
        }
        return null;
    }*/

    /**
     * returns all Entries of tblCompany
     * @return
     *//*
    public List<tblCompany> GetAllCompanys(){
        try {
            return _context.getTblCompanyDao().queryForAll();
        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("GetAllCompanys:"+e.getMessage());
        }
        return null;
    }*/

    /**
     * returns the tblCompany Object to the given name, if the ecxact name is not in the Database the company will be added and returned
     *  TODO checking why the DAO Object throws exception when inserting, while this happens Insert via preparedStatements
     *  TODO add case sensitive check for the name
     *  @param name name of the company
     * @return tblCompany
     *//*
    public tblCompany AddOrGetCompany(String name){

        try {
           List<tblCompany> result = _context.getTblCompanyDao().queryForEq("strName", name);
           if (result.size()>0)
               return result.get(0);

            try  (Connection conn = DriverManager.getConnection(_url);){
                if (conn != null) {
                    String sql = "INSERT INTO tblCompany(strName) VALUES(?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, name);
                        pstmt.executeUpdate();

                    } catch (SQLException e) {
                        _em.logErrorMessage(e);
                       // _em.logErrorMessage("AddOrGetCompany INSERT:"+e.getMessage());
                    }
                }

                result = _context.getTblCompanyDao().queryForEq("strName", name);
                if (result.size()>0)
                    return result.get(0);
            } catch (SQLException e) {
                _em.logErrorMessage(e);
                //_em.logErrorMessage("AddOrGetCompany:"+e.getMessage());
            }

        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("AddOrGetCompany SELECT:"+e.getMessage());
        }

        return null;
    }*/

    /**
     * returns all Jobs from tblJobs
     * @return ArrayList<tblJob>
     */
    public ArrayList<tblJob> getjobs() {
        return getJobs(Enums_.Status.ALL);
    }

    /**
     * return all Jobs which have the Status given
     * @param status Enumerator of the Status the Jobs shall have, Enums_.Status.ALL for all
     * @return ArrayList<tblJob>
     */
    public ArrayList<tblJob> getJobs(Enums_.Status status) {
        ArrayList<tblJob> result = new ArrayList<>();
        Dao<tblJob, Integer> daoTblJob = _context.getTblJobDao();

        try {
            if ((status == Enums_.Status.ALL) ) {
                result.addAll(daoTblJob.queryForAll());

            }else {
                QueryBuilder<tblJob, Integer> queryBuilder = daoTblJob.queryBuilder();
                Where<tblJob, Integer> where = queryBuilder.where();
                if (status != Enums_.Status.ALL) {
                    where.eq("intStatus", status.ordinal());
                }
                result.addAll(daoTblJob.query(queryBuilder.prepare()));
            }

        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("getJobs:"+e.getMessage());
        }
        return result;

    }

    /**
     * @deprecated does not suit our needs anymore
     *  Inserts a result in tblResults
     *  TODO checking why the DAO Object throws exception when inserting, while this happens Insert via preparedStatements
     * @param jobid the jobid of the Job which lead to results
     * @param pos number of positive news mentions
     * @param neg number of negative news mentions
     */
    @Deprecated
    public void addResult(int jobid, int companyid, int pos, int neg) {
        try  (Connection conn = DriverManager.getConnection(_url);){

            if (conn != null) {
                String sql = "INSERT INTO tblResults (intStatus, intJobID, tsCreated_on, intCompanyID, intPositiv, intNegativ) VALUES(?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Enums_.Status.NEW.ordinal());
                    pstmt.setInt(2, jobid);
                    pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(4, companyid);
                    pstmt.setInt(5, pos);
                    pstmt.setInt(6, neg);
                    pstmt.executeUpdate();

                } catch (SQLException e) {
                    _em.logErrorMessage(e);
                    //_em.logErrorMessage("addResult INSERT:"+e.getMessage());
                }
            }
        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("addResult:"+e.getMessage());
        }

    }

    /**
     * Inserts a result in tblResults
     * @param jobid the jobid of the Job which lead to results
     * @param json the content of the response
     */
    public void addResult(int jobid, String json) {
        try  (Connection conn = DriverManager.getConnection(_url);){
            Dao<tblResult, Integer> daoTblResult = _context.getTblResultDao();

            tblResult newRes = new tblResult();
            newRes.setIntJobID(jobid);
            newRes.setStrResponeJson(json);
            newRes.setTsCreated_on(new Timestamp(System.currentTimeMillis()));

            daoTblResult.create(newRes);
        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("addResult:"+e.getMessage());
        }

    }


    /**
     * returns all Entries of tblResults
     * @return
     */
    public ArrayList<tblResult> GetResults(){
        ArrayList<tblResult> result = new ArrayList<>();
        try {
            result.addAll(_context.getTblResultDao().queryForAll());
            return result;
        } catch (SQLException e) {
            _em.logErrorMessage(e);
        }
        return null;
    }


    /**
     * Inserts a source in tblSource
     * @param name short description of the source
     * @param url the url of source
     */
    public void addSource(String name, String url) {
        try  (Connection conn = DriverManager.getConnection(_url);){
            Dao<tblSource, Integer> daoTblSource= _context.getTblSourceDao();
            tblSource ts = new tblSource();
            ts.setName(name);
            ts.setStrUrl(url);
            ts.setTsCreated_on(new Timestamp(System.currentTimeMillis()));
            daoTblSource.create(ts);
        } catch (SQLException e) {
            _em.logErrorMessage(e);
        }

    }

    /**
     * Deletes a source in tblSource
     * @param id id of the source to delete
     */
    public void deleteSource(int id) {
        try  (Connection conn = DriverManager.getConnection(_url);){
            Dao<tblSource, Integer> daoTblSource= _context.getTblSourceDao();
            daoTblSource.deleteById(id);
        } catch (SQLException e) {
            _em.logErrorMessage(e);
        }

    }

    /**
     * returns all Entries of tblSources
     * @return
     */
    public ArrayList<tblSource> getSources(){
        ArrayList<tblSource> result = new ArrayList<>();
        try {
            result.addAll(_context.getTblSourceDao().queryForAll());
            return result;
        } catch (SQLException e) {
            _em.logErrorMessage(e);
        }
        return null;
    }

    public void clearResults() {
        try {
            _context.getTblResultDao().delete(_context.getTblResultDao().queryForAll());
        } catch (SQLException e) {
            _em.logErrorMessage(e);
        }
    }
}
