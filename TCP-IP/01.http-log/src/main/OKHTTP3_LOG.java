import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class OKHTTP3_LOG {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder()
                .eventListenerFactory(HttpEventListener.FACTORY)
                .build();
        Request request = new Request.Builder()
                .url("https://www.qiniu.com/?a=1&b=2")
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            //System.out.println(response.body().string());
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HttpEventListener extends EventListener {

    public static final Factory FACTORY = new Factory() {
        final AtomicLong nextCallId = new AtomicLong(1L);

        public EventListener create(Call call) {
            long callId = nextCallId.getAndIncrement();
            return new HttpEventListener(callId, call.request().url(), System.nanoTime());
        }
    };

    private long callStartNanos;
    private long dnsStartNanos;
    private long connectStartNanos;
    private long secureConnectStartNanos;
    private long requestHeadersStartNanos;
    private long requestBodyStartNanos;
    private long responseHeadersStartNanos;
    private long responseBodyStartNanos;

    public HttpEventListener(long callId, HttpUrl url, long callStartNanos) {
    }

    @Override
    public void callStart(Call call) {
        super.callStart(call);
        System.out.println("call start.");
        System.out.println("method: " + call.request().method());
        System.out.println("url: " + call.request().url().toString());
        System.out.println("schema: " + call.request().url().scheme());
        System.out.println("host: " + call.request().url().host());
        System.out.println("encoded path: " + call.request().url().encodedPath());
        System.out.println("encoded query: " + call.request().url().encodedQuery());
        callStartNanos = System.nanoTime();
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
        System.out.println("dns start.");
        System.out.println("domain: " + domainName);
        dnsStartNanos = System.nanoTime();
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        long nanoTime = System.nanoTime();
        System.out.printf("dns end, cost %.3fs.\n", (nanoTime - dnsStartNanos) / 1000000000d);
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
        System.out.println("connect start.");
        System.out.println("server ip: " + inetSocketAddress.getAddress().getHostAddress());
        connectStartNanos = System.nanoTime();
    }

    @Override
    public void secureConnectStart(Call call) {
        super.secureConnectStart(call);
        System.out.println("secure connect start.");
        secureConnectStartNanos = System.nanoTime();
    }

    @Override
    public void secureConnectEnd(Call call, Handshake handshake) {
        super.secureConnectEnd(call, handshake);
        long nanoTime = System.nanoTime();
        System.out.printf("secure connect end, cost %.3fs.\n", (nanoTime - secureConnectStartNanos) / 1000000000d);
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        long nanoTime = System.nanoTime();
        System.out.printf("connect end, cost %.3fs.\n", (nanoTime - connectStartNanos) / 1000000000d);
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
        long nanoTime = System.nanoTime();
        System.out.printf("connect failed for %s, cost %.3fs\n.", ioe.getMessage(), (nanoTime - connectStartNanos) / 1000000000d);
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        super.connectionAcquired(call, connection);
        System.out.println("connect acquired.");
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        super.connectionReleased(call, connection);
        System.out.println("connect released.");
    }

    @Override
    public void requestHeadersStart(Call call) {
        super.requestHeadersStart(call);
        System.out.println("request headers start.");
        requestHeadersStartNanos = System.nanoTime();
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        super.requestHeadersEnd(call, request);
        long nanoTime = System.nanoTime();
        System.out.printf("request headers end, cost %.3fs.\n", (nanoTime - requestHeadersStartNanos) / 1000000000d);
        System.out.println("request headers: [" + request.headers().toString().replace("\n", ", ") + "]");
    }

    @Override
    public void requestBodyStart(Call call) {
        super.requestBodyStart(call);
        System.out.println("request body start.");
        requestBodyStartNanos = System.nanoTime();
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        long nanoTime = System.nanoTime();
        System.out.printf("request body end, cost %.3fs, count %d bytes.\n", (nanoTime - requestBodyStartNanos) / 1000000000d, byteCount);
    }

    @Override
    public void responseHeadersStart(Call call) {
        super.responseHeadersStart(call);
        System.out.println("response headers start.");
        responseHeadersStartNanos = System.nanoTime();
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        super.responseHeadersEnd(call, response);
        long nanoTime = System.nanoTime();
        System.out.printf("response headers end, cost %.3fs.\n", (nanoTime - responseHeadersStartNanos) / 1000000000d);
        System.out.println("status code: " + response.code());
        System.out.println("response headers: [" + response.headers().toString().replace("\n", ", ") + "]");
    }

    @Override
    public void responseBodyStart(Call call) {
        super.responseBodyStart(call);
        System.out.println("response body start.");
        responseBodyStartNanos = System.nanoTime();
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        long nanoTime = System.nanoTime();
        System.out.printf("response body end, cost %.3fs, count %d bytes.\n", (nanoTime - responseBodyStartNanos) / 1000000000d, byteCount);
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
        long nanoTime = System.nanoTime();
        System.out.printf("call end, cost %.3fs.\n", (nanoTime - callStartNanos) / 1000000000d);
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        super.callFailed(call, ioe);
        long nanoTime = System.nanoTime();
        System.out.printf("call failed for %s, cost %.3fs.\n", ioe.getMessage(), (nanoTime - callStartNanos) / 1000000000d);
    }
}
