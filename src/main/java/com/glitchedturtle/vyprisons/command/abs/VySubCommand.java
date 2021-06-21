package com.glitchedturtle.vyprisons.command.abs;

public class VySubCommand {

    private String _name;
    private String _permissionNode;

    private String _usage;
    private String _description;

    public VySubCommand(String name, String permissionNode, String usage, String description) {
        _name = name;
        _permissionNode = permissionNode;
        _usage = usage;
        _description = description;
    }

    public String getName() {
        return _name;
    }

    public String getPermissionNode() {
        return _permissionNode;
    }

    public String getUsage() {
        return _usage;
    }

    public String getDescription() {
        return _description;
    }

}
