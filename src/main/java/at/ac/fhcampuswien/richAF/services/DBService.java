package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.data.EventManager;
import at.ac.fhcampuswien.richAF.model.*;
import at.ac.fhcampuswien.richAF.crawler.Crawler;
import at.ac.fhcampuswien.richAF.mesh.Node;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
     * @param config
     * @param em EventManager object for logging
     */
    public DBService(Config config, EventManager em)  {
        _config = config;
        _url = _config.getProperty("db.url");
        _em = em;
        Connection conn = getConnection();

        if (conn == null) {
            //_em.logErrorMessage("DBService Constructor: Could not connect to database");
            return;
        }
        try {
            conn.close();
            _context = new DBContext( config,allTablesExist(),_em );
        } catch (SQLException e) {
            _em.logErrorMessage(e);
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
    // * @param keyword ... keyword, most likely a company name which the urls have to contain, for no keyword "", the keyword will then also be put in the keyword collumn in tblPage
     */
    public void SavePagesFromCrawler(Crawler crawler, String keyword) {
        for (Node node : crawler.getMesh().getNodes()) {
            if (keyword != "")
                if(!node.getPage().getUri().toString().toLowerCase().contains(keyword.toLowerCase()))
                    continue;

            try  (Connection conn = DriverManager.getConnection(_url);){
                String raw = node.getPage().getRawContent();

                if (conn != null) {
                    String sql = "INSERT INTO tblPages(intStatus, strPage, tsCreated_on, strKeyword) VALUES(?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, Enums_.Status.NEW.ordinal());
                        pstmt.setString(2, raw);
                        pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                        pstmt.setString(4, keyword);
                        pstmt.executeUpdate();

                    } catch (SQLException e) {
                        _em.logErrorMessage(e);
                        //_em.logErrorMessage("SavePagesFromCrawler INSERT:"+e.getMessage());
                    }
                }
            } catch (SQLException e) {
                _em.logErrorMessage(e);
                //_em.logErrorMessage("SavePagesFromCrawler:"+e.getMessage());
            }

        }

    }

    /**
     * returns all tblPages from the DB
     * @return ArrayList<tblPage> of all Entries in the DB
     */
    public ArrayList<tblPage> getPages() {
        return getPages(Enums_.Status.ALL,"");
    }

    /**
     * returns the tblPages from the DB with the Status and the keyword in the columns
     * @param status ... Enumerator Status to be returned, Enums_.Status.ALL if the Status does not matter
     * @param keyword ... keyword of the pages  to be returned. "" if the keyword shall does not matter
     * @return ArrayList<tblPage> of the pages requested
     */
    public ArrayList<tblPage> getPages(Enums_.Status status, String keyword) {
        // counter if there is a where set in the select
        int wcount= 0;
        ArrayList<tblPage> result = new ArrayList<>();
        Dao<tblPage, Integer> daoTblPage = _context.getTblPageDao();

        try {
            if ((status == Enums_.Status.ALL) && (keyword == "" )) {
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
                if(keyword != "")
                    if (wcount==0)
                        where.eq("strKeyword", keyword);
                    else //if where is already in use add an and
                        where.and().eq("strKeyword", keyword);

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
     * @param companyid the companyid from tblCompany
     */
    public void addJob(String paragraph, int companyid) {
        try  (Connection conn = DriverManager.getConnection(_url);){

            if (conn != null) {
                String sql = "INSERT INTO tblJobs (intStatus, strParagraphs, tsCreated_on, intCompanyID) VALUES(?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Enums_.Status.NEW.ordinal());
                    pstmt.setString(2, paragraph);
                    pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(4, companyid);
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
     */
    public tblCompany GetCompanyById(int id){
        try {
            return _context.getTblCompanyDao().queryForId(id);
        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("GetCompanyById:"+e.getMessage());
        }
        return null;
    }

    /**
     * returns all Entries of tblCompany
     * @return
     */
    public List<tblCompany> GetAllCompanys(){
        try {
            return _context.getTblCompanyDao().queryForAll();
        } catch (SQLException e) {
            _em.logErrorMessage(e);
            //_em.logErrorMessage("GetAllCompanys:"+e.getMessage());
        }
        return null;
    }

    /**
     * returns the tblCompany Object to the given name, if the ecxact name is not in the Database the company will be added and returned
     *  TODO checking why the DAO Object throws exception when inserting, while this happens Insert via preparedStatements
     *  TODO add case sensitive check for the name
     *  @param name name of the company
     * @return tblCompany
     */
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
    }

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
     *  Inserts a result in tblResults
     *  TODO checking why the DAO Object throws exception when inserting, while this happens Insert via preparedStatements
     * @param jobid the jobid of the Job which lead to results
     * @param companyid the company which this result should be accounted on
     * @param pos number of positive news mentions
     * @param neg number of negative news mentions
     */
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
}
