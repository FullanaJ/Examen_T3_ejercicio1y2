import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.collections.*;


public class main {

    /**
     * Ejercicio 1
     * @return
     */
//    public static void main(String[] args) throws URISyntaxException {
//
//
//        ObservableMap<String, String> map = FXCollections.observableHashMap();
//        map.addListener(new DownloaderAndZipper());
//        int longitud = 20; // Longitud de la cadena aleatoria
//        String url;
//        while (true) {
//            url = asksForURL();
//            if (url.equals(""))
//                break;
//            map.put(RandomStringGenerator.generarCadenaAleatoria(longitud),url);
//        }
//        System.out.println("Se va a proceder a descargar y comprimir los ficheros");
//    }

    /**
     * Ejercicio 2
     * @param args
     */
    public static void main(String[] args) {
        // Se crea un mapa observable
        ObservableMap<String, String> map = FXCollections.observableHashMap();
        // Se añade un listener al mapa
        map.addListener(new DownloaderAndZipper());
        int longitud = 20; // Longitud de la cadena aleatoria
        String url;
        // Se pide una URL por consola
        while (true) {
            url = asksForURL();
            // Si la URL esta vacia se sale del bucle
            if (url.isEmpty())
                break;
            // Se añade la URL al mapa con una cadena aleatoria
            map.put(RandomStringGenerator.generarCadenaAleatoria(longitud),url);
        }
        // Se descargan las URL y se comprimen
        descargaYcomprime(map);

    }

    /**
     * Descarga las URL del mapa y las comprime
     * @param map
     */
    private static void descargaYcomprime(ObservableMap<String, String> map) {
        CompletableFuture.supplyAsync(
                () ->{
                    map.forEach(main::descargaURL);
                    return null;
                }
        ).whenComplete(
                (response, throwable) -> {
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream("dirCompressed.zip");
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    ZipOutputStream zipOut = new ZipOutputStream(fos);
                    File fileToZip = new File(Path.of("").resolve("paginas").toString());
                    try {
                        zipFile(fileToZip, fileToZip.getName(), zipOut);
                        zipOut.close();
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).join();
    }

    private static String asksForURL() {
        System.out.println("Introduce la URL: ");
        Scanner scanner = new Scanner(System.in);
        return Objects.requireNonNull(scanner.nextLine(), "");
    }

    /**
     * Comprime un archivo o directorio
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @throws IOException
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    /**
     * Descarga asyncronamente la url pasada y la gurada con el conmbre pasado
     * @param code
     * @param url
     */
    public static void descargaURL(String code, String url){

        Path path = Path.of("").toAbsolutePath().resolve("paginas").resolve(code);
        CompletableFuture<HttpResponse<String>> completableFuture = CompletableFuture.supplyAsync(
                () ->{
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET() // GET is default
                            .build();
                    try {
                        return client.send(request, HttpResponse.BodyHandlers.ofString());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).thenApply(
                (response) -> {
                    System.out.println("Status code: " + response.statusCode());
                    System.out.println("Headers: " + response.headers());
                    return response;
                }
        ).thenApply(
                (response) -> {
                    if (!Files.exists(path)) {
                        try {
                            Files.createFile(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        Files.writeString(path, response.body());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }

        );
        completableFuture.join();
    }
}
