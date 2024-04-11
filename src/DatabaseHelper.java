import java.util.*;
import java.time.LocalDate;
import java.sql.*;
public class DatabaseHelper {
    private static String uniqueIdentifierGenerator(String prefix) {
        Random rand = new Random();
        StringBuilder unique = new StringBuilder(prefix);

        for (int i = 0; i < 4; i++){
            unique.append(rand.nextInt(10));
        }

        return unique.toString();
    }

    public static String orderNumberGenerator(){
        return DatabaseHelper.uniqueIdentifierGenerator("C");
    }

    public static String invoiceGenerator(){
       return DatabaseHelper.uniqueIdentifierGenerator("S");
    }

    public static String trackingNumberGenerator(){
        Random rand = new Random();
        StringBuilder tracking_num = new StringBuilder();

        for (int i = 0; i < 2; i++)
            tracking_num.append((char) (rand.nextInt(26) + 'A'));

        for (int i = 0; i < 7; i++){
            tracking_num.append(rand.nextInt(9));
        }

        return tracking_num.toString();
    }

    public static String getDate(){
        LocalDate date = LocalDate.now();
        return String.valueOf(date);
    }

    public static boolean checkView(String view, Connection conn){
        Statement query = null;
        ResultSet rs = null;
        try{
            query = conn.createStatement();
            String sql = "SHOW FULL TABLES WHERE TABLE_TYPE='VIEW'";
            rs = query.executeQuery(sql);

            while (rs.next()){
                String full_tab = rs.getString("Tables_in_" + System.getenv("DB_NAME"));
                if (full_tab.equals(view)) {
                    return true;
                }
            }
        } catch(SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) { }
            }
            if (query != null) {
                try {
                    query.close();
                } catch (SQLException e) { }
            }
        }
        return false;
    }

    public static void createView(String view_name, Connection conn) {
        Statement query = null;
        try {
            query = conn.createStatement();

            String month = view_name.split("_")[0];
            String year = view_name.split("_")[1];

            //Number Representation of Months
            HashMap<String, String> month_info = new HashMap<>();
            month_info.put("JANUARY", "01");
            month_info.put("FEBRUARY", "02");
            month_info.put("MARCH", "03");
            month_info.put("APRIL", "04");
            month_info.put("MAY", "05");
            month_info.put("JUNE", "06");
            month_info.put("JULY", "07");
            month_info.put("AUGUST", "08");
            month_info.put("SEPTEMBER", "09");
            month_info.put("OCTOBER", "10");
            month_info.put("NOVEMBER", "11");
            month_info.put("DECEMBER", "12");
            String month_parameter = month_info.get(month);

            //Link days in month to respective month
            HashMap<String, String> day_info = new HashMap<>();
            day_info.put("JANUARY", "31");
            day_info.put("FEBRUARY", "28");
            day_info.put("MARCH", "31");
            day_info.put("APRIL", "30");
            day_info.put("MAY", "31");
            day_info.put("JUNE", "30");
            day_info.put("JULY", "31");
            day_info.put("AUGUST", "31");
            day_info.put("SEPTEMBER", "30");
            day_info.put("OCTOBER", "31");
            day_info.put("NOVEMBER", "30");
            day_info.put("DECEMBER", "31");
            String date_parameter = day_info.get(month);

            String sql = String.format("CREATE VIEW %s AS SELECT * FROM Orders WHERE Date_Created >= '%s-%s-01' AND" +
                    " Date_Created <= '%s-%s-%s'", view_name, year, month_parameter, year, month_parameter, date_parameter);
            query.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (query != null) {
                try {
                    query.close();
                } catch (SQLException sqlEx) { }
            }
        }
    }

    public static void getView(String view_name, Connection conn) {
        Statement query = null;
        ResultSet rs = null;
        try {
            query = conn.createStatement();
            String sql = String.format("SELECT * FROM %s", view_name);
            rs = query.executeQuery(sql);

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data Available.");
                sql = String.format("DROP VIEW %s", view_name);
                query.executeUpdate(sql);
                return;
            }

            System.out.printf("--------------------------------------------------------------------------" +
                    "----------------------------------%n");
            System.out.printf("| %-7s | %-11s | %-9s | %-4s | %-10s | %-15s | %-15s | %-12s |%n", "OrderID",
                    "OrderNumber", "ProductID", "Qty", "CustomerID", "Tracking_Number", "Delivery_Status", "Date_Created");
            System.out.printf("--------------------------------------------------------------------------" +
                    "----------------------------------%n");

            while (rs.next()) {
                int order_id = rs.getInt("OrderID");
                String order_number = rs.getString("OrderNumber");
                int product_id = rs.getInt("ProductID");
                int qty = rs.getInt("Qty");
                int customer_id = rs.getInt("CustomerID");
                String tracking_number = rs.getString("Tracking_Number");
                String delivery_status = rs.getString("Delivery_Status");
                String date_created = rs.getString("Date_Created");

                System.out.printf("| %02d      | %-11s | %02d        | %02d   | %02d         |" +
                                " %-15s | %-15s | %-12s |%n", order_id, order_number, product_id, qty, customer_id,
                        tracking_number, delivery_status, date_created);
            }
            System.out.printf("-------------------------------------------------------------------------" +
                    "-----------------------------------%n");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { }
            }
            if (query != null) {
                try {
                    query.close();
                } catch (SQLException sqlEx) { }
            }
        }
    }

    public static boolean checkCustomerID(int customer_id, Connection conn) {
        Statement query = null;
        ResultSet rs = null;
        try {
            query = conn.createStatement();
            String sql = "SELECT COUNT(*) FROM Customers WHERE CustomerID = " + customer_id;
            rs = query.executeQuery(sql);

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { }
            }
            if (query != null) {
                try {
                    query.close();
                } catch (SQLException sqlEx) { }
            }
        }
        return false;
    }
    public static boolean checkSellerID(int seller_id, Connection conn) {
        Statement query = null;
        ResultSet rs = null;
        try {
            query = conn.createStatement();
            String sql = "SELECT COUNT(*) FROM Sellers WHERE SellerID = " + seller_id;
            rs = query.executeQuery(sql);

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { }
            }
            if (query != null) {
                try {
                    query.close();
                } catch (SQLException sqlEx) { }
            }
        }
        return false;
    }
    public static int checkInventory(int product_id, Connection conn) {
        Statement query = null;
        ResultSet rs = null;
        try {
            query = conn.createStatement();
            String sql = "SELECT Qty FROM Inventory WHERE ProductID = " + product_id;
            rs = query.executeQuery(sql);

            if (!rs.isBeforeFirst()) return -1;

            if (rs.next()) {
                return rs.getInt("Qty");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { }
            }
            if (query != null) {
                try {
                    query.close();
                } catch (SQLException sqlEx) { }
            }
        }
        return -1;
    }

    public static void updateQuantity(int quantity, int ID, Connection conn) {
        Statement query = null;
        try {
            query = conn.createStatement();
            String sql = "UPDATE Inventory SET Qty = " + quantity + " WHERE ProductID = " + ID;
            query.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (query != null) {
                try {
                    query.close();
                } catch (SQLException sqlEx) { }
            }
        }
    }
}
