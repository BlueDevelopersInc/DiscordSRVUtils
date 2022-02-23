package tk.bluetree242.discordsrvutils.platform;

public interface Debugger {

    String run() throws Exception;

    String run(String stacktrace) throws Exception;
}
