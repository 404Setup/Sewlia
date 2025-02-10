package one.tranic.sewlia.permission;

import org.bukkit.permissions.PermissionDefault;

public enum Permissions {
    COMMAND_RELOAD("command.reload", "Reload Sewlia Config",PermissionDefault.OP),
    COMMAND_TPSBAR_SELF("command.tpsbar.self", "Show tpsbar for yourself", PermissionDefault.TRUE),
    COMMAND_TPSBAR_ANOTHER("command.tpsbar.another", "Show tpsbar to others", PermissionDefault.OP),
    COMMAND_RAMBAR_SELF("command.rambar.self", "Show rambar for yourself", PermissionDefault.TRUE),
    COMMAND_RAMBAR_ANOTHER("command.rambar.another", "Show rambar to others", PermissionDefault.OP),
    FEATURE_TPSLOCATION("feature.tpslocation", "Display player position when using tps command", PermissionDefault.OP);

    private final String permission;
    private final String description;
    private final PermissionDefault defaultValue;

    Permissions(String permission, String description, PermissionDefault defaultValue) {
        this.permission = permission;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String permission() {
        return permission;
    }

    public String description() {
        return description;
    }

    public PermissionDefault defaultValue() {
        return defaultValue;
    }
}