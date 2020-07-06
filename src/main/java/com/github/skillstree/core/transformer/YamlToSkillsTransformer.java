package com.github.skillstree.core.transformer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.skillstree.core.model.Skill;
import com.github.skillstree.core.model.SkillsTree;

/**
 * Transforms YAML declaration of skills tree to skills objects.
 */
public class YamlToSkillsTransformer {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private final Map<String, UUID> persistedSkills;

    public YamlToSkillsTransformer(Map<String, UUID> persistedSkills) {
        this.persistedSkills = persistedSkills;
    }

    /**
     * Parses the directory with skills files and creates the corresponding {@link SkillsTree}.
     * @param baseDir directory with skills files
     * @return {@link SkillsTree}
     * @throws IOException if problem with files occurred
     */
    public SkillsTree transform(File baseDir) throws IOException {
        if (!baseDir.isDirectory()) {
            throw new IllegalArgumentException("Skills set directory should be a directory");
        }
        final Map<UUID, Skill> skills = new HashMap<>();
        Skill mainSkill = parseSkills("", baseDir, skills);

        skills.forEach((s, skill) -> System.out.println(skill));

        return new SkillsTree()
                .setRootSkill(mainSkill)
                .setSkills(skills);
    }

    public SkillsTree transform(File baseDir, List<String> changedFiles) {
        return null; //TODO implement
    }

    private Skill parseSkills(String parentPath, File baseDir, Map<UUID, Skill> skills) throws IOException {
        Skill rootSkill = findRootSkill(parentPath, baseDir);
        skills.put(rootSkill.getId(), rootSkill);

        File[] files = baseDir.listFiles((dir, name) -> !name.startsWith(baseDir.getName()));
        if (files != null && files.length != 0) {
            for (File f : files) {
                Skill skill;
                if (f.isDirectory()) {
                    skill = parseSkills(parentPath + (parentPath.isEmpty() ? "" : "/") +
                            baseDir.getName(), f, skills);
                } else {
                    skill = mapper.readValue(Files.readString(f.toPath()), Skill.class);
                    skill.setCode(parentPath + (parentPath.isEmpty() ? "" : "/") +
                            baseDir.getName() + "/" + f.getName());
                    skill.setId(persistedSkills.get(skill.getCode()) == null ? UUID.randomUUID() :
                            persistedSkills.get(skill.getCode()));
                    skills.put(skill.getId(), skill);
                }
                rootSkill.addChild(skill.getId());
            }
        }
        return rootSkill;
    }

    private Skill findRootSkill(String parentPath, File baseDir) throws IOException {
        File[] files = baseDir.listFiles((dir, name) -> name.startsWith(baseDir.getName().toLowerCase()) &&
                (name.endsWith(".yaml") || name.endsWith(".yml")));

        Objects.requireNonNull(files, "There is no yaml file found for the root skill " + baseDir.getName());

        Skill rootSkill = mapper.readValue(Files.readString(files[0].toPath()), Skill.class);
        rootSkill.setId(persistedSkills.get(rootSkill.getCode()) == null ? UUID.randomUUID() :
                persistedSkills.get(rootSkill.getCode()));
        rootSkill.setCode(parentPath + (parentPath.isEmpty() ? "" : "/") +
                baseDir.getName() + "/" + files[0].getName());

        return rootSkill;
    }
}
