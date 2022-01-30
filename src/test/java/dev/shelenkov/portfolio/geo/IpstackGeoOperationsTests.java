package dev.shelenkov.portfolio.geo;

import dev.shelenkov.portfolio.geo.config.IpstackProperties;
import dev.shelenkov.portfolio.geo.exception.GeoDataNotFoundException;
import dev.shelenkov.portfolio.geo.exception.GeoProviderFailedRequestException;
import dev.shelenkov.portfolio.geo.exception.GeoServiceException;
import dev.shelenkov.portfolio.support.dto.CountryGeoData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(IpstackGeoOperations.class)
@EnableConfigurationProperties(IpstackProperties.class)
@TestPropertySource(properties = {
    "ipstack.url=http://api.ipstack.com",
    "ipstack.key=key"
})
public class IpstackGeoOperationsTests {

    @Autowired
    private IpstackGeoOperations operations;

    @Autowired
    private MockRestServiceServer server;

    @Value("classpath:geo/success_response.json")
    private Resource successResponse;

    @Value("classpath:geo/error_response.json")
    private Resource errorResponse;

    @Value("classpath:geo/ip_not_found_response.json")
    private Resource ipNotFoundResponse;

    @Test
    public void getGeoData_providerReturnsFullData_dtoWithNeededData() throws GeoServiceException {
        server.expect(requestTo("http://api.ipstack.com/77.243.99.72?access_key=key&output=json"))
            .andRespond(withSuccess(successResponse, MediaType.APPLICATION_JSON));

        CountryGeoData result = operations.getGeoData("77.243.99.72");

        assertThat(result).isEqualTo(new CountryGeoData("RU", "Russia"));
    }

    @Test
    public void getGeoData_500ResponseFromProvider_GeoProviderFailedRequestException() {
        server.expect(requestTo("http://api.ipstack.com/77.243.99.72?access_key=key&output=json"))
            .andRespond(withServerError());

        assertThatExceptionOfType(GeoProviderFailedRequestException.class).isThrownBy(() -> {
            operations.getGeoData("77.243.99.72");
        });
    }

    @Test
    public void getGeoData_providerRefusedServeRequest_GeoProviderFailedRequestException() {
        server.expect(requestTo("http://api.ipstack.com/77.243.99.72?access_key=key&output=json"))
            .andRespond(withSuccess(errorResponse, MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(GeoProviderFailedRequestException.class).isThrownBy(() -> {
            operations.getGeoData("77.243.99.72");
        });
    }

    @Test
    public void getGeoData_providerDoesNotKnowThisIp_GeoDataNotFoundException() {
        server.expect(requestTo("http://api.ipstack.com/77.243.99.72?access_key=key&output=json"))
            .andRespond(withSuccess(ipNotFoundResponse, MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(GeoDataNotFoundException.class).isThrownBy(() -> {
            operations.getGeoData("77.243.99.72");
        });
    }
}
