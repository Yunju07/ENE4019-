import java.io.DataInputStream;
import java.io.IOException;

public class HttpResponse {
    final static String CRLF = "\r\n";
    /** How big is the buffer used for reading the object */
    final static int BUF_SIZE = 8192;
    /** Maximum size of objects that this proxy can handle. */
    final static int MAX_OBJECT_SIZE = 100000;
    /** Reply status and headers */
    String version;
    int status;
    String statusLine = "";
    String headers = "";
    /* Body of reply */
    byte[] body = new byte[MAX_OBJECT_SIZE];

    /** Read response from server. */
    public HttpResponse(DataInputStream fromServer) {
        /* Length of the object */
        int length = -1;
        boolean gotStatusLine = false;

        /* First read status line and response headers */
        try {
            String line = fromServer.readLine(); // 상태 줄 읽기
            while (line != null && line.length() != 0) { // 빈 줄까지 읽기
                if (!gotStatusLine) {
                    statusLine = line;
                    gotStatusLine = true;
                } else {
                    headers += line + CRLF;
                }

                /* Content-Length 헤더를 찾기 */
                if (line.startsWith("Content-Length") || line.startsWith("Content-length")) {
                    String[] tmp = line.split(" ");
                    length = Integer.parseInt(tmp[1]);
                }
                line = fromServer.readLine(); // 다음 헤더 줄 읽기
            }
        } catch (IOException e) {
            System.out.println("Error reading headers from server: " + e);
            return;
        }

        try {
            int bytesRead = 0;
            byte buf[] = new byte[BUF_SIZE];
            boolean loop = false;

            /* Content-Length 헤더가 없으면 무한 루프를 설정 */
            if (length == -1) {
                loop = true;
            }

            /* 본문을 BUF_SIZE만큼 읽으면서 body에 복사 */
            while (bytesRead < length || loop) {
                int res = fromServer.read(buf); // 이진 데이터로 읽기
                if (res == -1) { // 연결 종료 시 루프 종료
                    break;
                }
                /* body에 데이터 복사 (MAX_OBJECT_SIZE 초과하지 않도록 주의) */
                for (int i = 0; i < res && (i + bytesRead) < MAX_OBJECT_SIZE; i++) {
                    body[bytesRead + i] = buf[i];
                }
                bytesRead += res;
            }
        } catch (IOException e) {
            System.out.println("Error reading response body: " + e);
            return;
        }
    }

    /**
     * Convert response into a string for easy re-sending. Only
     * converts the response headers, body is not converted to a
     * string.
     */
    public String toString() {
        String res = "";

        res = statusLine + CRLF;
        res += headers;
        res += CRLF;

        return res;
    }
}
