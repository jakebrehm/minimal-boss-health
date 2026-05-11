package com.minimalbosshealth;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class BossModel {

    private final String name;
    private final Set<Integer> ids;
    private final Set<String> tags;
    private final boolean instanceRequired;
    private final Set<Integer> validRegions;

    private BossModel(Builder builder) {
        this.name = builder.name;
        this.ids = Collections.unmodifiableSet(builder.ids);
        this.tags = Collections.unmodifiableSet(builder.tags);
        this.instanceRequired = builder.instanceRequired;
        this.validRegions = Collections.unmodifiableSet(builder.validRegions);
    }

    public boolean hasId(int id) { return ids.contains(id); }
    public boolean hasTag(String tag) { return tags.contains(tag); }

    @Override
    public String toString() {
        return "BossModel{name='" + name + "', ids=" + ids + ", tags=" + tags + "}";
    }

    public static Builder builder(String name) { return new Builder(name); }

    public static final class Builder {

        private final String name;
        private final Set<Integer> ids = new HashSet<>();
        private final Set<String> tags = new HashSet<>();
        private boolean instanceRequired = false;
        private final Set<Integer> validRegions = new HashSet<>();

        private Builder(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Boss name must not be blank");
            }
            this.name = name;
        }

        public Builder ids(int... npcIds) {
            for (int id : npcIds) ids.add(id);
            return this;
        }

        public Builder tags(String... tagValues) {
            tags.addAll(Arrays.asList(tagValues));
            return this;
        }

        public Builder requiresInstance() {
            this.instanceRequired = true;
            return this;
        }

        public Builder validRegions(int... regionIds) {
            for (int id: regionIds) validRegions.add(id);
            return this;
        }

        public BossModel build() { return new BossModel(this); }

    }

}
