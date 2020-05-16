package com.greymass.esr.models;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;

import java.util.List;

public enum ChainId {
    UNKNOWN(0, null),
    EOS(1, "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906"),
    TELOS(2, "4667b205c6838ef70ff7988f6e8257e8be0e1284a2f59699054a018f743b1d11"),
    JUNGLE(3, "e70aaab8997e1dfce58fbfac80cbbb8fecec7b99cf982a9444273cbc64c41473"),
    KYLIN(4, "5fff1dae8dc8e2fc4d5b23b2c7665c97f9e9d8edf2b6485a86ba311c25639191"),
    WORBLI(5, "73647cde120091e0a4b85bced2f3cfdb3041e266cbbe95cee59b73235a1b3b6f"),
    BOS(6, "d5a3d18fbb3c084e3b1f3fa98c21014b5f3db536cc15d08f9f6479517c6a3d86"),
    MEETONE(7, "cfe6486a83bad4962f232d48003b1824ab5665c36778141034d75e57b956e422"),
    INSIGHTS(8, "b042025541e25a472bffde2d62edd457b7e70cee943412b1ea0f044f88591664"),
    BEOS(9, "b912d19a6abd2b1b05611ae5be473355d64d95aeff0c09bedc8c166cd6468fe4"),
    WAX(10, "1064487b3cd1a897ce03ae5b6a865651747e2e152090f99c1d19d44e01aea5a4"),
    WAXTESTNET(11, "f16b1833c747c43682f4386fca9cbb327929334a762755ebec17f6f23c9b8a12"),
    FIO(12, "21dcae42c0182200e93f954a074011f9048a7624c6fe81d3c9541a614a88bd1c"),
    FIOTESTNET(13, "b20901380af44ef59c5918439a1f9a41d83669020319a80574b804a5f95cbd7e"),
    TELOSTESTNET(14, "1eaa0824707c8c16bd25145493bf062aecddfeb56c736f6ba6397f3195f33c9f");

    public static final String CHAIN_ID = "chain_id";

    private String gChainId;
    private int gChainAlias;
    private static final String ALIAS_LABEL = "chain_alias";

    ChainId(int alias, String chainId) {
        gChainAlias = alias;
        gChainId = chainId;
    }

    public static ChainId fromChainId(String chainId) {
        if (chainId == null)
            return ChainId.UNKNOWN;

        for (ChainId id : ChainId.values())
            if (chainId.equals(id.getChainId()))
                return id;

         return ChainId.UNKNOWN;
    }

    public static ChainId fromChainAlias(int alias) {
        for (ChainId id : ChainId.values())
            if (alias == id.getChainAlias())
                return id;

        return ChainId.UNKNOWN;
    }

    public static ChainId fromVariant(JsonArray variant) {
        return ALIAS_LABEL.equals(variant.get(0).getAsString()) ?
                ChainId.fromChainAlias(variant.get(1).getAsInt()) :
                ChainId.fromChainId(variant.get(1).getAsString());
    }

    public List<Object> toVariant() {
        List<Object> variant = Lists.newArrayList();
        variant.add(ALIAS_LABEL);
        variant.add(gChainAlias);
        return variant;
    }

    public int getChainAlias() {
        return gChainAlias;
    }

    public String getChainId() {
        return gChainId;
    }

}
