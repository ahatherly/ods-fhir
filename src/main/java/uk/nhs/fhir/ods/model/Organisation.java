package uk.nhs.fhir.ods.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Organization;

/**
 * Simple wrapper object for one or more FHIR resources representing an ODS ORG record
 * @author Adam Hatherly
 */
public class Organisation {

	private Organization fhirOrg = null;
	private String odscode = null;
	
	public Organisation() {
		fhirOrg = new Organization();
	}
	
	public void setBasicOrgDetails(ResultSet rs) throws SQLException {
		odscode = rs.getString("odscode");
        String name = rs.getString("name");
        boolean active = rs.getString("status").equals("Active");
        fhirOrg.setId(odscode);
        fhirOrg.setName(name);
        fhirOrg.setActive(active);
	}
	
	public void addAddress(ResultSet rs) throws SQLException {
		String line1 = rs.getString("address_line1");
		String line2 = rs.getString("address_line2");
		String line3 = rs.getString("address_line3");
		String town = rs.getString("town");
		String county = rs.getString("county");
		String post_code = rs.getString("post_code");
		String country = rs.getString("country");
		//String uprn = rs.getString("uprn");
		//String location_id = rs.getString("location_id");
		Address add = new Address();
		add.addLine(line1);
		add.addLine(line2);
		add.addLine(line3);
		add.setCity(town);
		add.setDistrict(county);
		add.setPostalCode(post_code);
		add.setCountry(country);
		fhirOrg.addAddress(add);
	}

	public Organization getFhirOrg() {
		return fhirOrg;
	}

	public String getOdscode() {
		return odscode;
	}
	
	public static List<Organization> convertListToFHIROrgs(List<Organisation> orgList) {
		List<Organization> newList = new ArrayList<Organization>(orgList.size());
		for (Organisation o : orgList) {
			newList.add(o.getFhirOrg());
		}
		return newList;
	}
}
