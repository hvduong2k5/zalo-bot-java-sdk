package io.github.hvduong2k5.zalobot.model.message;

import io.github.hvduong2k5.zalobot.util.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TextStyle {
    private final int start;
    private final int len;
    private final List<String> st;

    private TextStyle(Builder builder) {
        this.start = builder.start;
        this.len = builder.len;
        this.st = Collections.unmodifiableList(new ArrayList<>(builder.st));
    }

    public int getStart() { return start; }
    public int getLen() { return len; }
    public List<String> getSt() { return st; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int start = -1;
        private int len = -1;
        private final List<String> st = new ArrayList<>();

        private Builder() {}

        public Builder start(int start) {
            this.start = start;
            return this;
        }

        public Builder len(int len) {
            this.len = len;
            return this;
        }

        public Builder st(List<String> st) {
            this.st.clear();
            if (st != null) {
                this.st.addAll(st);
            }
            return this;
        }

        public Builder st(String... stArgs) {
            this.st.clear();
            if (stArgs != null) {
                this.st.addAll(Arrays.asList(stArgs));
            }
            return this;
        }

        public TextStyle build() {
            Preconditions.checkArgument(start >= 0, "start must be >= 0");
            Preconditions.checkArgument(len > 0, "len must be > 0");
            Preconditions.checkArgument(!st.isEmpty(), "st (styles) cannot be empty");
            return new TextStyle(this);
        }
    }
}
