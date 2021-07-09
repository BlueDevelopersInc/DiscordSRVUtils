package tk.bluetree242.discordsrvutils.exceptions;

import github.scarsz.discordsrv.dependencies.jda.api.exceptions.RateLimitedException;

public class UncatchedRateLimitedException extends RuntimeException{

    private final RateLimitedException cause;

    public UncatchedRateLimitedException(RateLimitedException ex) {
        cause = ex;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
