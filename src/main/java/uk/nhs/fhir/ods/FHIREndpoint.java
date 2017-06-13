package uk.nhs.fhir.ods;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import uk.nhs.fhir.ods.resourceproviders.RestfulOrganisationResourceProvider;

/**
 * This is the main FHIR endpoint servlet that handles FHIR requests.
 * @author Adam Hatherly
 * @see http://hapifhir.io/doc_rest_server.html
 * 
 * Try CURL to a few URLs like this:
 * 
 * Read:
 * http://fhir-base-url/Organization/X09
 * http://fhir-base-url/Organization/X09?_format=xml
 * Search:
 * http://fhir-base-url/Organization
 * http://fhir-base-url/Organization?name:exact=ASHCROFT NH (CLECKHEATON)
 * http://fhir-base-url/Organization?name:contains=CLECKHEATON
 * 
 * 
 */
@WebServlet(urlPatterns= {"/ods/*"}, displayName="ODS FHIR Server", loadOnStartup = 1)
public class FHIREndpoint extends RestfulServer {

	private static final Logger log = Logger.getLogger(FHIREndpoint.class.getName());
	private static final long serialVersionUID = 3984765447077799404L;

	@Override
    protected void initialize() throws ServletException {
		// Explicitly set this as an STU3 FHIR server
		super.setFhirContext(FhirContext.forDstu3());
		
		// Register a resource provider for Organization resources (note: you can add others for Locations etc.)
		List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
		resourceProviders.add(new RestfulOrganisationResourceProvider());
		setResourceProviders(resourceProviders);
		log.info("FHIR Endpoint initialised");
    }
}
