package dev.shelenkov.portfolio.geo;

import dev.shelenkov.portfolio.geo.config.IpstackConfig;
import dev.shelenkov.portfolio.geo.exception.GeoDataNotFoundException;
import dev.shelenkov.portfolio.geo.exception.GeoProviderFailedRequestException;
import dev.shelenkov.portfolio.geo.exception.GeoServiceException;
import dev.shelenkov.portfolio.geo.response.IpstackResponse;
import dev.shelenkov.portfolio.support.dto.CountryGeoData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class IpstackGeoOperations implements GeoOperations {

    private final RestTemplate restTemplate;
    private final IpstackConfig ipstackConfig;

    public IpstackGeoOperations(RestTemplateBuilder restTemplateBuilder,
                                IpstackConfig ipstackConfig) {
        this.restTemplate = restTemplateBuilder.build();
        this.ipstackConfig = ipstackConfig;
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public CountryGeoData getGeoData(String ip) throws GeoServiceException {
        ResponseEntity<IpstackResponse> response;
        try {
            response = restTemplate.getForEntity(buildRequest(ip), IpstackResponse.class);
        } catch (RestClientException e) {
            log.error("Fetch geo data error", e);
            throw new GeoProviderFailedRequestException("Fetch geo data error for IP: " + ip, e);
        }

        Validate.validState(response.getStatusCode().is2xxSuccessful());
        Validate.validState(response.getBody() != null);

        IpstackResponse body = response.getBody();

        if (!body.isSuccess()) {
            IpstackResponse.Error error = body.getError();
            log.error("Geo provider refused to serve request. IP: {}, Error: {}", ip, error);
            throw new GeoProviderFailedRequestException(String.format(
                "Geo provider refused to serve request. IP: %s, Error code: %d",
                ip, error.getCode()
            ));
        }
        if (body.getCountryCode() == null || body.getCountryName() == null) {
            log.info("Geo data object {} not filled for IP: {}", body, ip);
            throw new GeoDataNotFoundException("Geo data not found for IP: " + ip);
        }

        return new CountryGeoData(body.getCountryCode(), body.getCountryName());
    }

    @SneakyThrows(URISyntaxException.class)
    private URI buildRequest(String ip) {
        return new URIBuilder(ipstackConfig.getUrl())
            .setPath(ip)
            .setParameter("access_key", ipstackConfig.getKey())
            .setParameter("output", "json")
            .build();
    }
}
