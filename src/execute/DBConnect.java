package execute;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
	private String data = "";

	private Connection con = null;
	private Statement st = null;
	private ResultSet rst = null;

	public void connect() throws IOException {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/faultdetection", "root", "");
			st = con.createStatement();
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException");
		} catch (SQLException e) {
			System.out.println("SQLException");
		}
		// User input and execution section
		/*
		 * while(true) {
		 * System.out.println("\nChoose an option from the given choices : ");
		 * System.out.println(
		 * "\n1-Display Commands received\n2-Clear variable data\n3-Exit\n4-Show data from the Variable."
		 * ); int ch=Integer.parseInt(br.readLine());
		 * 
		 * try{ executeOption(ch); } catch(SQLException sqle) {
		 * System.out.println("SQLException caught"); }
		 * 
		 * }
		 */

	}
	public void connectToWeb()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			 con = DriverManager.getConnection("jdbc:mysql://db4free.net/saiprasadm", "saiprasad", "Saiprasad1996");
	           
			st = con.createStatement();
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException");
		} catch (SQLException e) {
			System.out.println("SQLException");
		}
	}

	public String fetchDataFromDB() throws SQLException {
		cleardata();
		System.out.println("\nDisplaying Element..\n");
		rst = st.executeQuery("SELECT * FROM faultdetect_t");
		while (rst.next()) {
			System.out.println(rst.getString("voltage") + "\t"
					+ rst.getString("data_receive"));
			data = data + rst.getString("data_sent");
		}
		return data;
	}

	public void closeDB() throws SQLException {
		con.close();
	}

	public void insertIntoDb(String voltage, String fault) throws SQLException {
		System.out.println("Data to be sent is Volatage = " + voltage
				+ " fault = " + fault);
		String query = "INSERT INTO `faultdetection`.`faultdetect_t` (`time`, `voltage`, `fault`) "
				+ "VALUES (CURRENT_TIMESTAMP, '"
				+ voltage
				+ "', '"
				+ fault
				+ "');";

		System.out.print("\nSending data to db\n");
		Main.updateGUI("\nSending data to DB");
		st.executeUpdate(query);
	}

	public String getString() {
		return data;
	}

	public String executeOption(int ch) throws SQLException {
		switch (ch) {
		case 1:
			cleardata();
			System.out.println("\nDisplaying Element..\n");
			rst = st.executeQuery("SELECT * FROM faultdetect_t");
			while (rst.next()) {
				System.out.println(rst.getString("voltage") + "\t"
						+ rst.getString("data_receive"));
				data = data + rst.getString("data_sent");
			}
			break;
		case 2:
			data = "";
			System.out.println("Variable data cleared");
			break;
		case 3:
			System.exit(0);
			break;
		case 4:
			System.out.println("\nData from the stored variable : " + data);
		}
		return data;
	}

	void cleardata() {
		this.data = "";
	}

	/*
	 * public static void main(String args[]) { DBConnect dbc = new DBConnect();
	 * try { dbc.connect(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 */
}