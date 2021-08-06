package tk.bluetree242.discordsrvutils.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;

public class UnCheckedSQLException extends RuntimeException{

    private SQLException cause;
    public UnCheckedSQLException(SQLException e) {
        cause = e;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
