package tk.bluetree242.discordsrvutils.listeners.punishments.advancedban;

import tk.bluetree242.discordsrvutils.interfaces.Punishment;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class AdvancedBanPunishment implements Punishment {
    private final me.leoko.advancedban.utils.Punishment punishment;
    public AdvancedBanPunishment(me.leoko.advancedban.utils.Punishment punishment) {
        this.punishment = punishment;
    }
    @Override
    public String getDuration() {
        if (punishment.getEnd() == -1)
        return "Permanent";
        return Utils.getDuration((punishment.getEnd() - punishment.getStart()) +1);
    }

    @Override
    public String getOperator() {
        return punishment.getOperator();
    }

    @Override
    public String getName() {
        return punishment.getName();
    }

    @Override
    public String getReason() {
        return punishment.getReason();
    }
}
