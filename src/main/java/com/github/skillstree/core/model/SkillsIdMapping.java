package com.github.skillstree.core.model;

import java.util.UUID;

public class SkillsIdMapping {

    private UUID id;

    private String code;

    public SkillsIdMapping() {
    }

    public SkillsIdMapping(UUID id, String code) {
        this.id = id;
        this.code = code;
    }

    public UUID getId() {
        return id;
    }

    public SkillsIdMapping setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public SkillsIdMapping setCode(String code) {
        this.code = code;
        return this;
    }
}
