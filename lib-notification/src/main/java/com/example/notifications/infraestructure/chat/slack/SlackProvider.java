package com.example.notifications.infraestructure.chat.slack;

import com.example.notifications.application.port.out.chat.ChatGateway;
import com.example.notifications.application.port.out.chat.ChatGatewayResponse;
import com.example.notifications.application.port.out.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackProvider implements ChatGateway {

    private final SlackConfig config;

    public SlackProvider(SlackConfig config) {
        this.config = config;
    }
    @Override
    public ChatGatewayResponse send(ChatMessage message) {
        SlackRequest requestMessage = new SlackRequest(message.message(), message.destination());
        SlackResponse slackResponse = callApi(requestMessage);
        log.info("Slack response: {}", slackResponse);
        if(!slackResponse.success())
            return new ChatGatewayResponse(
                    null, "ERROR", "ocurrio un error al enviar el mensaje"
            );
        return new ChatGatewayResponse("MSG-"+System.currentTimeMillis(), "SENT", null);
    }

    protected SlackResponse callApi(SlackRequest request) {

        return new SlackResponse(
                true,"MESSAGE SENT TO " + request.channel(), null
        );
    }
}
