package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

public class HttpClient {

    private final int statusCode;
    private final HashMap<String, String> headerFields = new HashMap<>();

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(host, port);

        socket.getOutputStream().write(
                ("GET " + requestTarget + " HTTP/1.1\r\n" +
                        "Connection: close\r\n" +
                        "Host: " + host + "\r\n" +
                        "\r\n").getBytes()
        );

        String statusLine = readLine(socket);
        this.statusCode = Integer.parseInt(statusLine.split(" ")[1]);

        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()){ // les headlines, inntil du kommer til en blank headerline
            int colonPos = headerLine.indexOf(':'); //hva er posisjonen til ":"
            String key = headerLine.substring(0, colonPos); // key er da headerLine, alt som er opptil ":"
            String value = headerLine.substring(colonPos+1).trim(); //value er alt som starter et hakk etter ":". skal ikke ha med ":". og fjerne (trim) widespace
            headerFields.put(key, value);
        }

    }

    private String readLine(Socket socket) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream in = socket.getInputStream();

        int c;
        while ((c = in.read()) != -1 && c != '\r') {
            result.append((char)c);
        }
        in.read(); //lese og kaste e ny karakter
        return result.toString();
    }

    public int getStatusCode() {
        return statusCode;
    }


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("httpbin.org", 80);

        socket.getOutputStream().write(
                ("GET /html HTTP/1.1\r\n" +
                        "Connection: close\r\r" +
                        "Host: httpbin.org\r\n" +
                        "\r\n").getBytes()
        );

        InputStream in = socket.getInputStream();

        int c;
        while ((c = in.read()) != -1) {
            System.out.print((char)c);
        }
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        return Integer.parseInt((getHeader("Content-Length"))); //tolker en tekst som en integer, teksten vi skal tolke er headerfielden
    }
}
