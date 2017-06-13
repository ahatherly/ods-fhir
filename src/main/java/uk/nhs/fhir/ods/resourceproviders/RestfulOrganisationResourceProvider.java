package uk.nhs.fhir.ods.resourceproviders;

import java.util.List;
import java.util.logging.Logger;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.fhir.ods.datasource.DataSourceFactory;
import uk.nhs.fhir.ods.datasource.IDataSource;
import uk.nhs.fhir.ods.model.Organisation;

/**
 * This is our resource provider for FHIR Organization resources. We can implement the various FHIR operations by
 * just implementing the relevant methods and adding an annotation.
 * @author Adam Hatherly
 * @see http://hapifhir.io/doc_rest_operations.html
 */
public class RestfulOrganisationResourceProvider implements IResourceProvider {
	
	private static final Logger log = Logger.getLogger(RestfulOrganisationResourceProvider.class.getName());
	private IDataSource dataSource = DataSourceFactory.getDataSource();
	
	public Class<Organization> getResourceType() {
		return Organization.class;
	}

	/**
	 * This method is the simple FHIR read operation - it takes an ID (in our case the ODS code),
	 * and returns a single FHIR resource
	 * @param theId
	 * @return FHIR Organization object
	 */
	@Read()
	public Organization getResourceById(@IdParam IdType theId) {
		log.info("Request for ORG by ID:" + theId.getIdPart());
		Organisation org = dataSource.getOrgByID(theId.getIdPart());
		log.info(org.toString());
		return org.getFhirOrg();
	}
	
	/**
	 * This method implements the FHIR search operation to allow for either exact or partial name
	 * matches.
	 * @param theName
	 * @return A list of FHIR Organization resources - these will be wrapped in a FHIR bundle
	 * @see https://www.hl7.org/fhir/search.html
	 */
	@Search()
    public List<Organization> getOrganisationByName(@RequiredParam(name = Organization.SP_NAME) StringParam theName) {
		String valueToMatch = theName.getValue();
	    
		if (theName.isExact()) {
			// Do an exact match search
			List<Organisation> foundList = dataSource.getOrganisationByName(true, valueToMatch);
			return Organisation.convertListToFHIROrgs(foundList);
		} else {
			// Do a fuzzy search if possible
			List<Organisation> foundList = dataSource.getOrganisationByName(false, valueToMatch);
			return Organisation.convertListToFHIROrgs(foundList);
		}
    }
	
	/**
	 * This is a search with no parameters, which will return all Organizations. In this case it is limited to the
	 * first 50. Ideally this would use paging. 
	 * @return A list of FHIR Organization resources - these will be wrapped in a FHIR bundle
	 */
    @Search
    public List<Organization> getAllOrganisations() {
        //TODO: Implement pagination!
    	log.info("Request for ALL Organisations (limit to 50)");
        List<Organisation> foundList = dataSource.getAllOrganisations();
        return Organisation.convertListToFHIROrgs(foundList);
    }
}
