package com.github.skillstree.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Skill {

    private UUID id;

    private String code;

    private String name;

    @JsonProperty("full-name")
    private String fullName;

    private String description;

    private List<UUID> children = new ArrayList<UUID>();

    public Skill() {
    }

    public UUID getId() {
        return id;
    }

    public Skill setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Skill setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Skill setName(String name) {
        this.name = name;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public Skill setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Skill setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<UUID> getChildren() {
        return children;
    }

    public Skill setChildren(List<UUID> children) {
        this.children = children;
        return this;
    }

    public Skill addChild(UUID skillId) {
        this.children.add(skillId);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(name, skill.name) &&
                Objects.equals(description, skill.description) &&
                Objects.equals(children, skill.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, children);
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id='" + id + '\'' +
                "code='" + code + '\'' +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
