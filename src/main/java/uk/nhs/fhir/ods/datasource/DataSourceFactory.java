package uk.nhs.fhir.ods.datasource;

import uk.nhs.fhir.ods.datasource.postgres.PostgresDataSource;

/**
 * Simple factory to get a data source (currently only Postgres)
 * @author Adam Hatherly
 */
public class DataSourceFactory {

	/**
	 * Get a data source to use
	 * @return
	 */
	public static IDataSource getDataSource() {
		return new PostgresDataSource();
	}
}
