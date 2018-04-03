package fun.rubicon.core.music;

import java.net.URI;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class LavalinkNode {

    private final String name;
    private final URI uri;
    private final String password;

    public LavalinkNode(String name, URI uri, String password) {
        this.name = name;
        this.uri = uri;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public URI getUri() {
        return uri;
    }

    public String getPassword() {
        return password;
    }
}
