package fun.rubicon.util;

public class Bitly {


    private String BITLY_TOKEN;
    private String BITLY_API;

    public Bitly(String token){
        this.BITLY_TOKEN = token;
        this.BITLY_API = "https://api-ssl.bitly.com/v3/shorten?access_token=" + this.BITLY_TOKEN +"&longUrl=LONG_URL";

    }
}
