package fun.rubicon.cluster.events;

import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.listener.Event;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author ForYaSee / Yannick Seeger
 */
@Data
public class ClusterClientConnectedEvent implements Event {

    private final ClusterClient client;
    private final OffsetDateTime timestamp;
}
