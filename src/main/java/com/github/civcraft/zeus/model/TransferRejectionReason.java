package com.github.civcraft.zeus.model;

/**
 * When an artemis/minecraft server requests a transfer of a player to another server, the transfer
 * may be rejected with any of these reasons
 *
 */
public enum TransferRejectionReason {

	INVALID_SOURCE, TARGET_REJECT, TARGET_DOWN, NO_TARGET_FOUND, PLAYER_LOGOFF;

}
