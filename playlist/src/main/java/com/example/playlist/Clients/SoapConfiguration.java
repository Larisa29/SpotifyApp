package com.example.playlist.Clients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class SoapConfiguration {
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in pom.xml
        marshaller.setContextPath("com.spotify.idmclient.wsdl");
        return marshaller;
    }

    @Bean
    public IDMClient soapClient(Jaxb2Marshaller marshaller) {
        IDMClient client = new IDMClient();
        client.setDefaultUri("http://127.0.0.1:8000");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
