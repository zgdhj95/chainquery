package io.itit.itf.okhttp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.itf.okhttp.callback.Callback;
import io.itit.itf.okhttp.ssl.X509TrustManagerImpl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 
 * @author icecooly
 *
 */
public class RequestCall {
	//
	private static Logger logger = LoggerFactory.getLogger(RequestCall.class);
	//
	private OkHttpRequest okHttpRequest;
	private Request request;
	private Call call;
	//
	private long readTimeOut;
	private long writeTimeOut;
	private long connTimeOut;
	private Boolean retryOnConnectionFailure;
	//
	protected List<Interceptor> networkInterceptors;
	protected SSLContext sslContext;

	//
	public RequestCall(OkHttpRequest request) {
		this.okHttpRequest = request;
		this.networkInterceptors = new ArrayList<>();
	}

	public RequestCall readTimeOut(long readTimeOut) {
		this.readTimeOut = readTimeOut;
		return this;
	}

	public RequestCall writeTimeOut(long writeTimeOut) {
		this.writeTimeOut = writeTimeOut;
		return this;
	}

	public RequestCall connTimeOut(long connTimeOut) {
		this.connTimeOut = connTimeOut;
		return this;
	}

	public RequestCall retryOnConnectionFailure(boolean retryOnConnectionFailure) {
		this.retryOnConnectionFailure = retryOnConnectionFailure;
		return this;
	}

	public RequestCall addNetworkInterceptor(Interceptor networkInterceptor) {
		networkInterceptors.add(networkInterceptor);
		return this;
	}

	public RequestCall sslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
		return this;
	}

	private void setSSLSocketFactory(OkHttpClient.Builder builder, SSLContext sslContext) {
		SSLSocketFactory sslSocketFactory = null;
		final X509TrustManager trustManager = new X509TrustManagerImpl();
		try {
			sslSocketFactory = sslContext.getSocketFactory();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		builder.sslSocketFactory(sslSocketFactory, trustManager).hostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
	}

	public Call buildCall(Callback callback) {
		OkHttpClient client = FastHttpClient.okHttpClient;
		if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0 || networkInterceptors.size() > 0
				|| sslContext != null || retryOnConnectionFailure != null) {
			OkHttpClient.Builder builder = FastHttpClient.okHttpClient.newBuilder();
			if (connTimeOut > 0) {
				builder.readTimeout(connTimeOut, TimeUnit.MILLISECONDS);
			}
			if (readTimeOut > 0) {
				builder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
			}
			if (writeTimeOut > 0) {
				builder.readTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
			}
			//
			networkInterceptors.forEach(i -> builder.addNetworkInterceptor(i));
			//
			if (sslContext != null) {
				setSSLSocketFactory(builder, sslContext);
			}
			//
			if (retryOnConnectionFailure != null) {
				builder.retryOnConnectionFailure(retryOnConnectionFailure);
			}
			client = builder.build();
		}
		logger.info("client.dispatcher().queuedCallsCount=" + client.dispatcher().queuedCallsCount()
				+ " runningCallsCount=" + client.dispatcher().runningCallsCount());
		return buildCall(callback, client);
	}

	public Call buildCall(Callback callback, OkHttpClient okHttpClient) {
		request = createRequest(callback);
		call = okHttpClient.newCall(request);
		return call;
	}

	private Request createRequest(Callback callback) {
		return okHttpRequest.createRequest(callback);
	}

	public Response execute() throws Exception {
		buildCall(null);
		try {
			Response rsp = new Response(call.execute());
			if (rsp.isSuccessful()) {
				FastHttpClientStat.onReqSuccess();
			} else {
				FastHttpClientStat.onReqFailure(call.request().url().toString(), null);
			}
			return rsp;
		} catch (Exception e) {
			FastHttpClientStat.onReqFailure(call.request().url().toString(), e);
			throw e;
		}
	}

	public void executeAsync(Callback callback) {
		buildCall(callback);
		execute(this, callback);
	}

	public void executeAsync(Callback callback, OkHttpClient client) {
		buildCall(callback, client);
		logger.info("client.dispatcher().queuedCallsCount=" + client.dispatcher().queuedCallsCount()
				+ " runningCallsCount=" + client.dispatcher().runningCallsCount());
		execute(this, callback);
	}

	private void execute(final RequestCall requestCall, Callback callback) {
		final Callback finalCallback = callback;
		final long id = requestCall.getOkHttpRequest().getId();
		requestCall.getCall().enqueue(new okhttp3.Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				FastHttpClientStat.onReqFailure(call.request().url().toString(), e);
				if (finalCallback != null) {
					finalCallback.onFailure(call, e, id);
				}
			}

			@Override
			public void onResponse(final Call call, final okhttp3.Response response) {
				FastHttpClientStat.onReqSuccess();
				if (finalCallback != null) {
					finalCallback.onResponse(call, new Response(response), id);
				}
			}
		});
	}

	public Call getCall() {
		return call;
	}

	public Request getRequest() {
		return request;
	}

	public OkHttpRequest getOkHttpRequest() {
		return okHttpRequest;
	}

	public void cancel() {
		if (call != null) {
			call.cancel();
		}
	}
}
