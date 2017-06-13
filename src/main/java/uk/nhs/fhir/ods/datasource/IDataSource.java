package uk.nhs.fhir.ods.datasource;

import java.util.List;

import org.hl7.fhir.dstu3.model.Organization;

import uk.nhs.fhir.ods.model.Organisation;

/**
 * Interface for accessing database layer
 * @author Adam Hatherly
 */
public interface IDataSource {
	
	/**
	 * Get a single Organisation by ID
	 * @param id
	 * @return
	 */
	public Organisation getOrgByID(String id);
	
	/**
	 * Get a list of all Organisations
	 * @return
	 */
	public List<Organisation> getAllOrganisations();
	
	/**
	 * Get a list of Organisations matching on name
	 * @param exact Whether to do an exact match or not
	 * @param name
	 * @return
	 */
	public List<Organisation> getOrganisationByName(boolean exact, String name);
}
