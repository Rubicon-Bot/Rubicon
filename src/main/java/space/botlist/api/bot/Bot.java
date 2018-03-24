package space.botlist.api.bot;

import lombok.Getter;

import java.util.List;

/**
 * @author Biosphere
 * @date 17.03.18
 */
@Getter
public class Bot {

    private boolean approved, featured, premium;
    private String avatar, discriminator, id, invite, library, longDesc, name, prefix, shortDesc, type;
    private int views;
    private long timestamp;
    private List<Owner> owners;
}
