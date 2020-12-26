package dev.shelenkov.portfolio.geo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IpstackResponse {

    private String ip;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("country_name")
    private String countryName;

    // Following fields are present in response only in case of error (see examples in tests)

    private boolean success = true;

    private IpstackResponse.Error error;

    @Data
    public static class Error {

        private long code;
        private String type;
        private String info;
    }
}
