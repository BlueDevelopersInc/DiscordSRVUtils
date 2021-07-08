package tk.bluetree242.discordsrvutils.exceptions;

public class StartupException extends RuntimeException{

    private Throwable cause;

    public StartupException(Throwable ex){
        this.cause = ex;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        return "Plugin error while starting";
    }
}
