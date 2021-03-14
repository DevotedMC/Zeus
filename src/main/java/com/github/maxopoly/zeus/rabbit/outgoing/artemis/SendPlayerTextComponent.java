package com.github.maxopoly.zeus.rabbit.outgoing.artemis;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import java.util.UUID;
import org.json.JSONObject;

public class SendPlayerTextComponent extends RabbitMessage {

    private UUID sender;
    private UUID receiver;
    private String message;

    public SendPlayerTextComponent(String transactionID, UUID sender, UUID receiver, String message) {
        super(transactionID);
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    protected void enrichJson(JSONObject json) {
        json.put("sender", sender.toString());
        json.put("receiver", receiver.toString());
        json.put("message", message);
    }

    @Override
    public String getIdentifier() {
        return "art_send_player_text_comp";
    }
}
