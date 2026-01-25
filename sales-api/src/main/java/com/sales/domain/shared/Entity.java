package com.sales.domain.shared;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Entity {
    protected Long id;
    protected String code;
    protected LocalDateTime createdAt;

    protected Entity() {
        this.createdAt = LocalDateTime.now();
    }

    protected Entity(Long id, String code, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(code, entity.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
