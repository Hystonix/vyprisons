package com.glitchedturtle.vyprisons.schematic;

import com.glitchedturtle.common.region.Region;
import com.glitchedturtle.common.util.TAssert;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class SchematicType {

    private int _id;

    private File _schematicFile;
    private WeakReference<Clipboard> _schematicData = null;

    private String _name;
    private List<String> _description;

    private Material _icon;

    private Vector _spawnOffset;
    private Region _mineOffset;
    private Region _region;

    private String _permissionNode = null;

    SchematicType(int id,
                  File schematicFile,
                  String name,
                  List<String> description,
                  Material icon,
                  Vector spawnOffset,
                  Region mineOffset,
                  Region region) {

        _id = id;
        _schematicFile = schematicFile;
        _name = name;
        _description = description;
        _icon = icon;
        _spawnOffset = spawnOffset;
        _mineOffset = mineOffset;
        _region = region;

    }

    public int getIdentifier() {
        return _id;
    }

    public File getSchematicFile() {
        return _schematicFile;
    }

    public Clipboard loadSchematic() throws IOException {

        if(_schematicData != null) {

            Clipboard clipboard = _schematicData.get();
            if(clipboard != null)
                return clipboard;

        }

        ClipboardFormat format = ClipboardFormats.findByFile(_schematicFile);
        TAssert.assertTrue(format != null, "Format is null");

        try (ClipboardReader reader = format.getReader(new FileInputStream(_schematicFile))){

            Clipboard clipboard = reader.read();
            _schematicData = new WeakReference<>(clipboard);

            return clipboard;

        }

    }

    void setPermissionNode(String permissionNode) {
        _permissionNode = permissionNode;
    }

    public String getName() {
        return _name;
    }

    public List<String> getDescription() {
        return _description;
    }

    public Material getIcon() {
        return _icon;
    }

    public Vector getSpawnOffset() {
        return _spawnOffset;
    }

    public Region getMineOffset() {
        return _mineOffset;
    }

    public Region getRegionOffset() {
        return _region;
    }

    public boolean canAccess(Player ply) {

        if(_permissionNode != null)
            return ply.hasPermission(_permissionNode);

        return true;

    }

}
