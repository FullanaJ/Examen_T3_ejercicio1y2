import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.net.URISyntaxException;

public class Ejercicio1 {

    /**
     * Ejercicio 1
     * @return
     */
    public static void main(String[] args) throws URISyntaxException {


        ObservableMap<String, String> map = FXCollections.observableHashMap();
        map.addListener(new DownloaderAndZipper());
        int longitud = 20; // Longitud de la cadena aleatoria
        String url;
        while (true) {
            url = Ejercicio2.asksForURL();
            if (url.equals(""))
                break;
            map.put(RandomStringGenerator.generarCadenaAleatoria(longitud),url);
        }
        System.out.println("Se va a proceder a descargar y comprimir los ficheros");
    }

}
