package tk.bluetree242.discordsrvutils.exceptions;

public class EmbedNotFoundException extends RuntimeException{

    private String msg;
    public EmbedNotFoundException(String embed) {
        this.msg = "Embed \"" + embed + "\" was not found";
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
