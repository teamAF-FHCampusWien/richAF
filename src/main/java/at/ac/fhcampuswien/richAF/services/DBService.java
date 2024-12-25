package at.ac.fhcampuswien.richAF.services;

import at.ac.fhcampuswien.richAF.model.*;
//import at.ac.fhcampuswien.richAF.crawler.Crawler;
//import at.ac.fhcampuswien.richAF.mesh.Node;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBService {

    private String _url;
    private Config _config;
    private DBContext _context;



    public DBService(Config config)  {
        _config = config;
        _url = _config.getProperty("db.url");
        Connection conn = getConnection();

        if (conn == null) {
            return;
        }
        try {
            conn.close();
            _context = new DBContext( config,allTablesExist() );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        /*
        if (allTablesExist() == false) {
            System.out.println("Time to create tables");
            initialize();
        }*/


    }

    /// legt die Tabellen an die für den Ollama gebraucht werden
    public void initialize(){
        String sql = "CREATE TABLE IF NOT EXISTS tblJobs (\n"
                + " id integer PRIMARY KEY,\n"
                + " status integer NOT NULL,\n"
                + " paragraphs text NOT NULL,\n"
                + " created_on timestamp NOT NULL\n"
                + ");";

        try ( Connection conn = DriverManager.getConnection(_url);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("tblJobs created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "CREATE TABLE IF NOT EXISTS tblPages (\n"
                + " id integer PRIMARY KEY,\n"
                + " status integer NOT NULL,\n"
                + " page text NOT NULL,\n"
                + " keyword text NOT NULL,\n"
                + " created_on timestamp NOT NULL\n"
                + ");";

        try ( Connection conn = DriverManager.getConnection(_url);
              Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("tblPages created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "CREATE TABLE IF NOT EXISTS tblResults (\n"
                + " id integer PRIMARY KEY,\n"
                + " jobid integer,\n"
                + " status integer NOT NULL,\n"
                + " created_on timestamp NOT NULL\n"
                + ");";

        try ( Connection conn = DriverManager.getConnection(_url);
              Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("tblResult created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



/// liefert die Verbindung zur Datenbank zurück oder legt die Datenbank auch an falls nicht vorhanden
    public Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(_url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("A new database has been created.");
            }
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed.");
            e.printStackTrace();
        }
        return null;
    }

/// controlliert ob die für das Ollama gebrauchten Tabellen angelegt sind
/// enthält zeilen aus Copilot
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
            System.out.println(e.getMessage());
        }
        //COPILOT ENDE

        String cTables =_config.getProperty("db.tables");
        for (String table : tables) {
            cTables = cTables.replace(table, "");
        }
        cTables = cTables.replace(";", "");
        return cTables.equals("");
    }


    /**Noch deaktiviert brauch die Crawler Klasse
     *
     */
    /*
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
                        System.out.println(e.getMessage());
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }

    }*/

    public ArrayList<tblPage> getPages() {
        return getPages(Enums_.Status.ALL,"");
    }

    public ArrayList<tblPage> getPages(Enums_.Status status, String keyword) {
        int wcount= 0;
        ArrayList<tblPage> result = new ArrayList<>();
        Dao<tblPage, Integer> daoTblPage = _context.getTblPageDao();

        try {
            if ((status == Enums_.Status.ALL) && (keyword == "" )) {
                result.addAll(daoTblPage.queryForAll());

            }else {
                QueryBuilder<tblPage, Integer> queryBuilder = daoTblPage.queryBuilder();
                Where<tblPage, Integer> where = queryBuilder.where();
                if (status != Enums_.Status.ALL) {
                    wcount++;
                    where.eq("intStatus", status.ordinal());
                }
                if(keyword != "")
                    if (wcount==0)
                        where.eq("strKeyword", keyword);
                    else
                        where.and().eq("strKeyword", keyword);

                result.addAll(daoTblPage.query(queryBuilder.prepare()));

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;

    }

    public void UpdateStatus(baseTbl tbl, Enums_.Status status){
        tbl.setStatus(status);
        System.out.println(tbl.getClass());

        try {
            if (tbl instanceof tblPage) {
                _context.getTblPageDao().update((tblPage) tbl);
            } else if (tbl instanceof tblJob) {
                _context.getTblJobDao().update((tblJob) tbl);
            } else if (tbl instanceof tblResult) {
                _context.getTblResultDao().update((tblResult) tbl);
            } else {
                System.out.println("Tabellen Objekttyp hat keinen Status");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public tblCompany GetCompanyById(int id){
        try {
            return _context.getTblCompanyDao().queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

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
                        System.out.println(e.getMessage());
                    }
                }

                result = _context.getTblCompanyDao().queryForEq("strName", name);
                if (result.size()>0)
                    return result.get(0);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public ArrayList<tblJob> getjobs() {
        return getJobs(Enums_.Status.ALL);
    }

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
            throw new RuntimeException(e);
        }
        return result;

    }


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
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
