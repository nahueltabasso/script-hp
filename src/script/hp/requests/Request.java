package script.hp.requests;

import com.google.gson.Gson;
import script.hp.models.dto.LocalidadResponseDTO;
import script.hp.models.dto.LocalidadResponseList;
import script.hp.models.dto.ProvinciaResponseDTO;
import script.hp.models.dto.ProvinciaResponseList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;

public class Request {

    private static final Logger logger = Logger.getLogger(String.valueOf(Request.class));
    private static final String URL_API_GOB = "https://apis.datos.gob.ar/georef/api";
    private static Gson gson = new Gson();

    public static List<ProvinciaResponseDTO> obtenerProvinciasArgentinaDesdeApiGob() {
        logger.info("Ingresa a obtenerProvinciasArgentinasDesdeApiGob");
        String urlWs = URL_API_GOB + "/provincias";
        logger.info("URL WS ----- " + urlWs + " -----");

        // Realizamos el request a la api rest
        String output = doRequestTipoGET(urlWs);
        ProvinciaResponseList provinciaResponseList = null;
        // Parsear al json a nuestro modelo
        provinciaResponseList = gson.fromJson(output, ProvinciaResponseList.class);
        return provinciaResponseList.getProvincias();
    }

    public static List<LocalidadResponseDTO> obtenerLocalidadesByProvinciaDesdesApiGob(String nombreProvincia) {
        logger.info("Ingresa a obtenerLocalidadesByProvinciaDesdeApiGob()");
        String urlWs = URL_API_GOB + "/departamentos?provincia=" + nombreProvincia + "&max=100";

        logger.info("URL WS ----- " + urlWs + " -----");

        // Realizamos el request a la api rest
        String output = doRequestTipoGET(urlWs);
        LocalidadResponseList localidadResponseList = null;
        // Parsear el json a nuestro modelo
        localidadResponseList = gson.fromJson(output, LocalidadResponseList.class);
        return localidadResponseList.getDepartamentos();
    }

    private static String doRequestTipoGET(String urlWs) {
        try {
            // Armamos el request al web service
            URL url = new URL(urlWs);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("charset", "utf-8");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setDoOutput(true);

            if (httpConnection.getResponseCode() != 200) {
                logger.info("Ocurrio un error!");
                throw new RuntimeException("Fallo el request: Error HTTP: " + httpConnection.getResponseCode());
            }

            InputStreamReader in = new InputStreamReader(httpConnection.getInputStream());
            BufferedReader br = new BufferedReader(in);

            String output = "";
            String jsonBodyString = "";
            while ((output = br.readLine()) != null) {
                logger.info("Obtenemos la respuesta del request");
                System.out.println(output);
                jsonBodyString = output;
            }
            // Cerramos la conexion http
            httpConnection.disconnect();
            return jsonBodyString;
        } catch (MalformedURLException e) {
            logger.info("Error: " + e.getMessage());
            e.printStackTrace();
            logger.info("Error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

