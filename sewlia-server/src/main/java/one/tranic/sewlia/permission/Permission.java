package one.tranic.sewlia.permission;

import org.bukkit.util.permissions.DefaultPermissions;
import org.jetbrains.annotations.NotNull;

public class Permission {
    private static final String ROOT = "sewlia";

    public static void registerCorePermissions() {
        org.bukkit.permissions.Permission parent = DefaultPermissions.registerPermission(
                ROOT,
                "Gives the user the ability to use all Sewlia utilities and commands"
        );

        for (Permissions perm : Permissions.values())
            setPermission(perm, parent);

        parent.recalculatePermissibles();
    }

    private static void setPermission(@NotNull Permissions perm, @NotNull org.bukkit.permissions.Permission parent) {
        DefaultPermissions.registerPermission(
                ROOT + "." + perm.permission(),
                perm.description(),
                perm.defaultValue(),
                parent
        );
    }
}