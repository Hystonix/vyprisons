package com.glitchedturtle.vyprisons.player.mine;

public enum MineAccessLevel {

    PUBLIC("vyprison.privacy.public"),
    GANG("vyprison.privacy.gang"),
    PRIVATE("vyprison.privacy.private");

    private String _permissionNode;

    MineAccessLevel(String permission) {
        _permissionNode = permission;
    }

    public String getPermissionNode() {
        return _permissionNode;
    }

}
