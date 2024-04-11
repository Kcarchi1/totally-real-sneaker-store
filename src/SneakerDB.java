import java.sql.*;
import java.util.*;

public class SneakerDB
{
    public static void main(String[]args)
    {
        Connection conn = null;
        Statement query = null;
        ResultSet rs = null;
        try
        {
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            query = conn.createStatement();
            String sql;

            Scanner input = new Scanner(System.in);

            //3 portals: Customers, Workers, Sellers
            System.out.println("Select A Portal:");
            System.out.println("----------------");
            System.out.println("Workers -- (Press 1)\nCustomers -- (Press 2)\nSellers -- (Press 3)");
            int selection = input.nextInt();

            //(Worker Portal)
            if (selection == 1) {
                System.out.println("Worker Portal:");
                System.out.println("--------------");
                System.out.print("Manage Inventory -- (Press 1)\nManage Orders -- (Press 2)\n");
                selection = input.nextInt();

                switch(selection) {
                    //Manage Inventory
                    case 1:
                        System.out.println("Manage Inventory:");
                        System.out.println("-----------------");
                        System.out.println("Add Products -- (Press 1)\nView Products -- (Press 2)");
                        selection = input.nextInt();
                        //Add products
                        if (selection == 1) {
                            while (selection == 1) {
                                input.nextLine();
                                System.out.print("Shoe name: ");
                                String inventory_name = input.nextLine();

                                System.out.print("Shoe Size: ");
                                String inventory_size = input.nextLine();

                                System.out.print("Quantity of item: ");
                                int inventory_qty = input.nextInt();

                                System.out.print("Cost of shoe: ");
                                double inventory_cost = input.nextDouble();

                                sql = String.format("INSERT INTO Inventory (Product_Name, size, Qty, Cost) VALUES ('%s', '%s', %d, %.2f)",
                                        inventory_name, inventory_size, inventory_qty, inventory_cost);
                                query.executeUpdate(sql);
                                System.out.println("Shoe Added to Inventory!");
                                System.out.println("Would you like to add another one? (Press 1 to continue or press any number to escape)");
                                selection = input.nextInt();
                            }
                        }
                        //View products
                        else if (selection == 2) {
                            sql = "SELECT * FROM Inventory";
                            rs = query.executeQuery(sql);

                            System.out.printf("------------------------------------------------------------------%n");
                            System.out.printf("| %-10s | %-25s | %-5s | %-4s | %-6s |%n",
                                    "ProductID", "Product_Name", "size", "Qty", "Cost");
                            System.out.printf("------------------------------------------------------------------%n");
                            while (rs.next()) {
                                int productID = rs.getInt("ProductID");
                                String product_name = rs.getString("Product_Name");
                                String size = rs.getString("size");
                                int qty = rs.getInt("Qty");
                                double cost = rs.getDouble("Cost");

                                System.out.printf("|     %02d     | %-25s | %-5s |  %02d  | %.2f |%n",
                                        productID, product_name, size, qty, cost);
                            }
                            System.out.printf("------------------------------------------------------------------%n");
                            rs.close();
                        }
                        break;
                    //Manage Orders
                    case 2:
                        System.out.println("Manage Orders:");
                        System.out.println("--------------");
                        System.out.println("View All Orders -- (Press 1)\nView Orders by Month/Year -- (Press 2)\n" +
                                "Update Order Status -- (Press 3)");
                        selection = input.nextInt();
                        //View All Orders
                        if(selection == 1) {
                            sql = "SELECT * FROM Orders";
                            rs = query.executeQuery(sql);

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
                        }
                        //View Orders by Month/Year
                        else if (selection == 2){
                            input.nextLine();

                            System.out.print("Enter Month: ");
                            String vname_month = input.nextLine();

                            System.out.print("Enter Year: ");
                            String vname_year = input.nextLine();

                            String view_name = (vname_month + "_" + vname_year).toUpperCase();

                            if (!DatabaseHelper.checkView(view_name, conn)){
                                DatabaseHelper.createView(view_name, conn);
                                DatabaseHelper.getView(view_name, conn);
                            }
                            else {
                                DatabaseHelper.getView(view_name, conn);
                            }
                        }
                        //Update Order Status
                        else if (selection == 3){
                            input.nextLine();
                            System.out.print("Enter Order Number: ");
                            String ordernum = input.nextLine();

                            System.out.print("New Order Status (Shipped or Delivered): ");
                            String new_status = input.nextLine();

                            sql = String.format("UPDATE Orders SET Delivery_Status='%s' WHERE OrderNumber='%s'",
                                    new_status, ordernum);
                            query.executeUpdate(sql);

                            System.out.println("Order Status Updated.");
                        }
                        break;
                }
            }
            //(Customer Portal)
            else if (selection == 2) {
                System.out.println("Customer Portal:");
                System.out.println("----------------");
                System.out.println("Register -- (Press 1)\nPlace Order -- (Press 2)\nView Order History -- (Press 3)");
                selection = input.nextInt();

                switch(selection) {
                    //Registration
                    case 1:
                        input.nextLine();
                        System.out.print("First Name: ");
                        String first_name = input.nextLine();

                        System.out.print("Last Name: ");
                        String last_name = input.nextLine();

                        System.out.print("Email: ");
                        String email = input.nextLine();

                        System.out.print("Phone Number (Ex. ###-###-####): ");
                        String phone_num = input.nextLine();

                        System.out.print("Street Address: ");
                        String street_add = input.nextLine();

                        System.out.print("City: ");
                        String city = input.nextLine();

                        System.out.print("Zipcode: ");
                        int zipcode = input.nextInt();
                        input.nextLine();

                        System.out.print("Date of Birth (Ex. YYYY-MM-DD): ");
                        String birth_date = input.nextLine();

                        sql = String.format("INSERT INTO Customers(FirstName, LastName, Email, Phone, Street, City," +
                                        "Zipcode, DOB) VALUES('%s', '%s', '%s', '%s', '%s', '%s', %d, '%s')", first_name, last_name,
                                email, phone_num, street_add, city, zipcode, birth_date);
                        query.executeUpdate(sql);
                        System.out.print("Registration Complete!");
                        break;
                    //Place Order
                    case 2:
                        String order_number = DatabaseHelper.orderNumberGenerator();
                        String tracking_number = DatabaseHelper.trackingNumberGenerator();
                        String date_created = DatabaseHelper.getDate();

                        System.out.print("Please enter your ID: ");
                        int customer_id = input.nextInt();

                        if (!DatabaseHelper.checkCustomerID(customer_id, conn)) {
                            System.out.print("ID Not Found. Please Register First.");
                            break;
                        }

                        System.out.print("Enter Shoe ID: ");
                        int product_id = input.nextInt();

                        int product_quantity = DatabaseHelper.checkInventory(product_id, conn);

                        if (product_quantity == 0) {
                            System.out.print("Out of Stock.");
                            break;
                        }
                        if (product_quantity == -1){
                            System.out.print("Product Does Not Exist.");
                            break;
                        }

                        System.out.printf("Quantity Available: %d\n", product_quantity);
                        System.out.print("Quantity to Order: ");
                        int qty = input.nextInt();

                        if (qty > product_quantity) {
                            System.out.print("Quantity Exceeds Amount On Hand.");
                            break;
                        }

                        sql = String.format("INSERT INTO Orders(OrderNumber, ProductID, Qty, CustomerID, Tracking_Number," +
                                        " Delivery_Status, Date_Created) VALUES ('%s', %d, %d, %d, '%s', 'Processing', '%s')",
                                order_number, product_id, qty, customer_id, tracking_number, date_created);
                        query.executeUpdate(sql);

                        int new_qty = product_quantity - qty;
                        DatabaseHelper.updateQuantity(new_qty, product_id, conn);

                        do {
                            System.out.println("Would you like to add a different product?\nYes -- (Press 1)\n" +
                                    "No -- (Press 2)");
                            selection = input.nextInt();

                            if(selection == 2)
                                break;

                            System.out.print("Enter Shoe ID: ");
                            product_id = input.nextInt();
                            product_quantity = DatabaseHelper.checkInventory(product_id, conn);

                            if (product_quantity == 0) {
                                System.out.println("Out of Stock.");
                                break;
                            }
                            if (product_quantity == -1){
                                System.out.println("Product Does Not Exist.");
                                break;
                            }

                            System.out.printf("Quantity Available: %d\n", product_quantity);
                            System.out.print("Quantity to Order: ");
                            qty = input.nextInt();

                            if (qty > product_quantity) {
                                System.out.println("Quantity Exceeds Amount On Hand.");
                                break;
                            }

                            sql = String.format("INSERT INTO Orders(OrderNumber, ProductID, Qty, CustomerID, Tracking_Number," +
                                            " Delivery_Status, Date_Created) VALUES ('%s', %d, %d, %d, '%s', 'Processing', '%s')",
                                    order_number, product_id, qty, customer_id, tracking_number, date_created);
                            query.executeUpdate(sql);

                            new_qty = product_quantity - qty;
                            DatabaseHelper.updateQuantity(new_qty, product_id, conn);
                        } while(selection == 1);

                        System.out.println("Order Placed!");
                        break;
                    //View Order History
                    case 3:
                        System.out.println("View Order History:");
                        System.out.println("-------------------");
                        System.out.println("View All Orders -- (Press 1)\nView Current Orders -- (Press 2)");
                        selection = input.nextInt();
                        input.nextLine();

                        //View All Orders
                        if (selection == 1) {
                            System.out.print("User ID: ");
                            String customerID = input.nextLine();

                            int customer_ID = Integer.parseInt(customerID);
                            if (!DatabaseHelper.checkCustomerID(customer_ID, conn)){
                                System.out.println("ID Not Found. Please Register First.");
                                break;
                            }

                            sql = String.format("SELECT Product_Name, size, o.Qty, Cost FROM Orders o LEFT JOIN " +
                                    "Inventory i ON o.ProductID=i.ProductID WHERE CustomerID=%s", customerID);
                            rs = query.executeQuery(sql);

                            if(!rs.isBeforeFirst()){
                                System.out.print("No Orders Have Been Made On This Account Yet.");
                                break;
                            }

                            System.out.printf("-----------------------------------------------------%n");
                            System.out.printf("| %-25s | %-5s | %-4s | %-6s |%n",
                                    "Product_Name", "size", "Qty", "Cost");
                            System.out.printf("-----------------------------------------------------%n");
                            while (rs.next()) {
                                String product_name = rs.getString("Product_Name");
                                String size = rs.getString("size");
                                int usr_qty = rs.getInt("Qty");
                                double cost = rs.getDouble("Cost");

                                System.out.printf("| %-25s | %-5s | %02d   | %.2f |%n", product_name, size, usr_qty, cost);
                            }
                            rs.close();
                            System.out.printf("-----------------------------------------------------%n");
                        }
                        //View Current Orders
                        else if (selection == 2) {
                            System.out.print("User ID: ");
                            String customerID = input.nextLine();
                            int checkID = Integer.parseInt(customerID);

                            if (!DatabaseHelper.checkCustomerID(checkID, conn)){
                                System.out.println("ID Not Found. Please Register First.");
                                break;
                            }

                            sql = "SELECT * FROM Orders WHERE CustomerID=" + customerID + " AND " +
                                    "(Delivery_Status='Processing' OR Delivery_Status='Shipped')";
                            rs = query.executeQuery(sql);

                            if (!rs.isBeforeFirst()){
                                System.out.print("You Do Not Have Any Current Orders.");
                                break;
                            }

                            System.out.printf("----------------------------------------------------------------------%n");
                            System.out.printf("| %-11s | %-9s | %-4s | %-15s | %-15s |%n", "OrderNumber", "ProductID",
                                    "Qty", "Tracking_Number", "Delivery_Status");
                            System.out.printf("----------------------------------------------------------------------%n");
                            while (rs.next()) {
                                String order_id = rs.getString("OrderNumber");
                                int productID = rs.getInt("ProductID");
                                int usr_qty = rs.getInt("Qty");
                                String tracking_num = rs.getString("Tracking_Number");
                                String deliv_status = rs.getString("Delivery_Status");

                                System.out.printf("| %-11s | %02d        | %02d   | %-15s | %-15s |%n",
                                        order_id, productID, usr_qty, tracking_num, deliv_status);
                            }
                            System.out.printf("----------------------------------------------------------------------%n");
                        }
                        break;
                }
            }
            //(Seller Portal)
            else if (selection == 3) {
                System.out.println("Seller Portal:");
                System.out.println("--------------");
                System.out.println("Register -- (Press 1)\nMake Sale -- (Press 2)\nView Sale History -- (Press 3)");
                selection = input.nextInt();

                switch(selection) {
                    //Registration
                    case 1:
                        input.nextLine();
                        System.out.print("First Name: ");
                        String first_name = input.nextLine();

                        System.out.print("Last Name: ");
                        String last_name = input.nextLine();

                        System.out.print("Email: ");
                        String email = input.nextLine();

                        System.out.print("Phone Number (Ex. ###-###-####): ");
                        String phone_num = input.nextLine();

                        System.out.print("Street Address: ");
                        String street_add = input.nextLine();

                        System.out.print("City: ");
                        String city = input.nextLine();

                        System.out.print("Zipcode: ");
                        int zipcode = input.nextInt();
                        input.nextLine();

                        System.out.print("Payout Method (ACH or Paypal): ");
                        String payout_method = input.nextLine();

                        sql = String.format("INSERT INTO Sellers(FirstName, LastName, Email, Phone, Street, City, Zipcode," +
                                        "Payout_Method) VALUES('%s', '%s', '%s', '%s', '%s', '%s', %d, '%s')", first_name, last_name,
                                email, phone_num, street_add, city, zipcode, payout_method);
                        query.executeUpdate(sql);

                        System.out.println("Registration Complete!");
                        break;
                    //Make Sale
                    case 2:
                        String invoice_number = DatabaseHelper.invoiceGenerator();
                        String date_created = DatabaseHelper.getDate();

                        System.out.print("Please Enter Your ID: ");
                        int seller_id = input.nextInt();

                        if (!DatabaseHelper.checkSellerID(seller_id, conn)){
                            System.out.println("ID Not Found. Please Register First.");
                            break;
                        }

                        System.out.print("Enter Shoe ID: ");
                        int product_id = input.nextInt();

                        int product_quantity = DatabaseHelper.checkInventory(product_id, conn);
                        if (product_quantity == -1){
                            System.out.print("Product Does Not Exist.");
                            break;
                        }

                        System.out.print("Quantity to Sell: ");
                        int qty = input.nextInt();

                        sql = String.format("INSERT INTO SellerSales(InvoiceNumber, ProductID, Qty, SellerID, Date_Created)" +
                                " VALUES('%s', %d, %d, %d, '%s')", invoice_number, product_id, qty, seller_id, date_created);
                        query.executeUpdate(sql);

                        int new_quantity = product_quantity + qty;
                        DatabaseHelper.updateQuantity(new_quantity, product_id, conn);

                        System.out.print("Sale Made!");
                        break;
                    //View Sale History
                    case 3:
                        input.nextLine();
                        System.out.print("Seller ID: ");
                        String sellerID = input.nextLine();

                        int seller_check = Integer.parseInt(sellerID);
                        if (!DatabaseHelper.checkSellerID(seller_check, conn)){
                            System.out.println("ID Not Found. Please Register First.");
                            break;
                        }

                        sql = String.format("SELECT * FROM SellerSales WHERE SellerID=%s", sellerID);
                        rs = query.executeQuery(sql);

                        if (!rs.isBeforeFirst()){
                            System.out.print("No Sales Made On This Account Yet.");
                            break;
                        }

                        System.out.printf("--------------------------------------------------%n");
                        System.out.printf("| %-13s | %-9s | %-3s | %-12s |%n", "InvoiceNumber", "ProductID",
                                "Qty", "Date_Created");
                        System.out.printf("--------------------------------------------------%n");
                        while (rs.next()) {
                            String invoiceNumber = rs.getString("InvoiceNumber");
                            int productID = rs.getInt("ProductID");
                            int seller_qty = rs.getInt("Qty");
                            String dateCreated = rs.getString("Date_Created");

                            System.out.printf("| %-13s | %02d        | %02d  | %-12s |%n", invoiceNumber, productID,
                                    seller_qty, dateCreated);
                        }
                        System.out.printf("--------------------------------------------------%n");
                        break;
                }
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
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) { }
            }
        }
    }
}
