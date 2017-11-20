package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Rubicon Discord bot
 *
 * @author Moritz Jahn / ForMoJa
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.tools
 */

public class Brainfuck extends Command {

    private final static int MAX = 255;

    private Map<Integer, Integer> loopPointer = new HashMap<>();
    private Map<Integer, Map<Integer, Integer>> loopStart = new HashMap<>();
    private int loopIndex = 0;

    private Map<Integer, Integer> setCells() {
        Map<Integer, Integer> placeholder = new HashMap<>();
        placeholder.put(0, 0);
        placeholder.put(1, 0);
        placeholder.put(2, 0);
        placeholder.put(3, 0);
        placeholder.put(4, 0);
        placeholder.put(5, 0);
        placeholder.put(7, 0);
        placeholder.put(8, 0);
        placeholder.put(9, 0);
        placeholder.put(10, 0);
        placeholder.put(11, 0);
        placeholder.put(12, 0);
        placeholder.put(13, 0);
        placeholder.put(14, 0);
        placeholder.put(15, 0);
        placeholder.put(16, 0);
        placeholder.put(17, 0);
        placeholder.put(18, 0);
        placeholder.put(19, 0);
        placeholder.put(20, 0);
        placeholder.put(21, 0);
        placeholder.put(22, 0);
        placeholder.put(23, 0);
        placeholder.put(24, 0);
        placeholder.put(25, 0);
        placeholder.put(26, 0);
        return placeholder;
    }

    public Brainfuck(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Map<Integer, Integer> cells = setCells();
        int pointer = 0;
        if (args.length > 0) {
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                char[] charArray = args[i].toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    switch (charArray[j]) {
                        case '>':
                            pointer += 1;
                            if (pointer > 27) {
                                sendErrorMessage("Your pointer is higher than 27.");
                                return;
                            }
                            break;
                        case '<':
                            pointer -= 1;
                            if (pointer < 0) {
                                sendErrorMessage("Your pointer is less than 0.");
                                return;
                            }
                            break;
                        case '+':
                            cells.put(pointer, cells.get(pointer) + 1);
                            if (cells.get(pointer) > MAX)
                                cells.put(pointer, 0);
                            break;
                        case '-':
                            cells.put(pointer, cells.get(pointer) - 1);
                            if (cells.get(pointer) < 0)
                                cells.put(pointer, MAX);
                            break;
                        case '.':
                            output.append((char) ((int) cells.get(pointer)));
                            break;
                        case '[':
                            loopIndex += 1;
                            loopPointer.put(loopIndex, pointer);
                            Map<Integer, Integer> position = new HashMap<>();
                            position.put(i, j + 1);
                            loopStart.put(loopIndex, position);
                            break;
                        case ']':
                            for (Map.Entry<Integer, Integer> positionEntry : loopStart.get(loopIndex).entrySet()) {
                                if (cells.get(loopPointer.get(loopIndex)) != 0) {
                                    i = positionEntry.getKey();
                                    j = positionEntry.getValue();
                                } else
                                    loopIndex -= 1;
                            }
                            break;
                        case ',':
                            sendNotImplementedMessage();
                            return;
                    }
                }
            }
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(output.toString())
                    .setColor(Colors.COLOR_PRIMARY);
            e.getTextChannel().sendMessage(builder.build()).queue();
        } else
            sendUsageMessage();
    }

    @Override
    public String getDescription() {
        return "Compile brainfuck code and send the result.";
    }

    @Override
    public String getUsage() {
        return Info.BOT_DEFAULT_PREFIX + "brainfuck <code>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
