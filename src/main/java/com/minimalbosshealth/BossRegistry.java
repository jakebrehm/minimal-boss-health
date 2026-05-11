package com.minimalbosshealth;

import lombok.Getter;

import java.util.*;

@Getter
public class BossRegistry {

    private final Map<Integer, BossModel> idMap;

    private final Map<String, Set<BossModel>> tagMap;

    private final Set<BossModel> all;

    private BossRegistry(Builder builder) {
        Map<Integer, BossModel> idLookup = new HashMap<>();
        Map<String, Set<BossModel>> tagLookup = new HashMap<>();

        for (BossModel def : builder.definitions) {
            for (int npcId : def.getIds()) {
                if (idLookup.containsKey(npcId)) {
                    throw new IllegalStateException(
                        "Duplicate NPC ID " + npcId + " found in both '" +
                        idLookup.get(npcId).getName() + "' and '" + def.getName() + "'"
                    );
                }
                idLookup.put(npcId, def);
            }

            for (String t : def.getTags()) {
                tagLookup.computeIfAbsent(t, k -> new HashSet<>()).add(def);
            }
        }

        this.idMap = Collections.unmodifiableMap(idLookup);
        this.tagMap = Collections.unmodifiableMap(tagLookup);
        this.all = Set.copyOf(builder.definitions);
    }

    public boolean isBoss(int npcId) {
        return idMap.containsKey(npcId);
    }

    public Optional<BossModel> getBoss(int npcId) {
        return Optional.ofNullable(idMap.get(npcId));
    }

    public Set<BossModel> getByTag(String tag) {
        return tagMap.getOrDefault(tag, Collections.emptySet());
    }

    public boolean isBossWithTag(int npcId, String tag) {
        return getBoss(npcId).map(def -> def.hasTag(tag)).orElse(false);
    }

    public int size() { return all.size(); }

    // region Builder

    public static Builder builder() { return new Builder(); }

    public static final class Builder {

        private final List<BossModel> definitions = new ArrayList<>();

        public Builder register(BossModel definition) {
            if (definition == null) {
                throw new IllegalArgumentException(("BossModel must not be null"));
            }
            definitions.add(definition);
            return this;
        }

        public Builder registerAll(Collection<BossModel> allDefinitions) {
            allDefinitions.forEach(this::register);
            return this;
        }

        public BossRegistry build() { return new BossRegistry(this); }

    }

    // endregion

}
