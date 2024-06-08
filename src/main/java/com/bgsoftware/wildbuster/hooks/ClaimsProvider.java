package com.bgsoftware.wildbuster.hooks;

public interface ClaimsProvider {

    Type getType();

    enum Type {

        CHUNK_CLAIM,
        BLOCK_CLAIM

    }

}
