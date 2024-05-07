package com.example.playlist.Clients;

import jakarta.xml.bind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import com.spotify.idmclient.wsdl.*;
import javax.xml.namespace.QName;
import java.math.BigInteger;


public class IDMClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(IDMClient.class);
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public GetUserByIdResponse getUserById(Integer id) {
        GetUserById request = OBJECT_FACTORY.createGetUserById();
        log.info("Requesting USER DETAILS  for user with ID: " + id);
//        JAXBElement<String> userName = new JAXBElement<>(
//                new QName("services.IDM.soap", "username"),
//                String.class,
//                String.valueOf(userId)
//        );
        JAXBElement<BigInteger> idJaxb = new JAXBElement<>(
                new QName("services.IDM.soap", "id"),
                BigInteger.class,
                BigInteger.valueOf(id)
        );
        request.setId(idJaxb);
        JAXBElement jaxbGetUserById = OBJECT_FACTORY.createGetUserById(request);
        JAXBElement<GetUserByIdResponse> response = (JAXBElement<GetUserByIdResponse>) getWebServiceTemplate()
                .marshalSendAndReceive("http://127.0.0.1:8000", jaxbGetUserById, null);

        GetUserByIdResponse userResponse = response.getValue();
        String result = userResponse.getGetUserByIdResult().getValue();
        log.info("Rezultat: " + result);

        return (GetUserByIdResponse) response.getValue();
    }

}
