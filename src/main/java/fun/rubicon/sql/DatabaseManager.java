package fun.rubicon.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class DatabaseManager {

    private List<DatabaseGenerator> generatorList = new ArrayList<>();

    public void addGenerator(DatabaseGenerator generator) {
        generatorList.add(generator);
    }

    public void addGenerators(DatabaseGenerator... generators) {
        generatorList.addAll(Arrays.asList(generators));
    }

    public void generate() {
        for (DatabaseGenerator generator : generatorList)
            generator.createTableIfNotExist();
    }
}
