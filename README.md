# Zeus

## Transfer protocol

How the transfer of a player from one server to another works on a protocol level:

Initial situation: The player is on the server Alice, connected through a Bungee proxy and enters a server portal

1. The player receives a world change packet from Alice (sent into loading screen), is frozen (various packets blackholed) and all persistent data is saved and written out to Zeus. After that the source server (Alice) sends a "init_transfer" packet to Zeus containing the UUID and location of the player.

2. Zeus receives the "init_transfer" packet and determines which server the player should go to.
- If the source of the "init_transfer" packet is not a minecraft client server, Zeus sends a "reject_transfer" packet to Alice with "INVALID_SOURCE" as reason
- If no target server can be determined, Zeus sends a "reject_transfer" packet to Alice with "NO_TARGET_FOUND" as reason
- If a target server can be determined, but it is offline Zeus sends a "reject_transfer" packet to Alice with "TARGET_DOWN" as reason
- If a target server (Bob) can be determined and it is online, Zeus sends a "receive_player_request" packet to Bob containing the players UUID and their location

3. Bob receives the "receive_player_request" packet, determines whether he wants to accept the player and replies with a "reply_receive_player_request" packet, containing an explicit accept or reject for the player

4. Zeus receives the "reply_receive_player_request" packet.
- If no "reply_receive_player_request" packet is received within 10 seconds, Zeus sends a "reject_transfer" packet to Alice with "TARGET_TIMEOUT" as reason
- If the "reply_receive_player_request" packet is received and rejects the transfer, Zeus sends a "reject_transfer" packet to Alice with "TARGET_REJECT" as reason
- If the "reply_receive_player_request" packet is received and accepts the transfer, Zeus sends a "accept_transfer" packet to Alice

5. Alice receives the "accept_transfer" packet and aggregates any leftover temporary states regarding the player that should be passed on and sends them to Zeus in a "transfer_player_data" packet.

6. Zeus receives the "transfer_player_data" packet and forwards it to Bob.

7. Bob receives the "transfer_player_data" packet, loads all additional data regarding the player it needs from Zeus and sends a "ready_player_transfer" packet to Zeus once it is fully ready to accept the player

8. Zeus receives the "ready_player_transfer" packet and sends a "transfer_player_bungee" containing the players UUID and Bobs name to all bungee servers.

9. The responsible bungee server switches the players connection to Bob

10. The player joins Bob
