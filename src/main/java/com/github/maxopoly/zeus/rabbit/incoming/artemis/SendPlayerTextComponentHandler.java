package com.github.maxopoly.zeus.rabbit.incoming.artemis;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.rabbit.DynamicRabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerTextComponent;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;

public class SendPlayerTextComponentHandler extends GenericInteractiveRabbitCommand {
    private enum FailureReason {
        PLAYER_DOES_NOT_EXIST;
    }

    @Override
    public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
        UUID sender = UUID.fromString(data.getString("sender"));
        UUID receiver = UUID.fromString(data.getString("receiver"));
        String message = data.getString("message");
        GlobalPlayerData receiverData = ZeusMain.getInstance().getPlayerManager().getOnlinePlayerData(receiver);
        Map<String, Object> replyParameter = new HashMap<>();
        if (receiverData == null) {
            replyParameter.put("success", false);
            replyParameter.put("reason", FailureReason.PLAYER_DOES_NOT_EXIST.toString());
            sendReply(sendingServer, new DynamicRabbitMessage(ticket, "art_ans_send_player_text_comp", replyParameter));
            return;
        } else {
            replyParameter.put("success", true);
        }
        sendReply(sendingServer, new DynamicRabbitMessage(ticket, "art_ans_send_player_text_comp", replyParameter));
        sendReply(receiverData.getMCServer(), new SendPlayerTextComponent(ticket, sender, receiver, message));
    }

    @Override
    public String getIdentifier() {
        return "art_req_send_player_text_comp";
    }
}
