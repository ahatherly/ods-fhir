package uk.nhs.fhir.ods.datasource.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import uk.nhs.fhir.ods.datasource.IDataSource;
import uk.nhs.fhir.ods.model.Organisation;
import uk.nhs.fhir.ods.util.PropertyReader;

/**
 * NOTE - This is a test class only! It has no connection pooling, or any other optimisations for use in a proper system!
 * @author Adam Hatherly
 */
public class PostgresDataSource implements IDataSource {
	private static final Logger log = Logger.getLogger(PostgresDataSource.class.getName());

	private Connection getConnection() throws ClassNotFoundException, SQLException {
         Class.forName("org.postgresql.Driver");
         Connection c = DriverManager.getConnection(PropertyReader.getProperty("DB_URL"),
									        		 PropertyReader.getProperty("DB_USER"),
									        		 PropertyReader.getProperty("DB_PASSWORD"));
         c.setAutoCommit(false);
         log.fine("Postgres database connection opened");
         return c;
	}
	
	public Organisation getOrgByID(String id) {
		  log.fine("getOrgByID called");
	      Connection c = null;
	      PreparedStatement preparedStatement = null;
	      Organisation org = null;
	      try {
	         c = getConnection();

	         // Get Organisation record
	         String sql = "SELECT * FROM public.organisations WHERE UPPER(odscode) = UPPER(?) AND record_class = 'HSCOrg' LIMIT 1";
	         preparedStatement = c.prepareStatement(sql);
			 preparedStatement.setString(1, id);
			 ResultSet rs = preparedStatement.executeQuery();
	         if (rs.next()) {
	            org = new Organisation();
	            org.setBasicOrgDetails(rs);
	         } else {
	        	 // No result
	        	 return null;
	         }
	         rs.close();
	         preparedStatement.close();
	         
	         // TODO: Add roles
	         // TODO: Add relationships
	         
	         // Add addresses
	         sql = "SELECT address_line1, address_line2, address_line3, town, county, " +
	                  "post_code, country, uprn, location_id " +
	                  "FROM addresses a WHERE UPPER(a.org_odscode) = UPPER(?);";
	         preparedStatement = c.prepareStatement(sql);
			 preparedStatement.setString(1, id);
			 rs = preparedStatement.executeQuery();
	         while (rs.next()) {
	            org.addAddress(rs);
	         }
	         
	         rs.close();
	         preparedStatement.close();
	         c.close();
	      } catch ( Exception e ) {
	         e.printStackTrace();
	      }
	      return org;
	}
	
	public List<Organisation> getAllOrganisations() {
		  log.fine("getAllOrganisations called");
	      Connection c = null;
	      Statement stmt = null;
	      PreparedStatement preparedStatement = null;
	      ResultSet address_resultset = null;
	      
	      List<Organisation> resultList = new ArrayList<Organisation>();
	      
	      String add_sql = "SELECT address_line1, address_line2, address_line3, town, county, " +
                  "post_code, country, uprn, location_id " +
                  "FROM addresses a WHERE a.org_odscode = ?;";
	      
	      try {
	         c = getConnection();
	         stmt = c.createStatement();
	         ResultSet rs = stmt.executeQuery( "SELECT * FROM public.organisations LIMIT 50;" );
	         while ( rs.next() ) {
	        	 Organisation org = new Organisation();
		         org.setBasicOrgDetails(rs);
		         
		         // Add addresses
		         preparedStatement = c.prepareStatement(add_sql);
				 preparedStatement.setString(1, org.getOdscode());
				 address_resultset = preparedStatement.executeQuery();
		         while (address_resultset.next()) {
		            org.addAddress(address_resultset);
		         }
		         resultList.add(org);
		         address_resultset.close();
		         preparedStatement.close();
	         }
	         rs.close();
	         stmt.close();
	         c.close();
	      } catch ( Exception e ) {
	         e.printStackTrace();
	      }
	      return resultList;
	}
	
	
	public List<Organisation> getOrganisationByName(boolean exact, String name) {
		log.fine("getAllOrganisations called");
	      Connection c = null;
	      PreparedStatement preparedStatement = null;
	      PreparedStatement addressPreparedStatement = null;
	      ResultSet address_resultset = null;
	      
	      List<Organisation> resultList = new ArrayList<Organisation>();
	      
	      String add_sql = "SELECT address_line1, address_line2, address_line3, town, county, " +
                "post_code, country, uprn, location_id " +
                "FROM addresses a WHERE a.org_odscode = ?;";
	      
	      try {
	         c = getConnection();
	         // Get Organisation record
	         
	         String sql = null;
	         
	         if (exact) {
	        	 sql = "SELECT * FROM public.organisations WHERE UPPER(name) = UPPER(?) AND record_class = 'HSCOrg'";
	         } else {
	        	 sql = "SELECT * FROM public.organisations WHERE name ILIKE ? AND record_class = 'HSCOrg'";
	        	 name = "%" + name + "%";
	         }
	         preparedStatement = c.prepareStatement(sql);
			 preparedStatement.setString(1, name);
			 ResultSet rs = preparedStatement.executeQuery();
	         while ( rs.next() ) {
	        	 Organisation org = new Organisation();
		         org.setBasicOrgDetails(rs);
		         
		         // Add addresses
		         addressPreparedStatement = c.prepareStatement(add_sql);
		         addressPreparedStatement.setString(1, org.getOdscode());
				 address_resultset = addressPreparedStatement.executeQuery();
		         while (address_resultset.next()) {
		            org.addAddress(address_resultset);
		         }
		         resultList.add(org);
		         address_resultset.close();
		         addressPreparedStatement.close();
	         }
	         rs.close();
	         preparedStatement.close();
	         c.close();
	      } catch ( Exception e ) {
	         e.printStackTrace();
	      }
	      return resultList;
	}	
	
}
