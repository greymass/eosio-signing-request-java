package com.greymass.esr;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.io.BaseEncoding;
import com.greymass.esr.interfaces.IRequest;
import com.greymass.esr.models.Action;
import com.greymass.esr.models.ChainId;
import com.greymass.esr.models.Identity;
import com.greymass.esr.models.InfoPair;
import com.greymass.esr.models.LinkCreate;
import com.greymass.esr.models.RequestFlag;
import com.greymass.esr.models.SealedMessage;
import com.greymass.esr.models.Signature;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static com.greymass.esr.SigningRequest.PLACEHOLDER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EncodeDecodeTests extends  ESRTest {

    @Test
    public void encodeAndDecodeRequest() throws ESRException {
        SigningRequest signingRequest = makeSigningRequest();
        signingRequest.setRequest(makeTransferAction(PLACEHOLDER_NAME, PLACEHOLDER_NAME, PLACEHOLDER_NAME, FOO, "1. PENG", "Thanks for the fish"));
        signingRequest.getRequestFlag().setBackground(true);

        String encoded = signingRequest.encode();

        assertEquals("Encoded should match", "esr://gmNgZGBY1mTC_MoglIGBIVzX5uxZRqAQGMBoExgDAjRi4fwAVz93ICUckpGYl12skJZfpFCSkaqQllmcwczAAAA", encoded);

        SigningRequest signingRequest2 = makeSigningRequest();
        signingRequest2.load(encoded);

        assertEquals("ChainId should be EOS", signingRequest2.getChainId(), ChainId.EOS);
        IRequest request = signingRequest2.getRequest();
        assertTrue("Should get an Action type of request", (request instanceof Action));
        Action action = (Action) request;
        assertTransferActionPacked(action, PLACEHOLDER_NAME, PLACEHOLDER_NAME, "0100000000000000000000000000285D01000000000000000050454E47000000135468616E6B7320666F72207468652066697368");

        assertEquals("Callback should be empty", "", signingRequest2.getCallback());
        RequestFlag flag = signingRequest2.getRequestFlag();
        assertEquals("Flag should be 3", (byte) 3, flag.getFlagValue());
        assertTrue("Flag should be broadcast", flag.isBroadcast());
        assertTrue("Flag should be background", flag.isBackground());
        List<InfoPair> pairs = signingRequest2.getInfoPairs();
        assertTrue("Should be no info pairs", pairs.isEmpty());
    }

    @Test
    public void encodeAndDecodeSignedRequest() throws ESRException {
        SigningRequest signingRequest = makeSigningRequest();
        signingRequest.setRequest(makeTransferAction(FOO, ACTIVE, FOO, BAR, "1.000 EOS", "hello there"));
        signingRequest.sign(new MockSignatureProvider());
        assertEquals("Should get the proper signature digest hex", "1D8C7ED18A09E67356B287ACF092B9B231235E38F4C2DFFC4659CB004369FD1B", signingRequest.getSignatureDigestAsHex());
        Signature signature = signingRequest.getSignature();
        assertEquals("Should get the signature from the provider", MockSignatureProvider.MOCK_SIGNATURE, signature.getSignature());
        assertEquals("Signer should be " + FOO, FOO, signature.getSigner());
        String encoded = signingRequest.encode();
        assertEquals("Encoded should match", "esr://gmNgZGBY1mTC_MoglIGBIVzX5uxZoAgIaMSCyBVvjYx0kAUYGNZZvmCGsJhd_YNBNHdGak5OvkJJRmpRKlQ3WLl8anjWFNWd23XWfvzTcy_qmtRx5mtMXlkSC23ZXle6K_NJFJ4SVTb4O026Wb1G5Wx0u1A3-_G4rAPsBp78z9lN7nddAQA", encoded);
        signingRequest = makeSigningRequest();
        signingRequest.load(encoded);
        assertEquals("Should get the proper signature digest hex", "1D8C7ED18A09E67356B287ACF092B9B231235E38F4C2DFFC4659CB004369FD1B", signingRequest.getSignatureDigestAsHex());
        signature = signingRequest.getSignature();
        assertEquals("Should get the signature from the provider", MockSignatureProvider.MOCK_SIGNATURE, signature.getSignature());
        assertEquals("Signer should be " + FOO, FOO, signature.getSigner());
    }

    @Test
    public void encodeAndDecodeTestRequests() throws ESRException {
        String req1uri = "esr://gmNgZGBY1mTC_MoglIGBIVzX5uxZRqAQGMBoExgDAjRi4fwAVz93ICUckpGYl12skJZfpFCSkaqQllmcwczAAAA";
        String req2uri ="esr://gmNgZGBY1mTC_MoglIGBIVzX5uxZRqAQGMBoExgDAjRi4fwAVz93ICUckpGYl12skJZfpFCSkaqQllmcwQxREVOsEcsgX-9-jqsy1EhNQM_GM_FkQMIziUU1VU4PsmOn_3r5hUMumeN3PXvdSuWMm1o9u6-FmCwtPvR0haqt12fNKtlWzTuiNwA";

        SigningRequest req1 = makeSigningRequest();
        req1.load(req1uri);

        SigningRequest req2 = makeSigningRequest();
        req2.load(req2uri);

        List<Action> actions1 = req1.resolveActions();
        List<Action> actions2 = req2.resolveActions();
        assertTransferActionUnpacked(actions1.get(0), PLACEHOLDER_NAME, PLACEHOLDER_NAME, PLACEHOLDER_NAME, FOO, "1 PENG", "Thanks for the fish");
        assertTransferActionUnpacked(actions2.get(0), PLACEHOLDER_NAME, PLACEHOLDER_NAME, PLACEHOLDER_NAME, FOO, "1 PENG", "Thanks for the fish");

        assertFalse("Reqest 1 should not have a signature", req1.hasSignature());
        assertTrue("Request 2 should have a signature", req2.hasSignature());
        Signature signature = req2.getSignature();
        assertEquals("Should get the signature from the provider", "SIG_K1_KBub1qmdiPpWA2XKKEZEG3EfKJBf38GETHzbd4t3CBdWLgdvFRLCqbcUsBbbYga6jmxfdSFfodMdhMYraKLhEzjSCsiuMs", signature.getSignature());
        assertEquals("Signer should be " + FOO + BAR, FOO + BAR, signature.getSigner());
        assertEquals("Request 1 should encode back to original uri", req1uri, req1.encode());
        assertEquals("Request 2 should encode back to original uri", req2uri, req2.encode());
    }

    @Test
    public void shouldEncodeDecodeWithMetadata() throws ESRException {
        SigningRequest request = makeSigningRequest();
        request.setCallback(EXAMPLE_CALLBACK);
        request.setInfoKey(FOO, BAR);
        request.setInfoKey(BAZ, BaseEncoding.base16().encode(new byte[]{0x00, 0x01, 0x02}));
        request.setRequest(new Identity());

        SigningRequest requestDecoded = makeSigningRequest();
        requestDecoded.load(request.encode());
        Map<String, String> info = requestDecoded.getInfo();
        assertTrue("Should have 2 keys", info.size() == 2);
        assertTrue("Should have foo key", info.containsKey(FOO));
        assertTrue("Should have baz key", info.containsKey(BAZ));
        assertEquals("foo should be bar", BAR, info.get(FOO));
        assertEquals("baz should be 000102", "000102", info.get(BAZ));
    }

    @Test
    public void linkCreateDecoding() throws ESRException {
        SigningRequest request = makeSigningRequest();
        String encoded = "esr://AgFx7oO89SFC1hAZ2V-cxUJ7pqDX_4rM2eIIiuKr6vPT3QMAAjtodHRwczovL2NiLmFuY2hvci5saW5rLzMxNGFkNzQ3LTllMWItNGVjMi04ZjZiLTI1MzZlMjczZTM0NQIEbGluayoAAADUAQxpPAAD9zidhCTEZUZy8ES6ijkahh2shAWq8ufj_w2SlXm4SC8LcmVxX2FjY291bnQHYmxva3Npbw";
        request.load(encoded);
        InfoPair linkPair = null;
        List<InfoPair> pairs = request.getInfoPairs();

        for (InfoPair pair : pairs) {
            if ("link".equals(pair.getKey())) {
                linkPair = pair;
                break;
            }
        }

        assertNotNull("Should get an InfoPair with key of 'link'", linkPair);
        LinkCreate lc = request.decodeLinkCreate(linkPair.getHexValue());
        assertEquals(".k", lc.getSessionName());
        assertEquals("PUB_K1_2wkBNMznVX3MQgPrMnrMMfnsDuhJZK4puxXSTGsjnU7fBbGgcQ", lc.getRequestKey());
    }

    @Test
    public void sealedMessageDecoding() throws ESRException {
        String encodedSealedMessage = "0003a2aa86d264d49ddafce3f551c802923b04981a8afba47b5a00f34e1dabd5bf089bb691fb498e7501800265cd74ecd35b4cfca8889774bf6d60212ac27963706defd709aa65a737e28bc680f907cae4e6dec7c5dedd578f0f7223cb67bb4d6c0035db8c99ba33373141e67a9a7347ef88226901d5c052977c25b319de012a91257c5e29fa8282aeccf6fdd8985b0dd71ada0f64b1c226c8e255fe664c751b73af4e7137545f9b367477b3deca13c0da3aab45620f588e23fb67ec9eba0a879e4ff0bf2c6c9243e568d22a55393c2fecbe384f096916e66e642242d8f256ebfc50a3b62e407fdad56df9369190db20aa952035c3d007290895d03c9bf145af39e33fb528ce30f801300f39b120e797be8e4c0019c427d93b22c590d7e1d15e592c7d457755cf3b5fd1196e608b9463";
        SigningRequest request = makeSigningRequest();
        SealedMessage sealed = request.decodeSealedMessage(encodedSealedMessage);
        assertEquals("X", "X");
    }

    @Test
    public void cloneTest() throws ESRException {
        SigningRequest signingRequest = makeSigningRequest();
        signingRequest.setRequest(makeTransferAction(FOO, ACTIVE, FOO, BAR, "1.000 EOS", "hello there"));

        SigningRequest signingRequestCopy = signingRequest.copy();
        assertEquals("Copy should encode the same", signingRequest.encode(), signingRequestCopy.encode());
        signingRequestCopy.setInfoKey(FOO, true);
        assertNotEquals("After setting info key, copy should not encode the same", signingRequest.encode(), signingRequestCopy.encode());
    }

}
