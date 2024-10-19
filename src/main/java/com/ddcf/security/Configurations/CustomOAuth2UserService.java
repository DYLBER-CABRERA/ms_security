
package com.ddcf.security.Configurations;

import com.ddcf.security.Models.User;
import com.ddcf.security.Repositories.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.ddcf.security.Services.JwtService;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service//marca como un servicio de Spring
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private JwtService theJWTService;//inicializa el jwtService que se usa para generar tokens JWT
    private final UserRepository theUserRepository;//inicializa el repositorio de usuarios


    //constructor de la clase de la cual devuelve theJWTService que es el manejador de los tokens
    public CustomOAuth2UserService(JwtService theJWTService, UserRepository theUserRepository) {
        this.theJWTService = theJWTService;
        this.theUserRepository = theUserRepository;
    }

    @Override//Sobrescritura
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Carga la información del usuario desde el proveedor de OAuth2
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extraer información del usuario
        String name = oAuth2User.getAttribute("name");//extrae el nombre del usuario
        String email = oAuth2User.getAttribute("email");//extrae el email del usuario
        String login = oAuth2User.getAttribute("login");//extrae el login del usuario

        // Si el email es null, obtenerlo desde la API de GitHub
        if (email == null) {//si el email es null
            //obtiene el token de acceso del objeto userRequest,luego llama
            //al metodo getTokenValue() para obtener el valor del token como una cadena string
            String token = userRequest.getAccessToken().getTokenValue();
            //crea un objeto RestTemplate que simplifica la realizacion
            // de solicitudes HTTP a servivios web RESTful
            RestTemplate restTemplate = new RestTemplate();

            String url = "https://api.github.com/user/emails";//DEFINE LA URL PARA OBTENER LOS EMAIL DEL USUARIO
            //Crea una intancia de HttpHeaders que representa encabezados HTTP en una solicitid o
            // respuesta HTTP la cual puedo añadir, modificar o eliminar encabezados
            HttpHeaders headers = new HttpHeaders();
            //añade un encabezado llamdao Authorization con el valor del encabezado es el token de acceso
            // precedido por la palabra "Bearer", que es el esquema de autenticación utilizado.
            headers.add("Authorization", "Bearer " + token);
            //crea un objeto HttpEntity que representa una solicitud o respuesta HTTP en un encabezado
            HttpEntity<String> entity = new HttpEntity<>(headers);
            //realiza una solicitud http a la URL especificada con el metodo GET y espera una respuesta que se
            // mapea a una lista de mapas y luego especifica eel tipo de respuesta esperada
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            //Obtiene el cuerpo de la respuesta HTTP, en i¿un listado de mapas y cada
            // mapa representa un correo electronico del usuario con sus atributos
            List<Map<String, Object>> emails = response.getBody();
            if (emails != null && !emails.isEmpty()) {//verifica si la lista no esta vacia
                // itera sobre cada mapa de la lista de correos electronicos
                for (Map<String, Object> emailMap : emails) {
                    //verifica si el correo actual es el principal y esta verificado
                    if (Boolean.TRUE.equals(emailMap.get("primary")) && Boolean.TRUE.equals(emailMap.get("verified"))) {
                        //Si el correo es primario y verificado, asigna el correo a la variable email y sale del bucle.
                        email = (String) emailMap.get("email");
                        break;
                    }
                }
            }
        }

        // Fallback al login si el email sigue siendo null
        if (email == null) {
            email = login + "@github.com";
        }
        User user = theUserRepository.getUserByEmail(email);//obtiene el usuario por email
        if (user == null) {//si el usuario no existe
            user = new User(name, email, null, null);//crea un nuevo usuario
            theUserRepository.save(user);//guarda el usuario
        }


        String id = user.get_id();//obtiene el id del usuario
        // Generar token JWT utilizando la información del usuario
        String token = theJWTService.generateToken(user);//genera el token JWT

        // Imprimir para verificar
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Login: " + login);
        System.out.println(token);   // Imprimir el token

        // Añadir token a los atributos del usuario OAuth2
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("token", token);
        attributes.put("id", id); // Añadir el ID a los atributos

        return new CustomOAuth2User(oAuth2User, attributes); // Retorna una nueva instancia de CustomOAuth2User con los atributos actualizados
    }
}