package com.minimalbosshealth;

import net.runelite.api.gameval.NpcID;

public final class BossRegistryFactory {
    
    private BossRegistryFactory() {}

    public static BossRegistry build() {
        return BossRegistry.builder()
            .register(
                BossModel.builder("Amoxliatl")
                    .ids(NpcID.AMOXLIATL)
                    .requiresInstance()
                    .build()
            )
            .register(
                BossModel.builder("Brutus")
                    .ids(NpcID.COWBOSS, NpcID.COWBOSS_HARDMODE)
                    .requiresInstance()
                    .build()
            )
            .build();
    }
    
}
