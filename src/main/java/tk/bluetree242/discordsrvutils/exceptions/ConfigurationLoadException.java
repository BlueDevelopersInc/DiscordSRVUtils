package tk.bluetree242.discordsrvutils.exceptions;

import space.arim.dazzleconf.error.ConfigFormatSyntaxException;

public class ConfigurationLoadException extends RuntimeException{

    private Throwable cause;
    private String confname;
    public ConfigurationLoadException(Throwable ex, String confname){
        this.cause = ex;
        this.confname = confname;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {

        return "Error Loading/Reloading the " + confname + "." + (cause instanceof ConfigFormatSyntaxException ? " Check the syntax at https://yaml-online-parser.appspot.com/" : " Please make sure all options have right types");
    }

    public String getConfigName() {
        return confname;
    }
}
