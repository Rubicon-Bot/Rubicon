package fun.rubicon.cluster;

public interface ClusterClient {

    void write(String s);

    void close();
}
