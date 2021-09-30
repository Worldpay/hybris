package com.worldpay.jalo;

import com.worldpay.constants.WorldpayaddonbackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class WorldpayaddonbackofficeManager extends GeneratedWorldpayaddonbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( WorldpayaddonbackofficeManager.class.getName() );
	
	public static final WorldpayaddonbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (WorldpayaddonbackofficeManager) em.getExtension(WorldpayaddonbackofficeConstants.EXTENSIONNAME);
	}
	
}
