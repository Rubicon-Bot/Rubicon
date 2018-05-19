package fun.rubicon.cluster.events;

import fun.rubicon.listener.Event;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author ForYaSee / Yannick Seeger
 */
@Data
public class ClusterDisconnectedEvent implements Event {

    private final OffsetDateTime timestamp;
}
