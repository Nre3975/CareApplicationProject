package com.example.tm470_careapp;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    private Connection connection;
    private final String host = "tm470-careappdatabasecontainer.cu4qqwmr1rz0.eu-west-2.rds.amazonaws.com";
    private final String database = "AWSL_CareHomeApp";
    private final int port = 5432;
    private static String user;
    private static String pass;
    private String url = "jdbc:postgresql://%s:%d/%s";
    private boolean status;
    private static Database dbInstance;

    //Get the only object available, or create it if not there.
    public static Database getInstance(String username, String password) {
        try {
            if (dbInstance == null || dbInstance.connection == null || dbInstance.connection.isClosed()) {
                dbInstance = new Database(username, password);
            } else {
                System.out.println("Connection Already Exists.:");
            }
        } catch (SQLException e) {
            Log.e("DB Conn Error", "exception", e);
        }
        return dbInstance;
    }

    //Get the DB Instance, Or ask user to login again if it's gone.
    public static Database getInstance() {
        try {
            if (dbInstance == null || dbInstance.connection == null || dbInstance.connection.isClosed()) {
                System.out.println("Connection Lost:");
                //Reconnect if we still have details.
                if (user != null && pass != null) {
                    dbInstance = new Database(user, pass);
                }
            } else {
                System.out.println("Connection Already Exists.");
            }
        } catch (SQLException e) {
            Log.e("DB Conn Error", "exception", e);
        }
        return dbInstance;
    }

    // Close a connection.
    public static Boolean closeInstance() throws SQLException {
        if (dbInstance == null) {
            System.out.println("No Connection Found.");
            return false;
        } else {
            dbInstance.connection.close();
            return true;
        }
    }

    // Private constructor for singleton pattern.
    private Database(String username, String password) {
        this.url = String.format(this.url, this.host, this.port, this.database);
        user = username;
        pass = password;
        connect();
        System.out.println("connection status:" + status);
    }

    // Check the connections status.
    public Boolean getStatus() {
        return this.status;
    }

    // Create Connection.
    private void connect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            status = true;
        } catch (Exception exception) {
            status = false;
            System.out.print(exception.getMessage());
            exception.printStackTrace();
        }
        System.out.println("connected: " + status);
    }

    // Format resultsets into list of Hashmaps for activities to process.
    private List<HashMap<String, Object>> convertResultSetToList(ResultSet rs) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        try {
            if (rs != null) {
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    HashMap<String, Object> row = new HashMap<>(columns);
                    for (int i = 1; i <= columns; ++i) {
                        if (md.getColumnType(i) == -2) {
                            byte[] fileBytes = rs.getBytes(i);
                            row.put(md.getColumnName(i), fileBytes);
                        } else {
                            row.put(md.getColumnName(i), rs.getObject(i));
                        }
                    }
                    list.add(row);
                }
            }
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return list;
    }

    //Getters.
    //Get current staff User Level.
    public Integer getStaffAccessLevel() {
        Integer staffLevel = 99;
        try {
            Statement st = dbInstance.connection.createStatement();
            String querySQL = "SELECT access_level FROM csa_dbowner.staff s INNER JOIN csa_dbowner.job_roles_lu jrl ON s.job_role_ri = jrl.job_role_id WHERE db_username = (SELECT CURRENT_USER);";
            System.out.println(querySQL);
            ResultSet rs = st.executeQuery(querySQL);
            while (rs.next()) {
                staffLevel = Integer.parseInt(rs.getString("access_level"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return staffLevel;
    }

    //Get any staff User Level.
    public Integer getStaffAccessLevelfromID(Integer staffID) {
        Integer accessLevel = 0;
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("SELECT access_level FROM csa_dbowner.staff s INNER JOIN csa_dbowner.job_roles_lu jrl ON s.job_role_ri = jrl.job_role_id WHERE staff_id = ?)", Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, staffID);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                accessLevel = rs.getInt(1);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return accessLevel;
    }

    //Get current staff User ID.
    public Integer getStaffID() {
        Integer staff_ID = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT staff_id FROM csa_dbowner.staff s WHERE db_username = (SELECT CURRENT_USER);");
            while (rs.next()) {
                staff_ID = Integer.parseInt(rs.getString("staff_id"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return staff_ID;
    }

    // Get db_username for a member of staff.
    public String getdbUserName(Integer staffId) {
        String dbUsername = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT db_username FROM csa_dbowner.staff s WHERE staff_id = " + staffId + ";");
            while (rs.next()) {
                dbUsername = (rs.getString("staff_id"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return dbUsername;
    }

    public Integer getStaffAccessLevelFromID() {
        Integer staff_ID = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT staff_id FROM csa_dbowner.STAFF s WHERE db_username = (SELECT CURRENT_USER);");
            while (rs.next()) {
                staff_ID = Integer.parseInt(rs.getString("staff_id"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return staff_ID;
    }


    //Get Staff User Details.
    public List getUserStaffDetails(Integer staffID) {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT s.staff_id, s.first_name, s.last_name, s.known_as, ct.contact_tel_no, s.telephone_no_id, s.contact_email, jrl.role_title, jctl.contract_type_desc, s2.first_name || ' ' || s2.last_name AS reports_to, ib.image_blob " +
                                            "    FROM csa_dbowner.staff s " +
                    "                           INNER JOIN csa_dbowner.contact_telephone ct ON s.telephone_no_id = ct.contact_tel_id " +
                    "                           INNER JOIN csa_dbowner.job_roles_lu jrl ON s.job_role_ri = jrl.job_role_id " +
                    "                           INNER JOIN csa_dbowner.job_contract_type_lu jctl ON s.contract_type_ri = jctl.contract_type_id " +
                    "                            LEFT OUTER JOIN csa_dbowner.staff s2 ON s.reports_to = s2.staff_id " +
                    "                            LEFT OUTER JOIN csa_dbowner.image_blob ib ON s.photo_image_id = ib.image_id " +
                    "                           WHERE s.staff_id = " + staffID + ";");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }


    // Get Valid Positions
    public List getPositions() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery(" SELECT job_role_id, role_title, access_level " +
                    "                             FROM csa_dbowner.job_roles_lu " +
                    "                            WHERE access_level > ( " +
                    "                           SELECT access_level " +
                    "                             FROM csa_dbowner.staff s " +
                    "                            INNER JOIN csa_dbowner.job_roles_lu jrl2 ON s.job_role_ri = jrl2.job_role_id " +
                    "                            WHERE s.db_username = (SELECT CURRENT_USER));");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get Valid Hours
    public List getHours() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT contract_type_id, contract_type_desc " +
                    "                           FROM csa_dbowner.job_contract_type_lu " +
                    "                          ORDER by 1;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Query Reports to possibilities
    public List getReportsTo() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT s.staff_id, s.known_as || ' ' || s.last_name AS \"reports_to\"" +
                    "                           FROM csa_dbowner.staff s " +
                    "                          WHERE s.db_username != (SELECT CURRENT_USER) " +
                    "                          ORDER BY job_role_ri DESC;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Query Valid Telephone Numbers.
    public List getTelephoneNumbers() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT contact_tel_no, min(contact_tel_id) as \"contact_tel_id\" " +
                    "                           FROM csa_dbowner.contact_telephone " +
                    "                          GROUP BY contact_tel_no " +
                    "                          ORDER by 1;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    //Get List of occupied rooms.
    public List getOccupiedRoomDetails() throws SQLException {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("select ro.room_number, su.service_user_id, su.known_as, su.last_name, su.sex, su.service_user_id, r.room_type_id, rt.room_type_description, ib.image_blob " +
                    "                            FROM csa_dbowner.room r, csa_dbowner.room_occupancy ro, csa_dbowner.service_user su, csa_dbowner.room_type_lu rt, csa_dbowner.image_blob ib " +
                    "                           WHERE ro.room_number = r.room_number " +
                    "                             AND ro.service_user_id = su.service_user_id " +
                    "                             AND r.room_type_id = rt.room_type_id " +
                    "                             AND su.photo_image_id = ib.image_id " +
                    "                             AND ro.date_to IS NULL " +
                    "                             AND ib.date_to IS NULL " +
                    "                           ORDER BY room_number;");

            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    //Get service users main details.
    public List getServiceUserDetails(Integer serviceUserID) throws SQLException {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("WITH su_doctors AS ( " +
                    "    SELECT su.service_user_id, cl.org_name " +
                    "      FROM csa_dbowner.service_user su " +
                    "      INNER JOIN csa_dbowner.SERVICE_USER_CONTACTS suc ON su.service_user_id = suc.service_user_id " +
                    "      INNER JOIN csa_dbowner.CONTACT_LIST cl ON suc.contact_id = cl.contact_id " +
                    "      INNER JOIN csa_dbowner.CONTACT_TYPE_LU ctl ON cl.contact_type_id = ctl.contact_type_id AND ctl.contact_type_id = 5) " +
                    "SELECT su.first_name, su.last_name, su.known_as, su.sex, su.ethnicity, e.ethnicity_description, su.date_of_birth, su.date_of_admittance, su.dnacpr_status, ib.image_blob, sd.org_name " +
                    "  FROM csa_dbowner.service_user su " +
                    " INNER JOIN csa_dbowner.ETHNICITY_lu e ON su.ethnicity = e.ethnicity_id " +
                    " INNER JOIN csa_dbowner.image_blob ib ON su.photo_image_id = ib.image_id " +
                    " LEFT OUTER JOIN su_doctors sd ON su.service_user_id = sd.service_user_id " +
                    "   WHERE su.service_user_id = " + serviceUserID + ";");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    //Get Staff User Allergies.
    public List getServiceUserAllergies(Integer serviceUserID) throws SQLException {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT sua.service_user_id, Allergy_description, Allergy_type_desc, date_from, date_to " +
                    "                            FROM csa_dbowner.service_user_allergies sua " +
                    "                           INNER JOIN csa_dbowner.allergies_lu al ON sua.allergy_id = al.allergy_id  " +
                    "                           INNER JOIN csa_dbowner.allergy_category_lu acl ON al.allergy_type_id = acl.allergy_type_id " +
                    "                             AND service_user_id = " + serviceUserID +
                    "                           ORDER BY date_to DESC;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    //Get Service User Further Details.
    public List getServiceUserFurtherDetails(Integer serviceUserID) {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT su.service_user_id, suvhw.su_height_cms, suvhw.su_weight_kgs, suvhw.su_bmi, suvbp.su_bp_systolic, suvbp.su_bp_diastolic, sufi.behaviour_history, sufi.dietary_requirements, sufi.moving_and_handling, sufi.personal_preferences " +
                    "                            FROM csa_dbowner.service_user su  " +
                    "                           INNER JOIN csa_dbowner.service_user_further_info sufi ON su.service_user_id = sufi.service_user_id " +
                    "                           INNER JOIN csa_dbowner.service_user_vitals_bp suvbp ON su.service_user_id = suvbp.service_user_id " +
                    "                           INNER JOIN csa_dbowner.service_user_vitals_hw suvhw ON su.service_user_id = suvhw.service_user_id " +
                    "                           WHERE suvbp.date_recorded = (SELECT max(suvbp2.date_recorded) FROM csa_dbowner.service_user_vitals_bp suvbp2 WHERE suvbp2.service_user_id = su.service_user_id) " +
                    "                             AND suvhw.date_recorded = (SELECT max(suvhw2.date_recorded) FROM csa_dbowner.service_user_vitals_hw suvhw2 WHERE suvhw2.service_user_id = su.service_user_id) " +
                    "                             AND su.service_user_id = " + serviceUserID + ";");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get Service User Medical Conditions.
    public List getServiceUserCurrMedCons(Integer serviceUserID) throws SQLException {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT sumd.service_user_id, mcl.med_condition_name, mccl.med_con_category_name, sumd.date_from, sumd.date_to " +
                    "                           FROM csa_dbowner.service_user_medical_details sumd " +
                    "                          INNER JOIN csa_dbowner.medical_conditions_lu mcl ON sumd.med_condition_id = mcl.med_condition_id " +
                    "                          INNER JOIN csa_dbowner.medical_cond_categories_lu mccl ON mcl.medical_category_id = mccl.med_con_category_id " +
                    "                            AND service_user_id = " + serviceUserID +
                    "                          ORDER BY date_to DESC;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get the service user contacts list.
    public List getServiceUserContacts(Integer serviceUserID) throws SQLException {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT  suc.contact_id, cl.title || ' ' || cl.first_name || ' ' || cl.last_name AS \"contact_name\", cl.org_name, ct.ct_description, " +
                    "        STRING_AGG(distinct cl2.org_name, ', ') AS \"parent_organisations\", " +
                    "        MAX(ct1.contact_tel_no) AS \"main_telno\", " +
                    "        MAX(ct2.contact_tel_no) AS \"work_telno\", " +
                    "        MAX(ct3.contact_tel_no) AS \"home_telno\", " +
                    "        MAX(ce1.contact_email_address) AS \"primary_email\", " +
                    "        MAX(ce2.contact_email_address) AS \"secondary_email\", " +
                    "        ca.address_line1, ca.address_line2, ca.address_line3, ca.city, ca.county_or_province, ca.post_code, ca.country " +
                    "  FROM csa_dbowner.service_user su " +
                    " INNER JOIN csa_dbowner.service_user_contacts suc ON su.service_user_id = suc.service_user_id " +
                    " INNER JOIN csa_dbowner.contact_list cl ON suc.contact_id = cl.contact_id " +
                    " INNER JOIN csa_dbowner.contact_address_link cal ON cl.contact_id = cal.contact_id " +
                    " INNER JOIN csa_dbowner.contact_address ca ON cal.address_id = ca.address_id " +
                    " INNER JOIN csa_dbowner.contact_contact_book ccb ON cl.contact_id = ccb.contact_id  " +
                    " INNER JOIN csa_dbowner.contact_type_lu ct ON cl.contact_type_id = ct.contact_type_id " +
                    " LEFT OUTER JOIN csa_dbowner.contact_association_lu calu ON suc.contact_id = calu.individual_contact_id " +
                    " LEFT OUTER JOIN csa_dbowner.contact_list cl2 ON calu.org_contact_id = cl2.contact_id " +
                    " LEFT OUTER JOIN csa_dbowner.contact_telephone ct1 on ccb.contact_tel_id = ct1.contact_tel_id AND ccb.contact_point_type = 1 " +
                    " LEFT OUTER JOIN csa_dbowner.contact_telephone ct2 on ccb.contact_tel_id = ct2.contact_tel_id AND ccb.contact_point_type = 2 " +
                    " LEFT OUTER JOIN csa_dbowner.contact_telephone ct3 on ccb.contact_tel_id = ct3.contact_tel_id AND ccb.contact_point_type = 3 " +
                    " LEFT OUTER JOIN csa_dbowner.contact_email ce1 on ccb.contact_email_id = ce1.contact_email_id AND ccb.contact_point_type = 4 " +
                    " LEFT OUTER JOIN csa_dbowner.contact_email ce2 on ccb.contact_email_id = ce2.contact_email_id AND ccb.contact_point_type = 5 " +
                    "  WHERE su.service_user_id = " + serviceUserID +
                    "    AND suc.date_to IS NULL " +
                    " GROUP BY suc.contact_id, cl.title || ' ' || cl.first_name || ' ' || cl.last_name, cl.org_name, ca.address_line1, ca.address_line2, ca.address_line3, ca.city, ca.county_or_province, ca.post_code, ca.country, ct.ct_description;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    //Get list of Service Users.
    public List getServiceUserList() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT service_user_id, known_as || ' ' || last_name as \"su_name\" " +
                    "                            FROM csa_dbowner.service_user " +
                    "                           WHERE date_of_leaving IS NULL " +
                    "                           ORDER BY last_name;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get list of available rooms.
    public List getAvailableRooms(Boolean occupiedRooms) {
        String sqlQuery;
        System.out.println(occupiedRooms);
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            if (occupiedRooms) {
                System.out.println(1);
                sqlQuery = "WITH current_occupancy AS (SELECT room_number, COUNT(*) " +
                        "                                FROM csa_dbowner.room_occupancy ro " +
                        "                               WHERE date_to IS NULL " +
                        "                               GROUP BY room_number) " +
                        "SELECT r.room_number, rtl.room_type_description, (rtl.maximum_occupancy - COALESCE(co.count, 0)) as \"space_remaining\" " +
                        "  FROM csa_dbowner.ROOM r " +
                        " INNER JOIN csa_dbowner.room_type_lu rtl ON r.room_type_id = rtl.room_type_id " +
                        " INNER JOIN current_occupancy co ON r.room_number = co.room_number " +
                        " WHERE COALESCE(co.count, 0) < rtl.maximum_occupancy " +
                        " ORDER BY room_number; ";
            } else {
                System.out.println(2);
                sqlQuery = "SELECT room_number, room_type_description " +
                        "             FROM csa_dbowner.room r " +
                        "            INNER JOIN csa_dbowner.room_type_lu rtl ON r.room_type_id = rtl.room_type_id " +
                        "            WHERE NOT EXISTS (SELECT 1 FROM csa_dbowner.room_occupancy ro WHERE ro.room_number = r.room_number) " +
                        "            ORDER BY room_number;";
            }
            ResultSet rs = st.executeQuery(sqlQuery);
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of Medical Categories
    public List getMedicalCategories() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT med_con_category_id, med_con_category_name " +
                    "                            FROM csa_dbowner.medical_cond_categories_lu " +
                    "                           ORDER by med_con_category_id;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of Medical Conditions
    public List getMedicalConditions(Integer category) {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT med_condition_id, med_condition_name, medical_category_id " +
                    "                            FROM csa_dbowner.medical_conditions_lu " +
                    "                           WHERE medical_category_id = " + category + " " +
                    "                           ORDER by med_condition_id;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of Allergy Categories
    public List getAllergyCategories() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT allergy_type_id, allergy_type_desc " +
                    "                           FROM csa_dbowner.allergy_category_lu " +
                    "                          ORDER by allergy_type_id;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of Allergies
    public List getAllergies(Integer category) {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT allergy_id, allergy_description, allergy_type_id " +
                    "                           FROM csa_dbowner.allergies_lu " +
                    "                          WHERE allergy_type_id = " + category + " " +
                    "                          ORDER by allergy_id;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of ethnicities held in the database.
    public List getEthnicities() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT ethnicity_description, ethnicity_id " +
                    "                            FROM csa_dbowner.ethnicity_lu " +
                    "                           ORDER BY ethnicity_description; ");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    public Integer getCurrentlyUsingDefaultService(Integer serviceuUserID, Integer serviceTypeID) {
        Integer result = null;
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("SELECT 1 as result " +
                    "                                                           FROM csa_dbowner.service_user_contacts suc " +
                    "                                                          INNER JOIN csa_dbowner.home_default_services hds ON suc.contact_id = hds.contact_id " +
                    "                                                          WHERE suc.service_user_id = ? " +
                    "                                                            AND hds.contact_type_id = ? " +
                    "                                                            AND suc.date_to IS NULL;");
            st.setInt(1, serviceuUserID);
            st.setInt(2, serviceTypeID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println("result?");
                result = (rs.getInt("result"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return result;
    }

    // Query Reports to possibilities
    public List getUsersResponsibleFor() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("WITH Recursive staff_hierarchy AS ( " +
                    "                               SELECT s1.staff_id, s1.known_as || ' ' || s1.last_name as \"reportee\", s1.reports_to " +
                    "                                 FROM csa_dbowner.STAFF s1 " +
                    "                                WHERE s1.staff_id = 2 " +
                    "                                UNION ALL " +
                    "                               SELECT s2.staff_id, s2.known_as || ' ' || s2.last_name as \"reportee\", s2.reports_to " +
                    "                                 FROM csa_dbowner.STAFF s2 " +
                    "                                 JOIN staff_hierarchy sh " +
                    "                                   ON sh.staff_id = s2.reports_to    " +
                    "                                   ) select staff_id, reportee, reports_to from staff_hierarchy");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of contact types.
    public List getContactTypes() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT contact_type_id, ct_description " +
                    "                            FROM csa_dbowner.contact_type_lu " +
                    "                           ORDER BY ct_description; ");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of contacts for a category.
    public List getContacts(Integer contactCategory) {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT contact_id, " +
                    "                                  CASE " +
                    "                                    WHEN org_name IS NOT NULL THEN org_name " +
                    "                                    WHEN org_name IS NULL THEN title || ' ' || first_name || ' ' || last_name " +
                    "                                  END as contact_name " +
                    "                                 FROM csa_dbowner.contact_list " +
                    "                                WHERE contact_type_id = 10 " +
                    "                                ORDER BY contact_name;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Get List of contacts for a category.
    public List getGPOptions() {
        List<HashMap<String, Object>> resultList = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT org_name, contact_id " +
                    "                            FROM csa_dbowner.contact_list cl " +
                    "                           WHERE contact_type_id = 5 " +
                    "                           ORDER by org_name;");
            resultList = convertResultSetToList(rs);
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return resultList;
    }

    // Query staff data.
    public String getSingleStaffValueForUpdate(String columnName, String staffID) {
        String result = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT s." + columnName + " as \"value_to_update\" " +
                    "                            FROM csa_dbowner.staff s " +
                    "                           WHERE s.staff_id = " + staffID + ";");
            while (rs.next()) {
                result = (rs.getString("value_to_update"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return result;
    }

    public Integer getHomeDefaultContactForService(Integer serviceID) {
        Integer result = null;
        try {
            Statement st = dbInstance.connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT contact_id FROM csa_dbowner.home_default_services WHERE contact_type_id = 5 AND date_to is null;");
            while (rs.next()) {
                result = (rs.getInt("contact_id"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
        return result;
    }

    // Update the service users basic information.
    public void updateSingleStaffDetails(String toUpdate, String newValue, Integer staffID) {
        Long key = null;
        PreparedStatement st = null;
        try {
            switch (toUpdate) {
                case "known_as":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.staff SET known_as = ? WHERE staff_id = ?;");
                    st.setString(1, newValue);
                    break;
                case "email_address":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.staff SET contact_email = ? WHERE staff_id = ?;");
                    st.setString(1, newValue);
                    break;
                case "emp_position":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.staff SET job_role_ri = ? WHERE staff_id = ?;");
                    st.setInt(1, Integer.parseInt(newValue));
                    break;
                case "contracted_hours":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.staff SET contract_type_ri = ? WHERE staff_id = ?;");
                    st.setInt(1, Integer.parseInt(newValue));
                    break;
                case "reports_to":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.staff SET reports_to = ? WHERE staff_id = ?;");
                    st.setInt(1, Integer.parseInt(newValue));
                    break;
                case "telephone_number":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.staff SET telephone_no_id = ? WHERE staff_id = ?;");
                    st.setInt(1, Integer.parseInt(newValue));
                    break;
            }
            if (st != null) {
                st.setInt(2, staffID);
                st.executeUpdate();
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Update the service users full name.
    public void updateStaffName(String firstName, String lastName, Integer staffID) {
        PreparedStatement st = null;
        try {
            st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.staff SET first_name = ?, last_name = ? WHERE staff_id = ?;");
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setInt(3, staffID);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Update the service users full name.
    public void updateTelephoneNUmber(Integer telephoneNumberID, String TelephoneNumber) {
        System.out.println("Enter");
        PreparedStatement st = null;
        try {
            st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.contact_telephone SET contact_tel_no = ? WHERE contact_tel_id = ?;");
            st.setString(1, TelephoneNumber);
            st.setInt(2, telephoneNumberID);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Update the service users further information columns.
    public void updateServiceUserFurtherInfoRec(String columnName, String newValue, Integer serviceUserId) {
        Long key = null;
        PreparedStatement st = null;
        try {
            switch (columnName) {
                case "dietary_requirements":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.service_user_further_info SET dietary_requirements = ? WHERE service_user_id = ? ;");
                    break;
                case "moving_and_handling":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.service_user_further_info SET moving_and_handling = ? WHERE service_user_id = ? ;");
                    break;
                case "behaviour_history":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.service_user_further_info SET behaviour_history = ? WHERE service_user_id = ? ;");
                    break;
                case "personal_preferences":
                    st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.service_user_further_info SET personal_preferences = ? WHERE service_user_id = ? ;");
                    break;
            }
            if (st != null) {
                st.setString(1, newValue);
                st.setInt(2, serviceUserId);
                st.executeUpdate();
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Update a service users details.
    public void updateServiceUser(String firstName, String lastName, String knownAS, String sex, String dateOfBirth, String dateOfAdmittance, Integer dnacprStatus, Integer ethnicity, Integer serviceUserID) {
        LocalDate convertedDateOfBirth = Utilities.convertStringToDate(dateOfBirth);
        LocalDate convertedDateOfAdmittance = Utilities.convertStringToDate(dateOfAdmittance);

        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("UPDATE  csa_dbowner.SERVICE_USER " +
                    "   SET  FIRST_NAME = ?, LAST_NAME = ?, KNOWN_AS = ?, SEX = ?, " +
                    "        DATE_OF_BIRTH = ?, DATE_OF_ADMITTANCE = ?, DNACPR_STATUS = ?, ETHNICITY = ? " +
                    " WHERE  SERVICE_USER_ID = ?;");
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, knownAS);
            st.setString(4, sex);
            st.setObject(6, convertedDateOfBirth);
            st.setObject(7, convertedDateOfAdmittance);
            st.setInt(8, dnacprStatus);
            st.setInt(9, ethnicity);
            st.setInt(10, serviceUserID);

            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Set room dateto to null to end residents occupation of it.
    public void updateOccupiedRoomEndDate(Integer roomNUmber, Integer serviceUserId) {
        LocalDate currentDate = LocalDate.now();
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.room_occupancy " +
                    "                                                            SET date_to = ? " +
                    "                                                          WHERE ROOM_NUMBER = ? AND SERVICE_USER_ID = ?;");

            System.out.println(st);
            st.setObject(1, currentDate);
            st.setInt(2, roomNUmber);
            st.setInt(3, serviceUserId);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Add default service to a user.
    public void updateUserPhoto(Integer userID, Integer photoID) {
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.SERVICE_USER SET PHOTO_IMAGE_ID = ? WHERE service_user_id = ?;");
            st.setInt(1, photoID);
            st.setInt(2, userID);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Add default service to a user.
    public void updateStaffPhoto(Integer userID, Integer photoID) {
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.STAFF SET PHOTO_IMAGE_ID = ? WHERE staff_id = ?;");
            st.setInt(1, photoID);
            st.setInt(2, userID);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Add default service to a user.
    public void UpdateEndDateForServiceUserDefaultContact(Integer serviceUserID, Integer serviceTypeID) {
        Integer contact_id = getHomeDefaultContactForService(serviceTypeID);
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("UPDATE csa_dbowner.SERVICE_USER_CONTACTS SET DATE_TO = now() WHERE service_user_id = ? AND contact_id = ?;");
            st.setInt(1, serviceUserID);
            st.setInt(2, contact_id);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Insert a new telephone number.
    public Long insertTelephone(String telephoneNUmber) {
        Long key = null;
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.CONTACT_TELEPHONE (CONTACT_TEL_NO) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, telephoneNUmber);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
        return key;
    }

    // Insert a new medical condition.
    public Long insertNewMedicalCondition(String medicalConditionName, Integer medicalCategoryID) {
        Long key = null;
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.medical_conditions_lu (med_condition_name, medical_category_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, medicalConditionName);
            st.setInt(2, medicalCategoryID);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
        return key;
    }

    // Insert a new medical condition.
    public Long insertNewAllergyCondition(String allergyName, Integer allergyCategoryID) {
        Long key = null;
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.allergies_lu (allergy_description, allergy_type_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, allergyName);
            st.setInt(2, allergyCategoryID);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
        return key;
    }

    // Insert a new medical condition against a user.
    public Boolean insertNewMedicalConditionForUser(Integer serviceUserId, Integer medicalConditionID, String dateFrom, String dateTo) {
        try {
            LocalDate convertedDateFrom = Utilities.convertStringToDate(dateFrom);
            LocalDate convertedDateTo = Utilities.convertStringToDate(dateTo);
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.service_user_medical_details (service_user_id, med_condition_id, date_from, date_to) VALUES (?, ?, ?, ?)");
            st.setInt(1, serviceUserId);
            st.setInt(2, medicalConditionID);
            st.setObject(3, convertedDateFrom);
            st.setObject(4, convertedDateTo);
            st.executeUpdate();
        } catch (SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
            return false;
        }
        return true;
    }

    // Insert a new allergy  against a user.
    public Boolean insertNewAllergyForUser(Integer serviceUserId, Integer allergyID, String dateFrom, String dateTo) {
        try {
            LocalDate convertedDateFrom = Utilities.convertStringToDate(dateFrom);
            LocalDate convertedDateTo = Utilities.convertStringToDate(dateTo);
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.service_user_allergies (service_user_id, allergy_id, date_from, date_to) VALUES (?, ?, ?, ?)");
            st.setInt(1, serviceUserId);
            st.setInt(2, allergyID);
            st.setObject(3, convertedDateFrom);
            st.setObject(4, convertedDateTo);
            st.executeUpdate();
        } catch (SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
            return false;
        }
        return true;
    }

    // Insert New Service User BP Reading.
    public void insertNewServiceUserBPVitals(Integer serviceUserID, Integer inputValue1, Integer inputValue2) {
        String sqlStatement = "INSERT INTO csa_dbowner.SERVICE_USER_VITALS_HW (service_user_id, su_height_cms, su_weight_kgs) VALUES (" + serviceUserID + ", " + inputValue1 + ", " + inputValue2 + ");";
        try {
            Statement st = dbInstance.connection.createStatement();
            st.executeUpdate(sqlStatement);
        } catch (SQLException e) {
            Log.e("Generic Update Error", "exception", e);
        }
    }

    // Insert New Service User Height + Weight Reading.
    public void insertNewServiceUserHWVitals(Integer serviceUserID, Double inputValue1, Double inputValue2) {
        String sqlStatement = "INSERT INTO csa_dbowner.SERVICE_USER_VITALS_HW (service_user_id, su_height_cms, su_weight_kgs) VALUES (" + serviceUserID + ", " + inputValue1 + ", " + inputValue2 + ");";
        try {
            Statement st = dbInstance.connection.createStatement();
            st.executeUpdate(sqlStatement);
        } catch (SQLException e) {
            Log.e("Generic Update Error", "exception", e);
        }
    }

    // Add default service to a user.
    public void insertDefaultServiceToUser(Long serviceUserID, Integer serviceTypeID) {
        LocalDate currentDate = LocalDate.now();
        Integer contact_id = getHomeDefaultContactForService(serviceTypeID);
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.SERVICE_USER_CONTACTS (service_user_id, contact_id, primary_ind, date_from) VALUES (?, ?, ?, ?);");
            st.setLong(1, serviceUserID);
            st.setInt(2, contact_id);
            st.setString(3, String.valueOf('Y'));
            st.setObject(4, currentDate);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Insert a new room association.
    public void insertRoomOccupancy(Integer roomNumber, Long serviceUserID, String dateOfAdmittance) {
        LocalDate convertedDateOfAdmittance = Utilities.convertStringToDate(dateOfAdmittance);
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.ROOM_OCCUPANCY (room_number, service_user_id, date_from) VALUES (?, ?, ?);");
            st.setInt(1, roomNumber);
            st.setLong(2, serviceUserID);
            st.setObject(3, convertedDateOfAdmittance);
            st.executeUpdate();
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
    }

    // Insert a new service user.
    public Long insertServiceUser(String firstName, String lastName, String knownAS, String sex, Integer photo_image_id, String dateOfBirth, String dateOfAdmittance, Integer dnacprStatus, Integer ethnicity) {
        Long key = null;
        LocalDate convertedDateOfBirth = Utilities.convertStringToDate(dateOfBirth);
        LocalDate convertedDateOfAdmittance = Utilities.convertStringToDate(dateOfAdmittance);
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.SERVICE_USER (FIRST_NAME, LAST_NAME, KNOWN_AS, SEX, PHOTO_IMAGE_ID, DATE_OF_BIRTH, DATE_OF_ADMITTANCE, DNACPR_STATUS, ETHNICITY)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, knownAS);
            st.setString(4, sex);
            st.setInt(5, photo_image_id);
            st.setObject(6, convertedDateOfBirth);
            st.setObject(7, convertedDateOfAdmittance);
            st.setInt(8, dnacprStatus);
            st.setInt(9, ethnicity);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
        return key;
    }

    public long insertNewStaffMember(String firstName, String lastName, String userName, String digiSig, String dateOfBirth, String startDate, String password) {
        LocalDate convertedDateOfBirth = Utilities.convertStringToDate(dateOfBirth);
        LocalDate convertedStartDate = Utilities.convertStringToDate(startDate);
        Integer defaultContractType = 1;
        Integer defaultJobRole = 6;
        Long key = null;

        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.staff (first_name, last_name, known_as, db_username, digital_signature, date_of_birth, job_role_ri, contract_type_ri, start_date) " +
                    "         VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, firstName);
            st.setString(4, userName);
            st.setString(5, digiSig);
            st.setObject(6, convertedDateOfBirth);
            st.setInt(7, defaultJobRole);
            st.setInt(8, defaultContractType);
            st.setObject(9, convertedStartDate);
            System.out.println(st);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
        if (key != null) {
            CreateDBUser(userName, password);
        }
        return key;
    }

    //Getters.
    // Create new DB user, Make sure in readonly to start with.
    public void CreateDBUser(String username, String password) {
        try {
            Statement st = dbInstance.connection.createStatement();
            String sql = "create user \" " + username + " \" with encrypted password '" + password + "'";
            st.executeUpdate(sql);
            st.close();

            st = dbInstance.connection.createStatement();
            sql = "GRANT readonly to \" " + username + "\";";
            System.out.println(sql);
            st.executeUpdate(sql);
            st.close();

        } catch (Exception e) {
            Log.e("Database Exception", "exception", e);
        }
    }

    // Insert new Image.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Long insertImage(Bitmap image) {
        Long key = null;
        LocalDate currentDate = LocalDate.now();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        image.recycle();
        try {
            PreparedStatement st = dbInstance.connection.prepareStatement("INSERT INTO csa_dbowner.IMAGE_BLOB (IMAGE_BLOB, DATE_ADDED) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            st.setBytes(1, byteArray);
            st.setObject(2, currentDate);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
        } catch (
                SQLException e) {
            Log.e("Error Inserting Number", "exception", e);
        }
        return key;
    }
}