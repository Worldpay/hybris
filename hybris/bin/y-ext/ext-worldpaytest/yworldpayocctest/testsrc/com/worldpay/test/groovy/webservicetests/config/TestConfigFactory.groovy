package com.worldpay.test.groovy.webservicetests.config

import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.config.ConfigurationService

class TestConfigFactory {

    private static final String DEFAULT_WEBROOT = 'rest'
    private static final String DEFAULT_AUTHORIZATION_WEBROOT = 'authorizationserver'

    private static final String webroot = DEFAULT_WEBROOT
    private static Map<String, ConfigObject> configsCache = new HashMap<>()

    static synchronized ConfigObject createConfig(final String version, final String propertyFileClassPath) {
        final String key = version + propertyFileClassPath
        if (configsCache.containsKey(key)) {
            return configsCache.get(key)
        } else {
            final ConfigObject config = createConfigInternal(version, propertyFileClassPath)
            configsCache.put(key, config)
            return config
        }
    }

    static ConfigObject createConfigInternal(final String version, final String propertyFileClassPath) {
        final ConfigurationService configurationService = Registry.getApplicationContext().getBean("configurationService")
        final String spockHost = configurationService.configuration.getProperty("spockHost")
        final String testHtmlPath = configurationService.configuration.getProperty("yworldpayocctest.test.html.path")
        final Map<String, Object> initialValues = new HashMap<>()
        initialValues.put("HOST", spockHost)
        initialValues.put("WEBROOT", webroot)
        initialValues.put("VERSION", version)
        initialValues.put("AUTHWEBROOT", DEFAULT_AUTHORIZATION_WEBROOT)
        initialValues.put("TEST_HTML_PATH", testHtmlPath)
        return createConfigFromConfigProperties(propertyFileClassPath, initialValues)
    }

    private static ConfigObject createConfigFromConfigProperties(
            final String propertyFile, final Map<String, Object> initialValues) {
        final String configScript = Registry.getApplicationContext().getBean("yWorldpayOccTestSetup").getClass().getResource(propertyFile).text
        if (configScript == null) return null

        final ConfigSlurper configSlurper = new ConfigSlurper()
        configSlurper.setBinding(initialValues)

        final ConfigObject configObject = configSlurper.parse(configScript)
        return configObject
    }
}
