package fun.rubicon.listener;

import fun.rubicon.entities.Shard;
import lombok.Data;

/**
 * @author ForYaSee / Yannick Seeger
 */
@Data
public abstract class Event {

    private final Shard shard;

}
