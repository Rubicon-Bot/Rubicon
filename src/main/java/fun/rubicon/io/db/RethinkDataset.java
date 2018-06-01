package fun.rubicon.io.db;

import fun.rubicon.io.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author ForYaSee / Yannick Seeger
 */
@RequiredArgsConstructor
public abstract class RethinkDataset {

    @Getter
    @Nonnull
    private final transient String table;

    public void saveData() {
        Data.db().save(this);
    }

    public void deleteData() {
        Data.db().delete(this);
    }

    public abstract String getId();
}
