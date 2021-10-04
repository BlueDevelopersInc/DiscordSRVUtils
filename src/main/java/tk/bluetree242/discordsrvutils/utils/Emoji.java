package tk.bluetree242.discordsrvutils.utils;

public class Emoji {


    private final String nameInText;
    private final String name;
    private final String nameInReaction;
    private final boolean emote;
    private Long id;
    private boolean animated;

    public Emoji(String unicode) {
        this.emote = false;
        this.nameInText = unicode;
        this.name = unicode;
        this.nameInReaction = unicode;
    }

    Emoji(long id, String name, boolean animated) {
        this.id = id;
        this.animated = animated;
        this.emote = true;
        this.nameInText = "<" + (animated ? "a" : "") + ":" + name + ":" + id + ">";
        this.name = name;
        this.nameInReaction = (animated ? "a:" : "") + name + ":" + id;
    }

    @Override
    public String toString() {
        return nameInText;
    }

    public String getNameInText() {
        return nameInText;
    }

    public String getName() {
        return name;
    }

    public String getNameInReaction() {
        return nameInReaction;
    }


}
