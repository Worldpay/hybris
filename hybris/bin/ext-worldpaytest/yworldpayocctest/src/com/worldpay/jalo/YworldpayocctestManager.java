package com.worldpay.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.worldpay.constants.YworldpayocctestConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class YworldpayocctestManager extends GeneratedYworldpayocctestManager {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( YworldpayocctestManager.class.getName() );

	public static final YworldpayocctestManager getInstance() 	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YworldpayocctestManager) em.getExtension(YworldpayocctestConstants.EXTENSIONNAME);
	}

}
