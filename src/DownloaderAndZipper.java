import javafx.collections.MapChangeListener;

/**
 * Clase que implementa la interfaz MapChangeListener para añadir un listener al mapa

 */
public class DownloaderAndZipper implements MapChangeListener<String, String> {

    @Override
    public void onChanged(Change<? extends String, ? extends String> change) {
        /**
         * Si se añade un elemento al mapa se imprime el valor añadido y la clave
         */
        if (change.wasAdded()) {
            String s = change.getKey();
            String u = change.getValueAdded();
            System.out.println(u + " enlocado como " + s);
        }
    }
}
