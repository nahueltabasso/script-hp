package script.hp;

import script.hp.data.Script;
import script.hp.models.dto.LocalidadResponseDTO;
import script.hp.models.dto.ProvinciaResponseDTO;
import script.hp.models.entity.Provincia;
import script.hp.requests.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {

    private static Script script = new Script();
    private static int errores = 0;

    public static void main(String[] args) {
        ejecutarScript();
    }

    private static void ejecutarScript() {

        try {
            // Primero guardamos las provincias obteniendo la informacion de la api
            System.out.println("Obtenemos las provincias");
            List<ProvinciaResponseDTO> provincias = Request.obtenerProvinciasArgentinaDesdeApiGob();
            // Ordenamos la lista
            Collections.sort(provincias, Comparator.comparing(ProvinciaResponseDTO::getNombre));

            for (ProvinciaResponseDTO provinciaDTO: provincias) {
                // Entidad a persistir en la base de MySQL
                Provincia provinciaEntity = new Provincia();
                provinciaEntity.setNombre(provinciaDTO.getNombre());
                provinciaEntity = script.insertarRegistroTablaProvincia(provinciaEntity);
                System.out.println("Provincia persistida: " + provinciaEntity.getNombre() + ", ID: " + provinciaEntity.getId());

                // Persistimos ahora las localidades correspondientes a la provincia
                obtenerYGuardarLocalidadesSegunProvincia(provinciaEntity);
            }

            // Ahora insertamos los tipos de persona
            List<String> roles = new ArrayList<>();
            roles.add("ROLE_USER");
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_SUPERADMIN");
            System.out.println("Insertamos los roles: " + roles.toString());

            boolean cantidadRolesInsertados = script.insertarMultiplesRegistrosTablaTipoPersona(roles);

            if (!cantidadRolesInsertados) {
                throw new Exception("Ocurrio una error, no se insertaron la cantidad correcta de registros en la tabla de tipo_persona");
            }
        } catch (Exception e) {
            System.out.println("Ocurrio un error");
            errores++;
            e.printStackTrace();
        }

        if (errores == 0) {
            System.out.println("El proceso termino sin errores!");
        } else {
            System.out.println("Ocurrieron " + errores + " errores. Revisar los logs");
        }
    }

    private static void obtenerYGuardarLocalidadesSegunProvincia(Provincia provincia) {
        // Obtenemos las localidades de la provincia
        System.out.println("Obtener las localidades de la provincia: " + provincia.getNombre());

        String param = getQueryParamFormat(provincia.getNombre());
        List<LocalidadResponseDTO> localidades = Request.obtenerLocalidadesByProvinciaDesdesApiGob(param);
        Collections.sort(localidades, Comparator.comparing(LocalidadResponseDTO::getNombre));

        try {
            boolean cantidadRegistrosInsertados = script.insertarMultiplesRegistrosTablaLocalidad(localidades, provincia.getId());
            if (!cantidadRegistrosInsertados) {
                throw new Exception("Ocurrio una error, no se insertaron la cantidad correcta de registros en la tabla de localidades");
            }
        } catch (Exception e) {
            errores++;
            e.printStackTrace();
        }
    }

    private static String getQueryParamFormat(String nombre) {
        List<String> nombreSeparado = List.of(nombre.split(" "));

        String param = "";
        if (nombreSeparado.size() == 1) {
            param = nombre.toLowerCase();
            return param;
        }

        for (String p : nombreSeparado) {
            p = p.toLowerCase();
            param += p + "-";
        }

        param = param.substring(0, param.length() - 1);
        return param;
    }

}
