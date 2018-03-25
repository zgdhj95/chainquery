package io.itit.itf.okhttp;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 
 * @author icecooly
 *
 */
public class GetRequest extends OkHttpRequest {
	//
	public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, long id) {
		super(url, tag, params, headers, null, null, id);
	}

	@Override
	protected RequestBody buildRequestBody() {
		return null;
	}

	@Override
	protected Request buildRequest(RequestBody requestBody) {
		return builder.get().build();
	}
}
