package com.github.skillstree.core.transformer;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.skillstree.core.model.Skill;
import com.github.skillstree.core.model.SkillsTree;
import com.github.skillstree.core.model.SkillsTreeJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms skills to JSON tree structure supported by the website skills tree representation.
 */
public class SkillsToJsonTreeTransformer {

    private static final Logger logger = LoggerFactory.getLogger(SkillsToJsonTreeTransformer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SkillsToJsonTreeTransformer() {
    }

    /**
     * Transforms skills tree into JSON string.
     * @param skillsTree {@link SkillsTree}
     * @return JSON representation of the specified skills tree
     * @throws IOException if the problem with transformation occurred
     */
    public String transform(SkillsTree skillsTree) throws IOException {
        logger.debug("Transformation of {} is requested", skillsTree);

        Skill rootSkill = skillsTree.getRootSkill();
        Map<UUID, Skill> skills = skillsTree.getSkills();

        SkillsTreeJson skillsTreeJson = new SkillsTreeJson()
                .setChart(new SkillsTreeJson.Chart()
                        .setContainer("#skills-tree")
                        .setRootOrientation("WEST")
                        .setHideRootNode(true)
                        .setConnectors(new SkillsTreeJson.Chart.Connectors().setType("bCurve")));

        SkillsTreeJson.NodeStructure rootNodeStructure = new SkillsTreeJson.NodeStructure();
        skillsTreeJson.setNodeStructure(rootNodeStructure);

        rootNodeStructure.setText(new SkillsTreeJson.NodeStructure.Text()
                .setName(rootSkill.getName()));
        rootSkill.getChildren().forEach(id -> parseChildren(rootNodeStructure, skills.get(id), skillsTree));

        String result = objectMapper.writeValueAsString(skillsTreeJson);
        logger.debug("Skills tree is transformed to json: {}", result);

        return result;
    }

    private void parseChildren(SkillsTreeJson.NodeStructure parent, Skill skill, SkillsTree skillsTree) {
        SkillsTreeJson.NodeStructure child = new SkillsTreeJson.NodeStructure()
                .setHtmlId(skill.getId().toString())
                .setInnerHtml("<div onclick='obtainSkill(\"" + skill.getId() + "\", 1)'>" + skill.getName() + "</div>");
        parent.addChild(child);

        skill.getChildren().forEach(id -> parseChildren(child, skillsTree.getSkills().get(id), skillsTree));
    }
}
