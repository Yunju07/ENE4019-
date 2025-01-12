import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    /** Help variables */
    final static String CRLF = "\r\n";
    final static int HTTP_PORT = 80;
    /** Store the request parameters */
    String method;
    String URI;
    String version;
    String headers = "";
    /** Server and port */
    private String host;
    private int port;

    /** Create HttpRequest by reading it from the client socket */
    public HttpRequest(BufferedReader from) {
        String firstLine = "";
        try {
            firstLine = from.readLine(); // 첫 줄 읽기
        } catch (IOException e) {
            System.out.println("Error reading request line: " + e);
        }

        String[] tmp = firstLine.split(" ");
        method = tmp[0]; // HTTP 메서드 (예: GET)
        URI = tmp[1]; // URI
        version = tmp[2]; // HTTP 버전

        System.out.println("URI is: " + URI);

        if (!method.equals("GET")) {
            System.out.println("Error: Method not GET");
        }

        try {
            String line = from.readLine();
            while (line != null && line.length() != 0) { // 빈 줄이 나올 때까지 읽기
                headers += line + CRLF;

                /* Host 헤더 파싱하여 host와 port 설정 */
                if (line.startsWith("Host:")) {
                    tmp = line.split(" ");
                    if (tmp[1].indexOf(':') > 0) {
                        String[] tmp2 = tmp[1].split(":");
                        host = tmp2[0];
                        port = Integer.parseInt(tmp2[1]);
                    } else {
                        host = tmp[1];
                        port = HTTP_PORT;
                    }
                }
                line = from.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading from socket: " + e);
            return;
        }
        System.out.println("Host to contact is: " + host + " at port " + port);
    }

    /** Return host for which this request is intended */
    public String getHost() {
        return host;
    }

    /** Return port for server */
    public int getPort() {
        return port;
    }

    /**
     * Convert request into a string for easy re-sending.
     */
    public String toString() {
        String req = "";

        req = method + " " + URI + " " + version + CRLF;
        req += headers;
        /* This proxy does not support persistent connections */
        req += "Connection: close" + CRLF;
        req += CRLF;

        return req;
    }
}
