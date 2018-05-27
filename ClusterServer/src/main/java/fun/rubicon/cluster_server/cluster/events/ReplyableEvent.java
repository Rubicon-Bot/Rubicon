package fun.rubicon.cluster_server.cluster.events;

public interface ReplyableEvent {

    void reply(String invoke, String message);
}
