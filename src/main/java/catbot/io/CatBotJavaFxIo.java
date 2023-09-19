package catbot.io;

import catbot.bot.Bot;
import catbot.internal.NamedParameterMap;
import catbot.task.Task;
import catbot.task.TaskList;
import javafx.application.Application;

public class CatBotJavaFxIo implements UserIo {

    //region Constants

    public static final String NAME =
            " ________   ________   _________        ________   ________   _________   \n"
            + "|\\   ____\\ |\\   __  \\ |\\___   ___\\     |\\   __  \\ |\\   __  \\ |\\___   ___\\ \n"
            + "\\ \\  \\___| \\ \\  \\|\\  \\\\|___ \\  \\_|     \\ \\  \\|\\ /_\\ \\  \\|\\  \\\\|___ \\  \\_| \n"
            + " \\ \\  \\     \\ \\   __  \\    \\ \\  \\       \\ \\   __  \\\\ \\  \\\\\\  \\    \\ \\  \\  \n"
            + "  \\ \\  \\____ \\ \\  \\ \\  \\    \\ \\  \\       \\ \\  \\|\\  \\\\ \\  \\\\\\  \\    \\ \\  \\ \n"
            + "   \\ \\_______\\\\ \\__\\ \\__\\    \\ \\__\\       \\ \\_______\\\\ \\_______\\    \\ \\__\\\n"
            + "    \\|_______| \\|__|\\|__|     \\|__|        \\|_______| \\|_______|     \\|__|\n";

    //endregion

    //region Fields

    private CatbotJavaFxController controller;
    private volatile boolean isStillOpen = true;
    private Bot bot;
    static CatBotJavaFxIo lastApplicationLaunchPoint;

    //endregion

    //region Constructors

    public CatBotJavaFxIo() {

    }

    //endregion

    //region UserIo

    @Override
    public void initialize() {
        //initialization impossible without FXML; handled in takeoverExecutionLogic() instead
    }

    void initializeAfterFxml() {
        controller = CatBotJavaFxApplication.getLastCreatedController();
        send("Hiya! I'm\n" + NAME + "\n");
        controller.attachConsumerForParsedCommands(bot::run);
        controller.sendAssistantDialogue();
    }

    @Override
    public void cleanup() {
        send("Bye!");
        this.isStillOpen = false;
    }

    @Override
    public boolean isStillOpen() {
        return this.isStillOpen;
    }

    @Override
    public void takeoverExecutionLogic(Bot bot) {
        this.bot = bot;

        lastApplicationLaunchPoint = this;
        Application.launch(CatBotJavaFxApplication.class, "");
    }

    //endregion

    //region ErrorIndicatorIo

    @Override
    public void indicateInvalidCommand(String attemptedCommand) {
        warn("idgi ;-;");
    }

    @Override
    public void indicateInvalidInteger(String attemptedInteger) {
        warn("that doesn't look like a number... number pls");
    }

    @Override
    public void indicateInvalidIndex(int attemptedIndex, TaskList.Bounds bounds) {
        warn("i expected a number from " + bounds.lowerBound + " to " + bounds.upperBound + "...");
    }

    @Override
    public void indicateArgumentInvalid(InvalidArgumentState invalidState, NamedParameterMap namedParameterMap) {
        switch (invalidState) {
        case PARAMETER_EMPTY:
            for (String arg : namedParameterMap.keySet()) {
                warn(arg + " is empty");
            }
            send("please make sure these arguments are filled!");
            break;
        case PARAMETER_MISSING:
            for (String arg : namedParameterMap.keySet()) {
                warn(arg + " is missing");
            }
            send("please make sure to include them next time!");
            break;
        case NOT_A_DATE:
            for (String arg : namedParameterMap.keySet()) {
                warn(arg + " is set to \"" + namedParameterMap.get(arg) + "\", which is not a date!");
            }
            break;
        default:
            throw new RuntimeException();
        }
    }

    //endregion

    //region TaskAssistantIo

    @Override
    public void displayTaskList(TaskList taskList) {
        int i = 1;
        int intlen = 0;
        for (int len = taskList.size(); len > 0; intlen++) {
            len /= 10;
        }
        for (Task t : taskList.getTasks()) {
            send(String.format("%" + intlen + "d", i++) + ". " + t);
        }
    }

    @Override
    public void displayTaskAdded(TaskList taskList) {
        int index = taskList.size() - 1;
        send("Added: " + (index + 1) + ". " + taskList.getTask(index));
    }

    @Override
    public void displayTaskDeleted(Task deleted) {
        send("Deleted: " + deleted);
    }

    @Override
    public void displayTaskModified(TaskList taskList, int index) {
        send((index + 1) + ". " + taskList.getTask(index));
    }

    //endregion

    //region Internal Helper

    private void send(String s) {
        controller.queueAssistantDialogue(s);
    }

    private void warn(String s) {
        send("! " + s);
    }

    //endregion
}