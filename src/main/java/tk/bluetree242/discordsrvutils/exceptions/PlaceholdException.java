package tk.bluetree242.discordsrvutils.exceptions;

public class PlaceholdException extends RuntimeException{


    private Throwable cause;

    public PlaceholdException(Throwable ex){
        this.cause = ex;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        return "Error while applying placeholders";
    }
}
