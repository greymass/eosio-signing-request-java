package com.greymass.esr;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.greymass.esr.interfaces.IRequest;
import com.greymass.esr.models.Action;
import com.greymass.esr.models.ChainId;
import com.greymass.esr.models.InfoPair;
import com.greymass.esr.models.RequestFlag;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static com.greymass.esr.SigningRequest.PLACEHOLDER_NAME;
import static com.greymass.esr.SigningRequest.PLACEHOLDER_PERMISSION_LEVEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class URITests extends ESRTest {

    @Test
    public void fromURI() throws ESRException {
        SigningRequest signingRequest = makeSigningRequest();
        String requestUri = "esr://gmNgZGBY1mTC_MoglIGBIVzX5uxZRqAQGMBoExgDAjRi4fwAVz93ICUckpGYl12skJZfpFCSkaqQllmcwczAAAA";
        signingRequest.load(requestUri);

        assertEquals("ChainId should be EOS", signingRequest.getChainId(), ChainId.EOS);
        IRequest request = signingRequest.getRequest();
        assertTrue("Should get an Action type of request", (request instanceof Action));
        Action action = (Action) request;
        assertEquals("Account should be eosio.token", "eosio.token", action.getAccount().getName());
        assertEquals("Name should be transfer", "transfer", action.getName().getName());
        assertEquals("Should be one permission", 1, action.getAuthorization().size());
        assertEquals("Should be placeholder name for permission account name", PLACEHOLDER_NAME, action.getAuthorization().get(0).getAccountName().getName());
        assertEquals("Should be placeholder name for permission name", PLACEHOLDER_NAME, action.getAuthorization().get(0).getPermissionName().getName());
        assertTrue("Action data should be packed", action.getData().isPacked());
        assertEquals("Should be expected encoded action data", "0100000000000000000000000000285D01000000000000000050454E47000000135468616E6B7320666F72207468652066697368", action.getData().getPackedData());
        assertEquals("Callback should be empty", "", signingRequest.getCallback());
        RequestFlag flag = signingRequest.getRequestFlag();
        assertEquals("Flag should be 3", (byte) 3, flag.getFlagValue());
        assertTrue("Flag should be broadcast", flag.isBroadcast());
        assertTrue("Flag should be background", flag.isBackground());
        List<InfoPair> pairs = signingRequest.getInfoPairs();
        assertTrue("Should be no info pairs", pairs.isEmpty());
    }

    @Test
    public void fromActionsURI() throws ESRException {
        SigningRequest signingRequest = makeSigningRequest();
        String requestUri = "esr://gmMsfNe856ui0zUByZvxc446VS9bcP1_15mbjzi6Hq1-9fnyXUZGhmVNJsyvDEIZGBjCdW3OnmUEMhganCdWgugVb42MLJAFfnBsPOkwhwEMWCICgkC0eGR-aZFCUWpyamZBiUJafpFCSGJxdk4lY1pGSUlBsZW-fnliTk5qiW5OZl62bkpqbr5eWmZRalJicWpiQYFecn6ufmJycn5pXol9Up5tdXVSXm2tWnEikFWcCGSVVABZJRUgscwUkGBmCpBdlFoIZAPJ2lomdqh2thKwvcxAFSqmZsaJFqlGybpJRsmmuiaGyZa6iYYWSbpmiUamBmZJiSnJqaYA";
        signingRequest.load(requestUri);
        Map<String, String> abiMap = signingRequest.fetchAbis();
        List<Action> actions = signingRequest.resolveActions(abiMap, PLACEHOLDER_PERMISSION_LEVEL);
        Action action = actions.get(0);
        Map<String, Object> actionMap = action.toMap();

        throw new ESRException("Add assertions");
    }

    @Test
    public void fromIdentityURI() throws ESRException {
        String requestUri = "esr://gmMsfNe856ui0zUByZvxc446VS9bcP1_15mbjzi6Hq1-9fnyXWZGRgYEWPHWyIghIaOkpKDYSl-_PDEnJ7VENyczL1s3JTU3Xy8tsyg1KbE4NbGgQC85P9e-ODPdtroaSNbWqhUngpiJQFZRaiGQCSRBogUg0QIQKzMFrDaltpaZPTE5Ob80r4StJLE4O6eSGSiqYmpmnGiRapSsm2SUbKprYphsqZtoaJGka5ZoZGpglpSYkpxqylRUbAdzXGmxbnJqXklRYo6hLoZDk3PyS1PSSvOSSzLz84r18lJL9ItSE0tLMgA";
        SigningRequest signingRequest = makeSigningRequest();
        signingRequest.load(requestUri);
        Map<String, String> abiMap = signingRequest.fetchAbis();
        List<Action> actions = signingRequest.resolveActions(abiMap, PLACEHOLDER_PERMISSION_LEVEL);
        Action action = actions.get(0);
        Map<String, Object> actionMap = action.toMap();


        throw new ESRException("Add assertions");
    }

}
