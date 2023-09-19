package catbot;

import catbot.bot.CatBot;
import catbot.io.CatBotJavaFxIo;
import catbot.io.UserIo;
import catbot.task.TaskList;

public class CatBotEntrypoint {

    public static void main(String[] args) {
        CatBot catBot = new CatBot(new TaskList("Tasks.txt"));
        UserIo userIo = new CatBotJavaFxIo();
        userIo.initialize();
        catBot.initialize(userIo);
        userIo.takeoverExecutionLogic(catBot);
    }

}