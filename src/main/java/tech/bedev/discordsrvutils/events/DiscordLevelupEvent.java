package tech.bedev.discordsrvutils.events;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import tech.bedev.discordsrvutils.Person.Person;

public class DiscordLevelupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Person person;
    private GuildMessageReceivedEvent getDiscordEvent;
    public DiscordLevelupEvent(GuildMessageReceivedEvent e, Person person) {
        this.person = person;
        this.getDiscordEvent = e;

    }
    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;

    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    public Person getPerson() {
        return person;
    }
    public GuildMessageReceivedEvent getDiscordEvent() {
        return getDiscordEvent;
    }
}
