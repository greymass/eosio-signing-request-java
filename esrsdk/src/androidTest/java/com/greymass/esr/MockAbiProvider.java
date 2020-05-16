package com.greymass.esr;

import com.greymass.esr.interfaces.IAbiProvider;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MockAbiProvider implements IAbiProvider {

    private ResourceReader gReader;

    public MockAbiProvider(ResourceReader reader) {
        gReader = reader;
    }

    @Override
    public String getAbi(String account) throws ESRException {
        if ("eosio.token".equals(account))
            return gReader.readResourceString(R.raw.tokenabi);

        throw new ESRException("Tests should only request eosio.token");
    }
    /*
    @Override
    public String getAbi(String account) {
        String url = "https://testnet.telos.caleos.io/v1/chain/get_abi";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.format("{\"account_name\": \"%s\"}", account));
        Request request = new Request.Builder().url(url).post(body).build();
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            return response.body().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
     */
}
