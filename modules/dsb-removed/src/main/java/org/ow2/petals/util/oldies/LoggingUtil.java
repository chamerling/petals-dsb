package org.ow2.petals.util.oldies;


import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

/**
 * Monolog logger implementation. This class is used to format various logs. it
 * can be used as a static class or as a wrapper object of a logger. The methods
 * add to the resulting message a "ClassName-MethodName" information. (only for
 * DEBUG and INFO level)
 * 
 * TODO : Rename to MonologLogger and move to kernel implementation
 * 
 * @author alouis
 * 
 */
public class LoggingUtil implements org.ow2.petals.kernel.api.log.Logger {

    protected Logger log;

    protected String name;

    /**
     * Creates a new instance of LoggingUtil
     * 
     * @param logger
     */
    public LoggingUtil(Logger logger) {
        this(logger, "");
    }
    
    public java.util.logging.Logger getLogger() {
        // CHA2012 : FIX for API change
        return java.util.logging.Logger.getLogger(log.getName());
    }

    /**
     * Creates a new instance of LoggingUtil
     * 
     * @param logger
     * @param loggerName
     */
    public LoggingUtil(Logger logger, String loggerName) {
        this.log = logger;

        if ((loggerName != null) && (loggerName.trim().length() > 0)) {
            loggerName = "[" + loggerName + "] ";
        } else {
            loggerName = "";
        }
        this.name = loggerName;
    }

    /**
     * {@inheritDoc}
     */
    public void call() {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.DEBUG)) {
            this.log.log(BasicLevel.LEVEL_DEBUG, this.name + "-CALL-" + classAndMethod());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void call(Object msg) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.DEBUG)) {
            this.log.log(BasicLevel.LEVEL_DEBUG, this.name + "-CALL-" + classAndMethod() + " " + msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.DEBUG)) {
            this.log.log(BasicLevel.LEVEL_DEBUG, this.name + "-START-" + classAndMethod());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start(Object msg) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.DEBUG)) {
            this.log.log(BasicLevel.LEVEL_DEBUG, this.name + "-START-" + classAndMethod() + " " + msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void end() {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.DEBUG)) {
            this.log.log(BasicLevel.LEVEL_DEBUG, this.name + "-END-" + classAndMethod());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void end(Object msg) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.DEBUG)) {
            this.log.log(BasicLevel.LEVEL_DEBUG, this.name + "-END-" + classAndMethod() + " " + msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void debug(Object message) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.DEBUG)) {
            this.log.log(BasicLevel.LEVEL_DEBUG, this.name + classAndMethod() + " " + message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void info(Object message) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.INFO)) {
            if (this.log.isLoggable(BasicLevel.DEBUG)) {
                this.log.log(BasicLevel.LEVEL_INFO, this.name + classAndMethod() + " " + message);
            } else {
                this.log.log(BasicLevel.LEVEL_INFO, this.name + message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void info(Object message, Throwable error) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.INFO)) {
            if (this.log.isLoggable(BasicLevel.DEBUG)) {
                this.log.log(BasicLevel.LEVEL_INFO, this.name + classAndMethod() + " " + message, error);
            } else {
                this.log.log(BasicLevel.LEVEL_INFO, this.name + message, error);
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    public void warning(Object message) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.WARN)) {
            this.log.log(BasicLevel.LEVEL_WARN, this.name + message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void warning(Object message, Throwable throwable) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.WARN)) {
            this.log.log(BasicLevel.LEVEL_WARN, this.name + message, throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void error(Object message) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.ERROR)) {
            this.log.log(BasicLevel.LEVEL_ERROR, this.name + message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void error(Object message, Throwable throwable) {
        if ((this.log != null) && this.log.isLoggable(BasicLevel.ERROR)) {
            this.log.log(BasicLevel.LEVEL_ERROR, this.name + message, throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDebugEnabled() {
        return this.isLevelEnabled(BasicLevel.DEBUG);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInfoEnabled() {
        return this.isLevelEnabled(BasicLevel.INFO);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWarnEnabled() {
        return this.isLevelEnabled(BasicLevel.WARN);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isErrorEnabled() {
        return this.isLevelEnabled(BasicLevel.ERROR);
    }

    private boolean isLevelEnabled(int level) {
        return (this.log != null) && this.log.isLoggable(level);
    }

    // //////////////////////////////////////////////////////////
    // Static part
    // //////////////////////////////////////////////////////////

    /**
     * Create an exception and analyse it in order to find the class and method
     * that called the LoggingUtil method.
     * 
     * @return
     */
    private static String classAndMethod() {
        String result = null;

        // throw and Parse an exception to find in the stack
        // the method that called the Loggingutil

        Throwable t = new Throwable();

        StackTraceElement[] ste = t.getStackTrace();

        if ((ste != null) && (ste.length > 2)) {
            StackTraceElement element = ste[2];

            // If the 2nd element in the stack is this class, get the 3rd
            if (element.getClassName().endsWith(LoggingUtil.class.getName())) {
                element = ste[3];
            }
            String className = element.getClassName();

            // remove the package name of the ClassName
            int index = className.lastIndexOf(".");

            if (index > -1) {
                className = className.substring(index + 1, className.length());
            }

            result = className + "." + element.getMethodName() + "()";
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return this.log.getName();
    }

}