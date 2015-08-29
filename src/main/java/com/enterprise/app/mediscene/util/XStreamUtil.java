package com.enterprise.app.mediscene.util;

import com.enterprise.app.mediscene.context.BatchInitializationResponse;
import com.enterprise.app.mediscene.context.BatchQueryResponse;
import com.enterprise.app.mediscene.context.GenericEntity;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public final class XStreamUtil {

	/**
	 * <h3>Thread safety</h3>
	 * <p>
	 * The XStream instance is thread-safe. That is, once the XStream instance
	 * has been created and configured, it may be shared across multiple threads
	 * allowing objects to be serialized/deserialized concurrently.
	 * <em>Note, that this only applies if annotations are not 
	 * auto-detected on -the-fly.</em>
	 * </p>
	 */

	private static final XStream xstream;
	static {
		xstream = new XStream(new DomDriver());
		// xstream.alias("stgrequestlog", BasicRequestLog.class);
		xstream.alias("BatchInfo", BatchInitializationResponse.class);
		//xstream.alias("ResponseDataForUpdateRequest",BatchDataCollectionResponse.class);
		xstream.alias("ResponseDataForReadRequest", BatchQueryResponse.class);
		xstream.alias("GenericEntity", GenericEntity.class);
	}

	public static void initialize() {
		//
	}

	public static final XStream getXstream() {
		return xstream;
	}

}
