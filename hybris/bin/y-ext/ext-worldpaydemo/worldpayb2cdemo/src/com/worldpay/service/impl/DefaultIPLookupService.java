package com.worldpay.service.impl;

import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;
import com.worldpay.model.IPInformationModel;
import com.worldpay.service.IPLookupService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.MessageFormat;

/**
 * {@inheritDoc}
 */
public class DefaultIPLookupService implements IPLookupService {

    private static final Logger LOG = Logger.getLogger(DefaultIPLookupService.class);

    private static final String IP_API_JSON_URL = "ip.api.json.url";
    private static final String SUCCESS = "success";

    protected final ModelService modelService;
    protected final ConfigurationService configurationService;
    protected final RestTemplate restTemplate;

    public DefaultIPLookupService(final ModelService modelService, final ConfigurationService configurationService, final RestTemplate restTemplate) {
        this.modelService = modelService;
        this.configurationService = configurationService;
        this.restTemplate = restTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPInformationModel getIPInformation(final String ip) {
        final IPInformationModel ipInfo = modelService.create(IPInformationModel.class);
        ipInfo.setIp(ip);

        try {
            final URI uri = URI.create(String.format("%s%s", configurationService.getConfiguration().getString(IP_API_JSON_URL), ip));
            final JSONObject json = new JSONObject(restTemplate.getForObject(uri, String.class));

            if (isSuccess(json)) {
                populateIPInformation(ipInfo, json);
            }
        } catch (final RestClientException ex) {
            LOG.warn("There was an error getting the ip info from the API", ex);
        } catch (final JSONException ex) {
            LOG.warn(MessageFormat.format("JsonException when populating ip info for ip {0} - probably invalid json", ip), ex);
        }

        modelService.save(ipInfo);
        return ipInfo;
    }

    private void populateIPInformation(final IPInformationModel ipInfo, final JSONObject json) throws JSONException {
        ipInfo.setIsp(json.getString("isp"));
        ipInfo.setOrg(json.getString("org"));
        ipInfo.setLat(json.getString("lat"));
        ipInfo.setLon(json.getString("lon"));
    }

    private boolean isSuccess(final JSONObject json) throws JSONException {
        return SUCCESS.equals(json.getString("status"));
    }
}
