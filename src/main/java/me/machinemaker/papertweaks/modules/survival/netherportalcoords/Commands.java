/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2026 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.papertweaks.modules.survival.netherportalcoords;

import org.incendo.cloud.Command;
import com.google.inject.Inject;
import me.machinemaker.papertweaks.cloud.dispatchers.CommandDispatcher;
import me.machinemaker.papertweaks.cloud.dispatchers.PlayerCommandDispatcher;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@ModuleCommand.Info(value = "portalcoords", aliases = "pcoords", descriptionKey = "modules.nether-portal-coords.commands.root", miniMessage = true, infoOnRoot = false)
class Commands extends ModuleCommand {

    private final Config config;
    private final MessageService messageService;

    @Inject
    Commands(final Config config, final MessageService messageService) {
        this.config = config;
        this.messageService = messageService;
    }

    private static int coordinateScale(final World world) {
        return world.getEnvironment() == World.Environment.NETHER ? 8 : 1;
    }

    @Override
    protected void registerCommands() {
        final Command.Builder<CommandDispatcher> builder = this.player();

        this.register(builder
            .permission(this.modulePermission("vanillatweaks.netherportalcoords"))
            .handler(context -> {
                final Player player = PlayerCommandDispatcher.from(context);
                final Location loc = player.getLocation();
                if (this.config.overWorlds().contains(player.getWorld())) {
                    final int netherScale = this.config.netherWorlds().stream().findFirst().map(Commands::coordinateScale).orElse(8);
                    this.messageService.coordinatesMsg(context.sender(), new MessageService.CoordinatesComponent(loc, i -> i / netherScale), "Nether");
                } else if (this.config.netherWorlds().contains(player.getWorld())) {
                    final int netherScale = coordinateScale(player.getWorld());
                    this.messageService.coordinatesMsg(context.sender(), new MessageService.CoordinatesComponent(loc, i -> i * netherScale), "Overworld");
                } else {
                    this.messageService.invalidWorld(context.sender());
                }
            })
        );
    }
}
