package tech.bedev.discordsrvutils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import tech.bedev.discordsrvutils.Person.Person;

public final class PlayerLevelupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Person person;
    private Player player;

    public PlayerLevelupEvent(Person person, Player player) {
        this.person = person;
        this.player = player;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Person getPerson() {
        return person;

    }

    public Player getPlayer() {
        return player;
    }
}
