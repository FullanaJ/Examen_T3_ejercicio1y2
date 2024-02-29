import java.math.BigInteger;
import java.security.SecureRandom;

class RandomStringGenerator {

    /**
     * Genera una cadena aleatoria de longitud pasada
     *
     * @param longitud
     * @return
     */
    private static SecureRandom random = new SecureRandom();

    public static String generarCadenaAleatoria(int longitud) {
        return new BigInteger(130, random).toString(32).substring(0, longitud);
    }

}
