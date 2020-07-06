package com.github.skillstree.core;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.github.skillstree.core.model.SkillsIdMapping;
import com.github.skillstree.core.model.SkillsTree;
import com.github.skillstree.core.service.GitExtractor;
import com.github.skillstree.core.service.PersistenceService;
import com.github.skillstree.core.service.WebSiteConstructor;
import com.github.skillstree.core.transformer.SkillsToJsonTreeTransformer;
import com.github.skillstree.core.transformer.YamlToSkillsTransformer;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillsUpdater {

    private static final Logger logger = LoggerFactory.getLogger(SkillsUpdater.class);

    private static final List<String> REPO_FILES_TO_IGNORE =
            Collections.unmodifiableList(List.of(".git", ".gitignore"));

    private final PersistenceService persistence;

    private final GitExtractor gitExtractor;

    private final SkillsToJsonTreeTransformer skillsToJsonTransformer;

    private final WebSiteConstructor webSiteConstructor;

    private final Map<String, SkillsTree> skillsTrees = new HashMap<>();

    private Map<String, UUID> persistedSkills;

    public SkillsUpdater(WebSiteConstructor webSiteConstructor) {
        this(webSiteConstructor, new PersistenceService(), new SkillsToJsonTreeTransformer());
    }

    /**
     * Constructor.
     * @param webSiteConstructor      web site constructor
     * @param persistenceService      persistence service
     * @param skillsToJsonTransformer skills to json transformer
     */
    public SkillsUpdater(WebSiteConstructor webSiteConstructor, PersistenceService persistenceService,
                         SkillsToJsonTreeTransformer skillsToJsonTransformer) {

        this.webSiteConstructor = webSiteConstructor;
        this.persistence = persistenceService;
        this.skillsToJsonTransformer = skillsToJsonTransformer;

        File repoDir = new File(new File(System.getProperty("java.io.tmpdir"))
                .getAbsolutePath() + File.separator + "skills");
        this.gitExtractor = new GitExtractor(repoDir);
    }

    /**
     * Retrieves all previously persisted skills mappings.
     */
    public void pullPreviouslyPersistedSkills() {
        persistence.initTables();
        persistedSkills = persistence.getAllMappings();
        logger.debug("Previously persisted skills: {}", persistedSkills);
    }

    /**
     * Retrieves skills trees.
     * @throws GitAPIException if a problem with the skills repository occurred
     * @throws IOException     if a problem in work with files occurred
     */
    public void retrieveSkillsTrees() throws GitAPIException, IOException {
        File skillsDir;
        skillsDir = gitExtractor.getDirWithSkills();

        File[] mainSkillDirs = skillsDir.listFiles(f ->
                f.isDirectory() && !REPO_FILES_TO_IGNORE.contains(f.getName()));
        Objects.requireNonNull(mainSkillDirs, "No root skills are found in the skills directory");

        for (File baseDir : mainSkillDirs) {
            logger.info("Parsing base directory for a root skill: " + baseDir.getName());

            YamlToSkillsTransformer tr = new YamlToSkillsTransformer(persistedSkills);
            SkillsTree skillsTree = tr.transform(baseDir);

            skillsTrees.put(skillsTree.getRootSkill().getCode(), skillsTree);
        }
    }

    /**
     * Updates skills in the persistence layer from the changes in skills tree repository.
     * @throws IOException if a problem with skills transformation to JSON occurred
     */
    public void updateSkills() throws IOException {
        for (SkillsTree skillsTree : skillsTrees.values()) {
            String skillsTreeJson = skillsToJsonTransformer.transform(skillsTree);
            webSiteConstructor.updateSkillsTree(skillsTreeJson);

            skillsTree.getSkills().forEach((id, skill) ->
                    persistence.save(new SkillsIdMapping(skill.getId(), skill.getCode())));
            persistence.save(gitExtractor.getLastCommitId());
            logger.info("Skills to id mappings have been persisted");
        }
    }
}
