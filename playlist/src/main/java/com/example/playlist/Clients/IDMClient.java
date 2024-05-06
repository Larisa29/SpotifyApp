package com.example.playlist.Clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import com.spotify.idmclient.wsdl.*;
import org.springframework.ws.soap.client.core.SoapActionCallback;


public class IDMClient extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(IDMClient.class);

    public GetUserByIdResponse getUser(Integer user) {
        GetUserById request = new GetUserById();
        log.info("Requesting username for " + user);
//        request.setId(user);

        GetUserByIdResponse response = (GetUserByIdResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://127.0.0.1:8000", request, null);

        return response;
    }

}
