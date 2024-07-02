package com.example.stage1Hng.Controller;

import ch.qos.logback.core.net.server.Client;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TaskController {

    @Value("${openWeather.key}")
    private String weatherKey;

//    The Controller
    @GetMapping("/api/hello")
    public Map<String, String> helloController(@RequestParam("visitor_name") String name, HttpServletRequest request){
        String clientIp = getClientIp(request);
        String clientCity = getClientCity("197.210.79.240");
        String getWeather = getWeather(clientCity);
        Map<String, String> finalResponse = new HashMap<>();

        finalResponse.put("greetings", " Hello "+ name+"!, the temperature is "+ getWeather+ " degree Celsius in" + clientCity + "");
        finalResponse.put("Location", clientCity);
        finalResponse.put("Client Ip", clientIp);
        return finalResponse;


    }



//Getting Client IpAddress method
    private String getClientIp(HttpServletRequest request){
        String clientIp;
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor == null){
            clientIp= request.getRemoteAddr();
        }else {
            clientIp = xForwardedFor.split(",")[0];
        }

        return clientIp;
    }

//    Getting client City with fetched Ip Address
    private String getClientCity (String ipAddress){
        String url = "http://ip-api.com/json/"+ipAddress;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, HashMap.class);
            return response.get("city").toString();

    }
    private String getWeather(String location){
        String getWeatherKey = weatherKey;
        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q="+location+ "&appid="+ getWeatherKey;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> forObject = restTemplate.getForObject(weatherUrl, HashMap.class);
        Map<String, Object> main = (Map<String, Object>) forObject.get("main");
//        return forObject.toString();
        return main.get("temp").toString();

    }
}
