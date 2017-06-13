package uk.nhs.fhir.ods.datasource;

import org.junit.Test;

import uk.nhs.fhir.ods.datasource.postgres.PostgresDataSource;

public class PostgresDataSourceTest {

	@Test
	public void testPostgresDataSource() {
		PostgresDataSource ds = new PostgresDataSource();
		//ds.runQuery();
		ds.getOrgByID("5N4GA");
	}

}
